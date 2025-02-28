package dev.xkmc.mob_weapon_api.api.simple;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.util.DummyProjectileWeapon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public abstract class RechargeableInstantBehavior implements IInstantWeaponBehavior {

	private final int minDurability;
	private final Supplier<DummyProjectileWeapon> predicate;

	protected RechargeableInstantBehavior(int minDurability, Supplier<DummyProjectileWeapon> predicate) {
		this.minDurability = minDurability;
		this.predicate = predicate;
	}

	@Override
	public boolean isValid(ProjectileWeaponUser user, ItemStack stack) {
		if (!IInstantWeaponBehavior.super.isValid(user, stack))
			return false;
		if (user.bypassAllConsumption())
			return true;
		if (stack.getDamageValue() < stack.getMaxDamage() - minDurability)
			return true;
		return !user.getPreferredProjectile(predicate.get().getDefaultInstance()).isEmpty();
	}

	private boolean checkAmmo(ProjectileWeaponUser user, ItemStack stack) {
		if (stack.getDamageValue() >= stack.getMaxDamage() - minDurability) {
			if (!user.bypassAllConsumption()) {
				ItemStack ammo = user.getPreferredProjectile(predicate.get().getDefaultInstance());
				if (ammo.isEmpty()) return false;
				ammo.shrink(1);
			}
			stack.setDamageValue(0);
		}
		return true;
	}

	@Override
	public int trigger(ProjectileWeaponUser ctx, ItemStack stack, LivingEntity target) {
		if (!checkAmmo(ctx, stack)) return 20;
		triggerImpl(ctx, stack, target);
		checkAmmo(ctx, stack);
		return 10;
	}

	protected abstract void triggerImpl(ProjectileWeaponUser ctx, ItemStack stack, LivingEntity target);

}
