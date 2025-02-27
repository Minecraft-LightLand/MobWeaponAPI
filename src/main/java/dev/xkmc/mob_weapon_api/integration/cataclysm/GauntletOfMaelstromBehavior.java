package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GauntletOfMaelstromBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 25;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 10;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		int cd = CataclysmProxy.spawnVortex(user.user(), target.position());
		setCD(user.user(), stack, cd);
		return 20;
	}


}
