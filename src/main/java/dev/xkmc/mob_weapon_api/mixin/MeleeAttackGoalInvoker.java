package dev.xkmc.mob_weapon_api.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 访问 {@code MeleeAttackGoal#getAttackReachSqr}（protected）。
 * <p>
 * 以前用 access transformer 把它提成 public，但那会让 vanilla 的
 * {@code Spider}/{@code PolarBear}/{@code Rabbit}/{@code Ravager}/{@code Vindicator} 里
 * 声明为 protected 的覆写变成「降低可见性」，ForgeGradle 的 {@code findRecomp} 必然失败。
 * {@code @Invoker} 在运行时生成调用，不需要改动任何可见性。
 */
@Mixin(MeleeAttackGoal.class)
public interface MeleeAttackGoalInvoker {

	@Invoker("getAttackReachSqr")
	double mobWeaponAPI$getAttackReachSqr(LivingEntity target);

}
