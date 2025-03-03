package dev.xkmc.mob_weapon_api.api.simple;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.init.MobWeaponAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IWeaponWithCD {

	default void setCD(LivingEntity user, ItemStack stack, int time) {
		long next = user.level().getGameTime() + time;
		MobWeaponAPI.TIMESTAMP.set(stack, next);
	}

	default boolean isValid(ProjectileWeaponUser user, ItemStack stack) {
		long current = user.user().level().getGameTime();
		return current >= MobWeaponAPI.TIMESTAMP.getOrDefault(stack, 0L);
	}

}
