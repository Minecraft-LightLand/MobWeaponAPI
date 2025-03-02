package dev.xkmc.mob_weapon_api.api.simple;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IInfiniteHoldBehavior extends IHoldWeaponBehavior{

	@Override
	default boolean infiniteUse(LivingEntity user, ItemStack stack) {
		return true;
	}

	@Override
	default int holdTime(LivingEntity user, ItemStack stack) {
		return 10000;
	}
}
