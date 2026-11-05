package dev.xkmc.mob_weapon_api.mixin;

import com.github.alexmodguy.alexscaves.server.entity.item.ExtinctionSpearEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 灭绝长矛（{@code ExtinctionSpearEntity}）的回收修正。
 * <p>
 * 原版 {@code tick()} 在落地后会朝 owner 飞回，而 {@code tryPickup(Player)} 只接受玩家拾取。
 * 傀儡不是 {@code Player}，于是长矛永远接不住、绕着傀儡无限转圈。
 * <p>
 * 这里在落地后若 owner 不是玩家就丢弃实体。{@code inGround} 是 {@code AbstractArrow} 的
 * protected 字段，mixin 编译期不可见，改用公共的 {@code isNoPhysics()} 与
 * {@code getDeltaMovement()} 近似判定"已停住"。
 */
@Pseudo
@Mixin(ExtinctionSpearEntity.class)
public class ExtinctionSpearReturnMixin {

	@Inject(method = "tick", at = @At("TAIL"), require = 0)
	private void mobWeaponAPI$dropForNonPlayerOwner(CallbackInfo ci) {
		ExtinctionSpearEntity self = (ExtinctionSpearEntity) (Object) this;
		if (self.level().isClientSide) return;
		Entity owner = self.getOwner();
		if (owner instanceof Player) return;
		// 原版落地后会 setNoPhysics(true) 并朝 owner 飞；此时直接丢弃，避免无限绕圈
		if (self.isNoPhysics() && self.getDeltaMovement().lengthSqr() < 1.0) {
			self.discard();
		}
	}

}
