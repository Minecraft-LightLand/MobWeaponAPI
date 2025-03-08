package dev.xkmc.mob_weapon_api.api.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public interface IWeaponGoalFactory<E extends Mob, T extends Goal & IWeaponGoal<E>> {

	T create(E golem, IMeleeGoal melee);

}
