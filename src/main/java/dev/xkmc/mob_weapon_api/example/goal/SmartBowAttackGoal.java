package dev.xkmc.mob_weapon_api.example.goal;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;

public class SmartBowAttackGoal<E extends Mob & IWeaponHolder & RangedAttackMob> extends SmartRangedAttackGoal<E> {

	private int attackTime = -1;

	public SmartBowAttackGoal(E mob, IMeleeGoal melee, double speed, double radius) {
		super(mob, melee, speed, radius);
	}

	@Override
	public boolean mayActivate(ItemStack stack) {
		var weapon = WeaponRegistry.BOW.get(mob, stack);
		if (weapon.isEmpty()) return false;
		return weapon.get().hasProjectile(mob.toUser(), stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}

	public void tick() {
		doMelee();
		strafing();
		LivingEntity target = mob.getTarget();
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponRegistry.BOW.get(mob, stack);
		if (weapon.isEmpty()) return;
		if (mob.isUsingItem() && target != null) {
			var user = mob.toUser();
			double dist = mob.distanceTo(target);
			if (seeTime < -60) {
				mob.stopUsingItem();
			} else if (seeTime > 0) {
				int i = mob.getTicksUsingItem();
				int pullTime = weapon.get().getPreferredPullTime(user, stack, dist);
				if (i >= pullTime) {
					mob.performRangedAttack(target, weapon.get().getPowerForTime(user, stack, i));
					mob.stopUsingItem();
				} else {
					weapon.get().tickUsingBow(user, stack);
				}
			}
		} else if (--attackTime <= 0 && seeTime >= -60) {
			var user = mob.toUser();
			if (target != null) {
				double dist = mob.distanceTo(target);
				int pullTime = weapon.get().getPreferredPullTime(user, stack, dist);
				if (pullTime <= 0) {
					mob.performRangedAttack(target, 0);
					return;
				}
			}
			mob.startUsingItem(mob.getWeaponHand());
			weapon.ifPresent(e -> e.startUsingBow(user, stack));

		}
	}

	@Override
	public void performRangedAttack(LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
		var opt = WeaponRegistry.BOW.get(mob, stack);
		if (opt.isEmpty()) return;
		attackTime = opt.get().shootArrow(mob.toUser(), power, stack, hand);
	}

}
