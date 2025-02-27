package dev.xkmc.mob_weapon_api.api.simple;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IInstantWeaponBehavior extends IWeaponWithCD {

	double range(ProjectileWeaponUser user, ItemStack stack);

	int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target);

}
