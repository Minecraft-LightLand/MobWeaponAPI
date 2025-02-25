package dev.xkmc.mob_weapon_api.registry;

import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface RangedStatusPredicate {

	Optional<WeaponStatus> getProperties(ItemStack stack);

}
