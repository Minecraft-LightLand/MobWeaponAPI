package dev.xkmc.mob_weapon_api.example.vanilla;

import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public record MeleeGoalWrapper(Mob mob, MeleeAttackGoal goal) implements IMeleeGoal {

	@Override
	public boolean canReachTarget(LivingEntity target) {
		return mob.isWithinMeleeAttackRange(target);
	}

	@Override
	public int getMeleeInterval() {
		return 20;
	}

}
