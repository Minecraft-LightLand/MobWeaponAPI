package dev.xkmc.mob_weapon_api.example.behavior;

import dev.xkmc.mob_weapon_api.api.projectile.CrossbowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ICrossbowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SimpleCrossbowBehavior implements ICrossbowBehavior {

	@Override
	public int chargeTime(LivingEntity user, ItemStack stack) {
		return CrossbowItem.getChargeDuration(stack, user);
	}

	@Override
	public boolean hasProjectile(ProjectileWeaponUser mob, ItemStack stack) {
		return !mob.getPreferredProjectile(stack).isEmpty();
	}

	@Override
	public List<ItemStack> getLoadedProjectile(LivingEntity user, ItemStack stack) {
		var data = stack.get(DataComponents.CHARGED_PROJECTILES);
		if (data == null) return List.of();
		return data.getItems();
	}

	@Override
	public void release(ItemStack stack) {
		stack.remove(DataComponents.CHARGED_PROJECTILES);
	}

	@Override
	public boolean tryCharge(ProjectileWeaponUser user, ItemStack stack) {
		return CrossbowItem.tryLoadProjectiles(user.user(), stack);
	}

	@Override
	public int performRangedAttack(CrossbowUseContext user, ItemStack stack, InteractionHand hand) {
		if (user.user() instanceof CrossbowAttackMob mob)
			mob.performCrossbowAttack(user.user(), user.getCrossbowVelocity(getLoadedProjectile(user.user(), stack)));
		return 0;
	}

}
