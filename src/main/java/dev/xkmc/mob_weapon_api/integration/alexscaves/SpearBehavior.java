package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.example.behavior.ThrowableBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

/**
 * 亚历克斯洞穴的三把投掷长矛：石灰岩长矛、灭绝长矛、霜薄荷长矛。
 * <p>
 * 原版走三叉戟式攻击：{@code use()} 开始蓄力，{@code releaseUsing()} 按蓄力强度投出对应的
 * {@code *SpearEntity}。三个 {@code releaseUsing} 都只接受 {@code Player}，傀儡拿不到玩家实例，
 * 所以这里复用本库已有的 {@link ThrowableBehavior}（{@code TridentBehavior} 的基类）。
 * <p>
 * 灭绝长矛的 {@code ExtinctionSpearEntity} 落地后会朝 owner 飞回，但 {@code tryPickup(Player)}
 * 只接受玩家拾取——傀儡接不住，于是它会绕着傀儡转圈永远不消失。这里在落地后直接 {@code discard()}。
 */
public class SpearBehavior extends ThrowableBehavior {

	public enum Kind {
		LIMESTONE(ACSoundRegistry.LIMESTONE_SPEAR_THROW),
		EXTINCTION(ACSoundRegistry.EXTINCTION_SPEAR_THROW),
		FROSTMINT(ACSoundRegistry.FROSTMINT_SPEAR_THROW);

		private final RegistryObject<SoundEvent> sound;

		Kind(RegistryObject<SoundEvent> sound) {
			this.sound = sound;
		}
	}

	private final Kind kind;

	public SpearBehavior(Kind kind) {
		this.kind = kind;
	}

	@Override
	public float getSpeed(ItemStack stack, Projectile proj) {
		return kind == Kind.EXTINCTION ? 3.5F : 2.5F;
	}

	/**
	 * 灭绝长矛蓄力时会召唤一圈护盾之灵（原版 {@code onUseTick} 在刚开始使用时触发一次）。
	 * 傀儡的使用状态由 AI 驱动，这里在每次开始"使用"的第一 tick 复刻同样的召唤。
	 */
	@Override
	public void tickUsing(ProjectileWeaponUser user, ItemStack stack, int time) {
		if (kind != Kind.EXTINCTION) return;
		// 原版 onUseTick 的触发条件是 timeUsing == getUseDuration(stack)，
		// 也就是"刚开始使用"的那一 tick；这里的 time 是已用 tick 数（从 0 起），所以取 0。
		if (time != 0) return;
		var golem = user.user();
		var level = golem.level();
		if (level.isClientSide) return;
		level.playSound(null, golem, com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry.EXTINCTION_SPEAR_SUMMON.get(),
				net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
		int heads = 3 + stack.getEnchantmentLevel(com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry.HERD_PHALANX.get());
		float rotateBy = 360F / heads;
		for (int i = 0; i < heads; i++) {
			var spirit = com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry.DINOSAUR_SPIRIT.get().create(level);
			if (spirit == null) break;
			spirit.copyPosition(golem);
			spirit.setDinosaurType(com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity.DinosaurType.GROTTOCERATOPS);
			spirit.setPlayerUUID(golem.getUUID());
			spirit.setRotateOffset(i * rotateBy);
			level.addFreshEntity(spirit);
		}
	}

	@Override
	protected Projectile getProjectile(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		var golem = user.user();
		var level = golem.level();
		AbstractArrow spear = switch (kind) {
			case LIMESTONE -> new com.github.alexmodguy.alexscaves.server.entity.item.LimestoneSpearEntity(level, golem, stack);
			case EXTINCTION -> new com.github.alexmodguy.alexscaves.server.entity.item.ExtinctionSpearEntity(level, golem, stack);
			case FROSTMINT -> new com.github.alexmodguy.alexscaves.server.entity.item.FrostmintSpearEntity(level, golem, stack);
		};
		spear.pickup = AbstractArrow.Pickup.DISALLOWED;
		level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				kind.sound.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		return spear;
	}

}
