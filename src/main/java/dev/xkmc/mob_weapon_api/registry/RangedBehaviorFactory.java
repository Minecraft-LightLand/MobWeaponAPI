package dev.xkmc.mob_weapon_api.registry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface RangedBehaviorFactory<T> {

	T create(LivingEntity golem, ItemStack stack);

}
