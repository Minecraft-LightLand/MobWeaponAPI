package dev.xkmc.mob_weapon_api.integration.alexscaves;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 纯近战武器的占位 behavior。
 * <p>
 * 亚历克斯洞穴的近战武器（{@code PrimitiveClubItem} 等）没有远程或蓄力逻辑，登记它们的目的只是让
 * {@code WeaponRegistry} 把它们识别成武器，从而让傀儡不会因为"没有可用武器"而忽略手里的物品。
 * 实际伤害由傀儡自身的近战逻辑处理，所以 {@link #trigger} 什么都不做。
 */
public class MeleeBehavior implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 0;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		return 0;
	}

}
