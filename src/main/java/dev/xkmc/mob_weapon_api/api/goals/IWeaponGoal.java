package dev.xkmc.mob_weapon_api.api.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public interface IWeaponGoal<E extends Mob> {

	/**
	 * Returns true if golem can switch to this goal
	 * */
	default boolean mayActivate(ItemStack stack) {
		return true;
	}

	/**
	 * Returns true when this ranged goal can be switched to after golem is far enough from target.
	 * */
	default boolean isAvailable(ItemStack stack) {
		return mayActivate(stack);
	}

	double range(ItemStack stack);

	/**
	 * Return true if golem can use stack associated with this goal for melee attack.
	 * */
	default boolean shouldUseForMelee(ItemStack other) {
		return true;
	}

}
