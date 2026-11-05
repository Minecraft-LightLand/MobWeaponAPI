package dev.xkmc.mob_weapon_api.mixin;

import com.github.alexmodguy.alexscaves.server.entity.item.SpinningPeppermintEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 糖果权杖的旋转薄荷糖：让非玩家持有者（傀儡）也走和玩家一样的跟随 / 追踪。
 * <p>
 * 原版 {@code tick()} 对 owner 分两支：
 * <ul>
 *   <li>{@code Player}：空闲时绕着玩家转；{@code SEEKCANDY} 有目标时把 {@code spinRadius}
 *       从 4 收缩到 0，围绕点设成"自身前方 1 格"——于是先盘旋、半径收缩后直线扑向目标；</li>
 *   <li>{@code Mob}：<b>没有空闲跟随</b>，也没有 {@code SEEKCANDY} 分支。</li>
 * </ul>
 * <p>
 * 关键在于 {@code tick()} 开头那句 {@code setSpinRadius(3F)}：它在 {@code angle} 计算<b>之前</b>执行，
 * 所以只在 TAIL 里改半径是没用的（下一 tick 又被 3F 覆盖，盘旋范围反而越来越大）。
 * 必须 {@code @Redirect} 掉那次调用本身。
 */
@Pseudo
@Mixin(SpinningPeppermintEntity.class)
public class SpinningPeppermintFollowMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", remap = false,
			target = "Lcom/github/alexmodguy/alexscaves/server/entity/item/SpinningPeppermintEntity;setSpinRadius(F)V"),
			require = 0)
	private void mobWeaponAPI$spinRadius(SpinningPeppermintEntity self, float radius) {
		if (self.getOwner() instanceof Mob && self.getSeekingEntityId() != -1) {
			// 与 Player 分支一致：半径从 4 收缩到 0，前 30 tick 盘旋、之后直线扑向目标
			self.setSpinRadius(4.0F - 4.0F * Math.min(1F, self.tickCount / 30F));
			return;
		}
		self.setSpinRadius(radius);
	}

	@Inject(method = "tick", at = @At("TAIL"), require = 0)
	private void mobWeaponAPI$followMobOwner(CallbackInfo ci) {
		SpinningPeppermintEntity self = (SpinningPeppermintEntity) (Object) this;
		if (self.level().isClientSide) return;
		if (!(self.getOwner() instanceof Mob mob)) return;
		int seekId = self.getSeekingEntityId();
		Entity seeking = seekId == -1 ? null : self.level().getEntity(seekId);
		if (seeking != null && !seeking.isAlive()) seeking = null;
		if (seeking == null) {
			// 原目标死了 / 被移除：改追傀儡当前锁定的目标，而不是直接瞬移回傀儡身边。
			// 原版玩家版每个糖果只认召唤时那一个目标，打死后就失效；傀儡这边让它接力，
			// 否则剩下的糖果会突然"瞬移"到傀儡身上——看起来像 bug。
			Entity next = mob.getTarget();
			if (next != null && next.isAlive()) {
				self.setSeekingEntityId(next.getId());
				seeking = next;
			}
		}
		if (seeking != null) {
			// 围绕点 = 自身位置 + 朝目标方向 1 格（固定长度，匀速而不是指数逼近）
			Vec3 add = seeking.getEyePosition().subtract(self.position());
			if (add.length() > 1.0F) add = add.normalize();
			self.setSpinAroundPosition(self.position().add(add));
		} else {
			// 空闲：围绕 owner 旋转（原版 Mob 分支缺的就是这一条）
			self.setSpinAroundPosition(mob.position().add(0, mob.getBbHeight() * 0.45F, 0));
		}
	}

}
