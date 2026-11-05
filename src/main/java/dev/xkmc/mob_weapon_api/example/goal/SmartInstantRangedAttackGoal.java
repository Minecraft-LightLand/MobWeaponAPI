package dev.xkmc.mob_weapon_api.example.goal;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
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

	/**
	 * 只判断"还有没有弹药"，不看冷却。
	 * {@code mayActivate} 含 {@code isValid()}（读 NBT 冷却），冷却期间 {@code checkSwitch} 会误判成
	 * "打不出去"而把武器换到副手；换手后新主手仍在冷却，下一轮又被换回来，形成主副手来回切换的死循环。
	 * 但"弹药耗尽要切近战"必须能识别，所以这里用 {@code hasAmmo}（不含冷却）。
	 */
	@Override
	public boolean isAvailable(ItemStack stack) {
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		return weapon.isPresent() && weapon.get().hasAmmo(holder.toUser(), stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
		// 必须回调父类：它会 setAggressive(false) / setInRangeAttack(false) / 重置 seeTime 与 meleeTime /
		// stopUsingItem。漏掉的话，傀儡用完一次 INSTANT 远程武器后 isAggressive() 与 IS_IN_RANGE_ATTACK
		// 会一直残留 true —— 表现为模型保持攻击预备姿势、阻尼盾极化一直停在「远程吸引」模式。
		super.stop();
	}

	@Override
	public double range(ItemStack stack) {
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		return weapon.map(b -> b.range(holder.toUser(), stack)).orElse(0.0);
	}

	@Override
	protected boolean keepDistance(ItemStack stack) {
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		return weapon.map(IInstantWeaponBehavior::keepDistance).orElse(true);
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
