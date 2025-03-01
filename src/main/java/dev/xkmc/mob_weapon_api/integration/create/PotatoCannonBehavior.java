package dev.xkmc.mob_weapon_api.integration.create;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PotatoCannonBehavior implements IBowBehavior {

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		try {
			return !user.getPreferredProjectile(stack).isEmpty();
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public int getStandardPullTime(BowUseContext user, ItemStack stack) {
		return 0;
	}

	@Override
	public float getPowerForTime(BowUseContext user, ItemStack stack, int pullTime) {
		return 0;
	}

	@Override
	public int shootArrow(BowUseContext user, float power, ItemStack stack, InteractionHand hand) {
		return CreateProxy.shootPotato(user, stack, hand);
	}

}
