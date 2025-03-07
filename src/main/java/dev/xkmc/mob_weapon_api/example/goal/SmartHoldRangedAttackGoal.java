package dev.xkmc.mob_weapon_api.example.goal;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class SmartHoldRangedAttackGoal<E extends Mob> extends SmartRangedAttackGoal<E> {

	private int attackTime = -1;

	public SmartHoldRangedAttackGoal(E mob, IMeleeGoal melee, double speed) {
		super(mob, mob instanceof IWeaponHolder h ? h : IWeaponHolder.simple(mob), melee, speed, 0);
	}

	public SmartHoldRangedAttackGoal(E mob, IWeaponHolder holder, IMeleeGoal melee, double speed, double radius) {
		super(mob, holder, melee, speed, radius);
	}

	@Override
	public boolean mayActivate(ItemStack stack) {
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		return weapon.isPresent() && weapon.get().isValid(holder.toUser(), stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}


	@Override
	public double range(ItemStack stack) {
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		return weapon.map(b -> b.range(mob, stack)).orElse(0.0);
	}

	public void tick() {
		doMelee();
		strafing();
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		if (weapon.isEmpty()) return;
		LivingEntity target = mob.getTarget();
		boolean invalidTarget = target == null || !target.isAlive();
		boolean withInRange = !invalidTarget && mob.distanceTo(target) < weapon.get().range(mob, stack);
		var user = holder.toUser();
		if (mob.isUsingItem()) {
			boolean infiniteUse = weapon.get().infiniteUse(mob, stack);
			if (seeTime < -60 || infiniteUse && !withInRange) {
				mob.stopUsingItem();
			} else if (seeTime > 0 && !invalidTarget) {
				int i = mob.getTicksUsingItem();
				if (!infiniteUse && i >= weapon.get().holdTime(mob, stack)) {
					attackTime = weapon.get().trigger(user, stack, target, i);
					mob.stopUsingItem();
				} else {
					weapon.get().tickUsing(user, stack, i);
				}
			}
		} else if (--attackTime <= 0 && seeTime >= -60 && withInRange) {
			mob.startUsingItem(holder.getWeaponHand());
		}
	}


	@Override
	public void performRangedAttack(LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
	}

}
