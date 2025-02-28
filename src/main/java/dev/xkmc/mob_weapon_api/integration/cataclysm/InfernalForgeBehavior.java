package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class InfernalForgeBehavior implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 5;
	}

	@Override
	public int trigger(ProjectileWeaponUser ctx, ItemStack stack, LivingEntity target) {
		LivingEntity user = ctx.user();
		setCD(user, stack, CataclysmProxy.infernalForge(user, target));
		return 10;
	}

}
