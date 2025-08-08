package dev.xkmc.mob_weapon_api.example.goal;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class SmartInstantRangedAttackGoal<E extends Mob> extends SmartRangedAttackGoal<E> {

	private int attackTime = -1;

	public SmartInstantRangedAttackGoal(E mob, IMeleeGoal melee, double speed) {
		super(mob, mob instanceof IWeaponHolder h ? h : IWeaponHolder.simple(mob), melee, speed, 0);
	}

	public SmartInstantRangedAttackGoal(E mob, IWeaponHolder holder, IMeleeGoal melee, double speed, double radius) {
		super(mob, holder, melee, speed, radius);
	}

	@Override
	public boolean mayActivate(ItemStack stack) {
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		return weapon.isPresent() && weapon.get().isValid(holder.toUser(), stack);
	}

	@Override
	public Optional<WeaponStatus> getWeaponStatus(ItemStack stack) {
		return WeaponRegistry.INSTANT.getProperties(stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}

	@Override
	public double range(ItemStack stack) {
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		return weapon.map(b -> b.range(holder.toUser(), stack)).orElse(0.0);
	}

	public void tick() {
		doMelee();
		strafing();
		if (attackTime > 0) {
			attackTime--;
			return;
		}
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		if (weapon.isEmpty()) return;
		LivingEntity target = mob.getTarget();
		var user = holder.toUser();
		if (seeTime > 0 && target != null &&
				mob.distanceTo(target) < weapon.get().range(user, stack)) {
			attackTime = weapon.get().trigger(user, stack, target);
		}
	}


	@Override
	public void performRangedAttack(LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
	}

}
