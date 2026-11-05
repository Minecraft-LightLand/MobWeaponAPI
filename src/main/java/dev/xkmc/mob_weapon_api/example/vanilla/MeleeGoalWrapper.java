package dev.xkmc.mob_weapon_api.example.vanilla;

import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.mixin.MeleeAttackGoalInvoker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public record MeleeGoalWrapper(Mob mob, MeleeAttackGoal goal) implements IMeleeGoal {

	@Override
	public boolean canReachTarget(LivingEntity target) {
		// getAttackReachSqr 是 protected，且子类会覆写它（各 boss 的攻击距离不同），
		// 所以既不能直接调，也不能自己重算——用 @Invoker 走虚调用。
		return ((MeleeAttackGoalInvoker) goal).mobWeaponAPI$getAttackReachSqr(target) >= mob.distanceToSqr(target);
	}

	@Override
	public int getMeleeInterval() {
		return 20;
	}

}
