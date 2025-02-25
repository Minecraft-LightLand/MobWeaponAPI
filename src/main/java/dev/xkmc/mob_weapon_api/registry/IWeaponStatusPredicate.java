package dev.xkmc.mob_weapon_api.registry;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IWeaponStatusPredicate {

	Optional<WeaponStatus> getProperties(LivingEntity golem, ItemStack weapon, @Nullable InteractionHand hand);

}
