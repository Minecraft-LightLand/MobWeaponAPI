package dev.xkmc.mob_weapon_api.example.goal;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class SmartHoldRangedAttackGoal<E extends Mob & IWeaponHolder> extends SmartRangedAttackGoal<E> {

	private int attackTime = -1;

	public SmartHoldRangedAttackGoal(E mob, IMeleeGoal melee, double speed) {
		super(mob, melee, speed, 0);
	}

	@Override
	public boolean mayActivate(ItemStack stack) {
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		return weapon.isPresent() && weapon.get().isValid(mob.toUser(), stack);
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
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		if (weapon.isEmpty()) return;
		LivingEntity target = mob.getTarget();
		var user = mob.toUser();
		if (mob.isUsingItem() && target != null) {
			if (seeTime < -60) {
				mob.stopUsingItem();
			} else if (seeTime > 0) {
				int i = mob.getTicksUsingItem();
				if (i >= weapon.get().holdTime(mob, stack)) {
					attackTime = weapon.get().trigger(user, stack, target, i);
					mob.stopUsingItem();
				} else {
					weapon.get().tickUsing(user, stack, i);
				}
			}
		} else if (--attackTime <= 0 && seeTime >= -60 && target != null &&
				mob.distanceTo(target) < weapon.get().range(mob, stack)) {
			mob.startUsingItem(mob.getWeaponHand());
		}
	}


	@Override
	public void performRangedAttack(LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
	}

}
