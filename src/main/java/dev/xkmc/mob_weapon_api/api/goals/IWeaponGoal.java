package dev.xkmc.mob_weapon_api.api.goals;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public interface IWeaponGoal<E extends Mob & IWeaponHolder> {

	default boolean mayActivate(ItemStack stack) {
		return true;
	}

	double range(ItemStack stack);

}
