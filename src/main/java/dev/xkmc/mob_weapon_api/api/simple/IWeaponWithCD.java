package dev.xkmc.mob_weapon_api.api.simple;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.init.MobWeaponAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IWeaponWithCD {

	String TIMESTAMP = MobWeaponAPI.MODID + "_WeaponCD";

	default void setCD(LivingEntity user, ItemStack stack, int time) {
		long next = user.level().getGameTime() + time;
		stack.getOrCreateTag().putLong(TIMESTAMP, next);
	}

	default boolean isValid(ProjectileWeaponUser user, ItemStack stack) {
		long current = user.user().level().getGameTime();
		return current >= stack.getOrCreateTag().getLong(TIMESTAMP);
	}

	/**
	 * 这把武器现在还有没有弹药（不涉及冷却）。
	 * <p>
	 * {@link #isValid} 把冷却也算进去，用它判断"能不能用"会让 {@code checkSwitch} 在冷却期间
	 * 把武器换到副手、下一轮又换回来（主副手来回切换的死循环）。同时"弹药耗尽该切近战"又必须能识别，
	 * 所以把两者拆开：{@code isValid} 管冷却，{@code hasAmmo} 管弹药。
	 */
	default boolean hasAmmo(ProjectileWeaponUser user, ItemStack stack) {
		return true;
	}

	/**
	 * 使用这把武器时要不要和敌人保持距离（{@code SmartRangedAttackGoal#strafing} 的侧移/后退）。
	 * <p>
	 * 弓、枪这类「越远越安全」的武器保持默认 {@code true}；钩锁这种「必须贴近才有意义」的武器
	 * 返回 {@code false}，傀儡就只转向、不做后退动作。
	 */
	default boolean keepDistance() {
		return true;
	}

}
