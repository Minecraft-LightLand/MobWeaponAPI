package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInfiniteHoldBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MeatShredderBehavior implements IInfiniteHoldBehavior {

	@Override
	public boolean canMelee() {
		return false;
	}

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 3;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		return 1;
	}

}
