package dev.xkmc.mob_weapon_api.api.goals;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public interface IWeaponGoalFactory<E extends Mob & IWeaponHolder, T extends Goal & IWeaponGoal<E>> {

	T create(E golem, IMeleeGoal melee);

}
