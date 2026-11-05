package dev.xkmc.mob_weapon_api.mixin;

import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import dev.xkmc.mob_weapon_api.integration.alexscaves.SpiritOwnerProxy;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让 {@code DinosaurSpiritEntity}（灭绝长矛的三种龙灵）能被傀儡驱动。
 * <p>
 * 原版 {@code getUsingPlayer()} 只查玩家列表，傀儡返回 null → 之灵立刻 {@code setFading(true)}，
 * 于是"蓄力的护盾之灵（GROTTOCERATOPS）"和"命中的拖拽之灵（SUBTERRANODON）"都只闪一下就没了。
 * 这里直接改 {@code getUsingPlayer()} 本身而不是某个调用点：{@code tick} 和
 * {@code DinosaurSpiritRenderer} 都会调它，只改一处会让渲染侧的朝向计算拿不到 owner
 * （渲染器用 {@code playerPos - dinoPos} 实现"潮汐锁定"，owner 为 null 时龙灵只会平移不会转）。
 */
@Pseudo
@Mixin(DinosaurSpiritEntity.class)
public class DinosaurSpiritOwnerMixin {

	@Inject(method = "getUsingPlayer", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
	private void mobWeaponAPI$resolveOwner(CallbackInfoReturnable<Player> cir) {
		Player resolved = SpiritOwnerProxy.resolve((DinosaurSpiritEntity) (Object) this);
		if (resolved != null) cir.setReturnValue(resolved);
	}

	/**
	 * {@code tickTremorsaurus} 用 owner 构造伤害源。代理 {@code Player} 一旦被别的 mod 当成真玩家，
	 * 就会在 {@code LivingDeathEvent} 里强转 {@code ServerPlayer} 崩服。这里换成真实 owner。
	 */
	@Redirect(method = "tickTremorsaurus", remap = false, require = 0,
			at = @At(value = "INVOKE",
					target = "Lcom/github/alexmodguy/alexscaves/server/misc/ACDamageTypes;causeSpiritDinosaurDamage(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;"))
	private DamageSource mobWeaponAPI$damageSource(RegistryAccess registryAccess, Entity source) {
		return ACDamageTypes.causeSpiritDinosaurDamage(registryAccess, SpiritOwnerProxy.unwrap(source));
	}

}
