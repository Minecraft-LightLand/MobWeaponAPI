package dev.xkmc.mob_weapon_api.mixin;

import com.github.alexmodguy.alexscaves.server.entity.item.CandyCaneHookEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 拐杖糖钩锁（{@code CandyCaneHookEntity}）的"玩家仍握着钩锁"判定修正。
 * <p>
 * 原版 {@code isOwnerHoldingHook} 在 owner 不是 {@code Player} 时直接返回 {@code true}，
 * 于是傀儡一发射钩子，钩子在自己的 {@code tick} 里就把自己判成"已被收回"
 * （{@code setReeling(true)}），第 6 tick 起开始朝 owner 飞、一贴近就 {@code discard} ——
 * 表现为"钩锁几乎不飞出去就收回来了"。
 * <p>
 * 非玩家 owner 改为以钩子自身的 {@code reeling} 状态为准：只有 Behavior 侧主动
 * {@code setReeling(true)} 时才回收，其余时间正常飞行。
 */
@Pseudo
@Mixin(CandyCaneHookEntity.class)
public class CandyCaneHookOwnerMixin {

	@Inject(method = "isOwnerHoldingHook", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
	private void mobWeaponAPI$nonPlayerOwner(boolean ignoreReeling, CallbackInfoReturnable<Boolean> cir) {
		CandyCaneHookEntity self = (CandyCaneHookEntity) (Object) this;
		if (self.getOwner() instanceof Player) return;
		cir.setReturnValue(self.isReeling());
	}

}
