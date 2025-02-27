package dev.xkmc.mob_weapon_api.example.goal;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.ItemStack;

public class SmartCrossbowAttackGoal<E extends Mob & IWeaponHolder & CrossbowAttackMob> extends SmartRangedAttackGoal<E> {
	private CrossbowState crossbowState = CrossbowState.UNCHARGED;
	private int attackDelay;

	public SmartCrossbowAttackGoal(E mob, IMeleeGoal melee, double speed, float radius) {
		super(mob, melee, speed, radius);
	}

	@Override
	public boolean mayActivate(ItemStack stack) {
		var weapon = WeaponRegistry.CROSSBOW.get(mob, stack);
		if (weapon.isEmpty()) return false;
		return weapon.get().hasProjectile(mob.toUser(), stack) ||
				weapon.get().hasLoadedProjectile(mob, stack);
	}

	public void stop() {
		super.stop();
		mob.setChargingCrossbow(false);
		attackDelay = 0;
	}

	public void tick() {
		doMelee();
		strafing();
		LivingEntity target = mob.getTarget();
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponRegistry.CROSSBOW.get(mob, stack);
		if (weapon.isEmpty()) return;
		var user = mob.toUser();
		var behavior = weapon.get();
		if (crossbowState == CrossbowState.UNCHARGED) {
			if (behavior.hasLoadedProjectile(mob, stack)) {
				crossbowState = CrossbowState.CHARGED;
				mob.setChargingCrossbow(false);
			} else if (behavior.hasProjectile(user, stack)) {
				if (behavior.chargeTime(mob, stack) <= 0) {
					if (weapon.get().tryCharge(user, stack)) {
						crossbowState = CrossbowState.CHARGED;
					}
				} else {
					mob.startUsingItem(mob.getWeaponHand());
					crossbowState = CrossbowState.CHARGING;
					mob.setChargingCrossbow(true);
				}
			}
		} else if (crossbowState == CrossbowState.CHARGING) {
			if (!mob.isUsingItem()) {
				crossbowState = CrossbowState.UNCHARGED;
			}
			if (mob.getTicksUsingItem() >= weapon.get().chargeTime(mob, stack)) {
				mob.releaseUsingItem();
				if (weapon.get().tryCharge(user, stack)) {
					crossbowState = CrossbowState.CHARGED;
					mob.setChargingCrossbow(false);
				}
			}
		}

		if (target != null) {
			if (crossbowState == CrossbowState.CHARGED) {
				if (attackDelay == 0) {
					attackDelay = 10;
				}
				--attackDelay;
				if (attackDelay == 0) {
					crossbowState = CrossbowState.READY_TO_ATTACK;
				}
			} else if (crossbowState == CrossbowState.READY_TO_ATTACK && seeTime > 0) {
				mob.performRangedAttack(target, 1.0F);
				behavior.release(stack);
				crossbowState = CrossbowState.UNCHARGED;
			}
		}
	}

	@Override
	public void performRangedAttack(LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
		WeaponRegistry.CROSSBOW.get(mob, stack).ifPresent(e -> e.performRangedAttack(mob.toUser(), stack, hand));
	}

	enum CrossbowState {
		UNCHARGED,
		CHARGING,
		CHARGED,
		READY_TO_ATTACK
	}

}
