package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SoulRenderBehavior implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 6;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		int cd = CataclysmProxy.spawnHalberd(user.user());
		setCD(user.user(), stack, cd);
		return 10;
	}

}
