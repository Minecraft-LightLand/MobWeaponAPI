package dev.xkmc.mob_weapon_api.api.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public interface IMeleeGoal {

	default Goal asGoal() {
		return (Goal) this;
	}

	boolean canReachTarget(LivingEntity target);

	int adjustedTickDelay(int i);

}
