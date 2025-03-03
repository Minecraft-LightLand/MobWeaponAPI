package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.RechargeableInstantBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class LaserGatlingBehavior extends RechargeableInstantBehavior {

	public LaserGatlingBehavior() {
		super(1, CataclysmIntegration.LASER_GATLING);
	}

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 25;
	}

	@Override
	protected void triggerImpl(ProjectileWeaponUser ctx, ItemStack stack, LivingEntity target) {
		CataclysmProxy.shootLaserGatling(ctx.user(), target.getEyePosition().subtract(ctx.user().getEyePosition()).normalize());
		if (!ctx.bypassAllConsumption())
			stack.hurtAndBreak(1, ctx.user(), EquipmentSlot.MAINHAND);
	}

}
