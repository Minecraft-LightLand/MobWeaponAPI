package dev.xkmc.mob_weapon_api.example.vanilla;

import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class SimpleUser implements IWeaponHolder, ISmartUser {

	protected final Mob mob;

	public SimpleUser(Mob mob) {
		this.mob = mob;
	}

	@Override
	public float getInitialVelocityFactor() {
		return 3;
	}

	@Override
	public float getInitialInaccuracy() {
		return 1;
	}

	@Override
	public InteractionHand getWeaponHand() {
		return InteractionHand.MAIN_HAND;
	}

	@Override
	public void setInRangeAttack(boolean b) {

	}

	@Override
	public ISmartUser toUser() {
		return this;
	}

	@Override
	public Vec3 viewVector() {
		var target = mob.getTarget();
		return target == null ? mob.getViewVector(0) : target.getEyePosition().subtract(mob.getEyePosition()).normalize();
	}

	@Override
	public AimResult aim(Vec3 arrowOrigin, float velocity, float gravity, float inaccuracy) {
		var target = mob.getTarget();
		if (target == null) {
			var vec = mob.getViewVector(0);
			return (e, a) -> shoot(vec, a, e, velocity, inaccuracy);
		}
		double dx = target.getX() - mob.getX();
		double dz = target.getZ() - mob.getZ();
		double dy = target.getY(1 / 3d) - arrowOrigin.y() + Math.sqrt(dx * dx + dz * dz) * gravity / velocity * 6;
		Vec3 vec = new Vec3(dx, dy, dz);
		return (e, a) -> shoot(vec, a, e, velocity, inaccuracy);
	}

	private void shoot(Vec3 vec, float a, Projectile e, float v, float i) {
		vec = vec.yRot(a * Mth.DEG_TO_RAD);
		e.shoot(vec.x, vec.y, vec.z, v, i);
	}

	@Override
	public LivingEntity user() {
		return mob;
	}

	@Override
	public ItemStack getPreferredProjectile(ItemStack weapon, Predicate<ItemStack> special, Predicate<ItemStack> general) {
		return Items.ARROW.getDefaultInstance();
	}

	@Override
	public boolean bypassAllConsumption() {
		return true;
	}

	@Override
	public boolean hasInfiniteArrow(ItemStack weapon, ItemStack ammo) {
		return true;
	}

}
