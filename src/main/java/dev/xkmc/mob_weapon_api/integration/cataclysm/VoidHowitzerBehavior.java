package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IWeaponWithCD;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class VoidHowitzerBehavior implements IBowBehavior, IWeaponWithCD {

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		return isValid(user, stack);
	}

	@Override
	public int shootArrow(BowUseContext user, float power, ItemStack stack, InteractionHand hand) {
		var proj = CataclysmProxy.shootVoid(user.user());
		if (proj == null) return 20;
		user.aim(proj.proj().position(), proj.speed(), proj.gravity(), user.getInitialInaccuracy()).shoot(proj.proj(), 0);
		user.user().level().addFreshEntity(proj.proj());
		setCD(user.user(), stack, proj.cooldown());
		return 10;
	}

	@Override
	public int getStandardPullTime(BowUseContext user, ItemStack stack) {
		return 12;
	}

	@Override
	public float getPowerForTime(BowUseContext user, ItemStack stack, int pullTime) {
		return 1;
	}

}
