package dev.xkmc.mob_weapon_api.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import dev.xkmc.mob_weapon_api.integration.alexscaves.GalenaGauntletBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 方铅护手：让傀儡使用时<b>不消耗</b>另一只手的武器。
 * <p>
 * 原版 {@code onUseTick} 会把另一只手的磁力武器 {@code setCount(0)} 抹掉，再生成一个
 * {@code MagneticWeaponEntity} 带着它的副本环绕。玩家能靠实体被 pickup 时收回武器，
 * 而 {@code tryPickup} 只接受 {@code Player}——傀儡接不住，武器就真的没了。
 * <p>
 * 这里对非玩家持有者整段接管：其余逻辑（音效、寻找已有环绕武器、生成实体）照抄原版，
 * 只把 {@code setCount(0)} 去掉，武器留在傀儡手上。环绕武器的控制器判定与自毁由
 * {@link MagneticWeaponGolemMixin} 负责。
 */
@Pseudo
@Mixin(GalenaGauntletItem.class)
public class GalenaGauntletGolemMixin {

	@Inject(method = "onUseTick", at = @At("HEAD"), remap = true, require = 0, cancellable = true)
	private void mobWeaponAPI$golemOnUseTick(Level level, LivingEntity living, ItemStack stack,
			int timeUsing, CallbackInfo ci) {
		if (living instanceof Player) return;
		ci.cancel();

		// 护手耐久：原版在 {@code GalenaGauntletItem#use} 里 {@code hurtAndBreak(1)} 一次（每次右键启动），
		// {@code onUseTick} 不再消耗。傀儡不走 {@code use()}（goal 直接 startUsingItem），
		// 所以这里在<b>使用第一 tick</b>补一次，语义与原版等价。
		// 用 {@code timeUsing} 判定第一 tick：原版 {@code getUseDuration}=72000，
		// 首次 tick 时 {@code timeUsing == 72000}。
		if (timeUsing >= stack.getUseDuration() - 1) {
			if (!(living instanceof Player p && p.isCreative()) && stack.isDamageableItem()) {
				stack.hurtAndBreak(1, living, e -> {
			});
			}
		}

		AlexsCaves.PROXY.playWorldSound(living, (byte) 11);
		// 用 findWeapon 而不是"另一只手的物品"：副手可能是盾牌，武器在副武器槽里
		ItemStack weapon = GalenaGauntletBehavior.findWeapon(living, stack);
		if (!weapon.isEmpty()) {
			boolean alreadyInUse = false;
			for (MagneticWeaponEntity existing : level.getEntitiesOfClass(MagneticWeaponEntity.class,
					living.getBoundingBox().inflate(64, 64, 64))) {
				Entity controller = existing.getController();
				if (controller != null && controller.is(living)) {
					alreadyInUse = true;
					break;
				}
			}
			if (!alreadyInUse) {
				MagneticWeaponEntity magneticWeapon = ACEntityRegistry.MAGNETIC_WEAPON.get().create(level);
				if (magneticWeapon != null) {
					// 只 copy、不清零：武器留在傀儡身上
					magneticWeapon.setItemStack(weapon.copy());
					magneticWeapon.setPos(living.position().add(0, 1, 0));
					magneticWeapon.setControllerUUID(living.getUUID());
					level.addFreshEntity(magneticWeapon);
				}
			}
		}
		// 没武器时不要在这里 stopUsingItem：goal 的 canUse 已经因为 hasAmmo=false 停下来了，
		// 两边同时抢"停止"会让使用状态每 tick 抖一次，循环音跟着抽搐。
	}

}
