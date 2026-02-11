package dev.xkmc.mob_weapon_api.integration.l2complements;

import dev.xkmc.l2complements.content.item.wand.BoreasScepter;
import dev.xkmc.l2complements.content.item.wand.WinterStormWand;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInfiniteHoldBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BoreasScepterBehavior implements IInfiniteHoldBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 12;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		return 1;
	}

	@Override
	public void tickUsing(ProjectileWeaponUser user, ItemStack stack, int time) {
		BoreasScepter.tickServer(user.user(), user.user().level(), user.user().position(), time);
		if (time % 20 == 0 && !user.bypassAllConsumption()) {
			stack.hurtAndBreak(1, user.user(), e -> e.broadcastBreakEvent(user.user().getUsedItemHand()));
		}
	}

}
