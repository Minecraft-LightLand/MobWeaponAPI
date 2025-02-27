package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class VoidForgeBehavior implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 7;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		int cd = CataclysmProxy.spawnVoidFangs(user.user(), target.position().subtract(user.user().position()).normalize());
		setCD(user.user(), stack, cd);
		return 20;
	}


}
