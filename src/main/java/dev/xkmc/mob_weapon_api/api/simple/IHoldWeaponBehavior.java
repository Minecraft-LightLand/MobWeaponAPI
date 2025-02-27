package dev.xkmc.mob_weapon_api.api.simple;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IHoldWeaponBehavior extends IWeaponWithCD {

	double range(LivingEntity user, ItemStack stack);

	int holdTime(LivingEntity user, ItemStack stack);

	int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time);

	default void tickUsing(ProjectileWeaponUser user, ItemStack stack, int time) {

	}

}
