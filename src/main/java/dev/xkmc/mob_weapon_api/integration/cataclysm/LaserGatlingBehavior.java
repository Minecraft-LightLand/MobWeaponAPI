package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record LaserGatlingBehavior(int interval) implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 25;
	}

	@Override
	public boolean isValid(ProjectileWeaponUser user, ItemStack stack) {
		if (user.bypassAllConsumption())
			return true;
		if (stack.getDamageValue() < stack.getMaxDamage() - 1)
			return true;
		return !user.getPreferredProjectile(CataclysmIntegration.LASER_GATLING.asStack()).isEmpty();
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		if (!checkAmmo(user, stack)) return 20;
		CataclysmProxy.shootLaserGatling(user.user(), target.getEyePosition().subtract(user.user().getEyePosition()).normalize());
		if (!user.bypassAllConsumption())
			stack.hurtAndBreak(1, user.user(), (e) -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		checkAmmo(user, stack);
		return 10;
	}

	private boolean checkAmmo(ProjectileWeaponUser user, ItemStack stack) {
		if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
			if (!user.bypassAllConsumption()) {
				ItemStack ammo = user.getPreferredProjectile(CataclysmIntegration.LASER_GATLING.asStack());
				if (ammo.isEmpty()) return false;
				ammo.shrink(1);
			}
			stack.setDamageValue(0);
		}
		return true;
	}

}
