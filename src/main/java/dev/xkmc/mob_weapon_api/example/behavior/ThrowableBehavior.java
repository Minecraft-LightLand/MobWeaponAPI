package dev.xkmc.mob_weapon_api.example.behavior;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

public abstract class ThrowableBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 25;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 20;
	}

	public float getSpeed(ItemStack stack, Projectile proj) {
		return 3f;
	}

	public float getGravity(ItemStack stack, Projectile proj) {
		return 0.05f;
	}

	@Nullable
	protected abstract Projectile getProjectile(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time);

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		if (stack.getEnchantmentLevel(Enchantments.LOYALTY) > 0) {
			stack = stack.copy();
			var map = stack.getAllEnchantments();
			map.remove(Enchantments.LOYALTY);
			EnchantmentHelper.setEnchantments(map, stack);
		}

		Projectile proj = getProjectile(user, stack, target, time);
		if (proj == null) return 20;
		ShootUtils.shootAimHelper(target, proj, getSpeed(stack, proj), getGravity(stack, proj));
		user.user().playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F / (user.user().getRandom().nextFloat() * 0.4F + 0.8F));
		proj.getPersistentData().putInt("DespawnFactor", 20);
		user.user().level().addFreshEntity(proj);
		if (!user.bypassAllConsumption())
			stack.hurtAndBreak(1, user.user(), e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		return 5;
	}

	@Override
	public void tickUsing(ProjectileWeaponUser user, ItemStack stack, int time) {

	}

}
