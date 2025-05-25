package dev.xkmc.mob_weapon_api.example.behavior;

import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.mob_weapon_api.api.projectile.CrossbowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUseContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeneralCrossbowBehavior extends SimpleCrossbowBehavior {

	@Override
	public int performRangedAttack(CrossbowUseContext user, ItemStack stack, InteractionHand hand) {
		performShooting(user, hand, stack, user.getCrossbowVelocity(getLoadedProjectile(user.user(), stack)), user.getInitialInaccuracy());
		return 0;
	}

	protected void performShooting(CrossbowUseContext user, InteractionHand hand, ItemStack stack, float velocity, float inaccuracy) {
		List<ItemStack> list = getLoadedProjectile(user.user(), stack);
		float spread = EnchantmentHelper.processProjectileSpread((ServerLevel) user.user().level(), stack, user.user(), 0);
		float step = list.size() == 1 ? 0f : 2f * spread / (list.size() - 1);
		float start = (float) ((list.size() - 1) % 2) * step / 2f;
		int sign = 1;

		ProjectileWeaponUseContext.AimResult aim = null;
		for (int i = 0; i < list.size(); ++i) {
			float angle = start + sign * (i + 1) / 2f * step;
			sign = -sign;
			ItemStack ammo = list.get(i);
			boolean creative = user.hasInfiniteArrow(stack, ammo);
			if (!ammo.isEmpty()) {
				float pitch = angle == 0 ? 1 : getRandomShotPitch(i % 2 == 0, user.user().getRandom());
				aim = shootProjectile(user, aim, hand, stack, ammo, pitch, creative, velocity, inaccuracy, angle);
			}
		}
		stack.remove(DataComponents.CHARGED_PROJECTILES);
	}

	private static ProjectileWeaponUseContext.AimResult shootProjectile(
			CrossbowUseContext ctx, @Nullable ProjectileWeaponUseContext.AimResult aim, InteractionHand hand, ItemStack stack, ItemStack ammo,
			float rand, boolean infinite, float velocity, float inaccuracy, float angle) {
		var user = ctx.user();
		var level = user.level();
		boolean rocket = ammo.is(Items.FIREWORK_ROCKET);
		Projectile projectile;
		if (rocket) {
			projectile = new FireworkRocketEntity(level, ammo, user, user.getX(), user.getEyeY() - (double) 0.15F, user.getZ(), true);
		} else {
			projectile = getArrow(ctx, stack, ammo, velocity);
			if (infinite || angle != 0.0F) {
				((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			}
		}
		if (aim == null) {
			aim = ctx.aim(projectile.position(), velocity, rocket ? 0 : 0.05f, inaccuracy);
		}
		aim.shoot(projectile, angle);
		projectile.setDeltaMovement(projectile.getDeltaMovement().normalize().scale(velocity));
		if (!ctx.bypassAllConsumption()) {
			stack.hurtAndBreak(rocket ? 3 : 1, user, LivingEntity.getSlotForHand(hand));
		}
		level.addFreshEntity(projectile);
		level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, rand);
		return aim;
	}

	private static AbstractArrow getArrow(CrossbowUseContext user, ItemStack bow, ItemStack ammo, float velocity) {
		var ans = user.createArrow(ammo, velocity, bow);
		ans.setCritArrow(true);
		ans.setSoundEvent(SoundEvents.CROSSBOW_HIT);
		return ans;
	}

	private static float[] getShotPitches(RandomSource p_220024_) {
		boolean flag = p_220024_.nextBoolean();
		return new float[]{1.0F, getRandomShotPitch(flag, p_220024_), getRandomShotPitch(!flag, p_220024_)};
	}

	private static float getRandomShotPitch(boolean p_220026_, RandomSource p_220027_) {
		float f = p_220026_ ? 0.63F : 0.43F;
		return 1.0F / (p_220027_.nextFloat() * 0.5F + 1.8F) + f;
	}

}
