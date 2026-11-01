package dev.xkmc.cataclysm_mux_0327.mixin;

import com.github.L_Ender.cataclysm.entity.projectile.Wither_Homing_Missile_Entity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 巡回导弹锁定的目标死亡后会原地爆炸（原版 tick 里 finalTarget 失效即爆）。
 * 这里在每 tick 开头检查：若当前目标失效，改取 owner（傀儡/Boss）的当前目标，
 * 让导弹重新锁定，避免白丢。对原版 Boss 弹也是正向行为。
 * 3.16 环境同名同逻辑同样命中，收敛向新行为。
 */
@Mixin(Wither_Homing_Missile_Entity.class)
public abstract class WitherHomingMissileRetargetMixin extends Projectile {

	@Shadow
	@Nullable
	private Entity finalTarget;

	@Shadow
	@Nullable
	private UUID targetId;

	public WitherHomingMissileRetargetMixin(EntityType<? extends Projectile> type, Level level) {
		super(type, level);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void cataclysm_mux_0327$retarget(CallbackInfo ci) {
		if (this.level().isClientSide) return;
		if (this.finalTarget != null && this.finalTarget.isAlive()) return;
		if (this.getOwner() instanceof Mob mob) {
			LivingEntity t = mob.getTarget();
			if (t != null && t.isAlive() && !(t instanceof Player p && p.isSpectator())) {
				this.finalTarget = t;
				this.targetId = null;
			}
		}
	}

}