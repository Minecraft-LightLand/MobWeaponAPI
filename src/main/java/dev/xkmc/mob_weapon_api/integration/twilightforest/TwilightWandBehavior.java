package dev.xkmc.mob_weapon_api.integration.twilightforest;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.RechargeableInstantBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import twilightforest.entity.projectile.TwilightWandBolt;
import twilightforest.util.TFItemStackUtils;

public class TwilightWandBehavior extends RechargeableInstantBehavior {

	protected TwilightWandBehavior() {
		super(0, TFIntegration.TWILIGHT_WAND);
	}

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 18;
	}

	@Override
	protected void triggerImpl(ProjectileWeaponUser ctx, ItemStack stack, LivingEntity target) {
		var e = ctx.user();
		e.swing(InteractionHand.MAIN_HAND);
		var level = e.level();
		var proj = new TwilightWandBolt(level, e);
		var vec = target.getEyePosition().subtract(proj.position()).normalize();
		proj.shoot(vec.x, vec.y, vec.z, 1.5f, 0);
		level.addFreshEntity(proj);
		if (!ctx.bypassAllConsumption()) {
			TFItemStackUtils.hurtButDontBreak(stack, 1, (ServerLevel) level, e);
		}
	}

}
