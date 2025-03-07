package dev.xkmc.mob_weapon_api.api.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public interface IWeaponGoal<E extends Mob> {

	default boolean mayActivate(ItemStack stack) {
		return true;
	}

	double range(ItemStack stack);

}
