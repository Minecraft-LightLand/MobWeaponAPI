package dev.xkmc.mob_weapon_api.integration.l2complements;

import dev.xkmc.l2complements.content.item.wand.SonicShooter;
import dev.xkmc.l2complements.init.registrate.LCItems;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SonicShooterBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return LCItems.SONIC_SHOOTER.get().getDistance(stack) - 2;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return LCItems.SONIC_SHOOTER.get().getUseDuration(stack) - 1;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		if (user.user().level() instanceof ServerLevel sl) {
			Vec3 dst = target.getEyePosition();
			Vec3 src = user.user().getEyePosition();
			Vec3 dir = dst.subtract(src).normalize();

			int dmg = stack.getDamageValue();
			if (user.bypassAllConsumption())
				stack.setDamageValue(0);
			SonicShooter.shoot(sl, user.user(), stack, src, dir);
			if (user.bypassAllConsumption())
				stack.setDamageValue(dmg);
		}
		return 5;
	}

}
