package dev.xkmc.mob_weapon_api.integration.l2complements;

import dev.xkmc.l2complements.content.item.wand.HeliosScepter;
import dev.xkmc.l2complements.content.item.wand.HellfireWand;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class HeliosScepterBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 25;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 20;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		if (user.user().level() instanceof ServerLevel sl) {
			int dmg = stack.getDamageValue();
			if (user.bypassAllConsumption())
				stack.setDamageValue(0);
			HeliosScepter.trigger(user.user(), sl, target.position(), time);
			if (user.bypassAllConsumption())
				stack.setDamageValue(dmg);
		}
		return 10;
	}

	@Override
	public void tickUsing(ProjectileWeaponUser user, ItemStack stack, int time) {
		var target = user.user() instanceof Mob mob ? mob.getTarget() : null;
		if (target == null) return;
		HeliosScepter.renderRegionServer(user.user(), target.position(), time);
	}

}
