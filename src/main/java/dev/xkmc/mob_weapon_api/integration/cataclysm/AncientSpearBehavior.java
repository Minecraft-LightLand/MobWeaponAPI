package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AncientSpearBehavior implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 25;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		CataclysmProxy.launchTornado(user.user(), target.getEyePosition().subtract(user.user().getEyePosition()).normalize());
		if (!user.bypassAllConsumption()) {
			stack.hurtAndBreak(1, user.user(), e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		}
		return 20;
	}

}
