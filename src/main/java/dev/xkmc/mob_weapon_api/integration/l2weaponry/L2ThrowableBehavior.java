package dev.xkmc.mob_weapon_api.integration.l2weaponry;

import dev.xkmc.l2weaponry.content.entity.BaseThrownWeaponEntity;
import dev.xkmc.l2weaponry.content.item.base.BaseThrowableWeaponItem;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.example.behavior.ThrowableBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class L2ThrowableBehavior extends ThrowableBehavior {

	@Override
	protected @Nullable Projectile getProjectile(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		if (stack.getItem() instanceof BaseThrowableWeaponItem item) {
			BaseThrownWeaponEntity<? extends BaseThrownWeaponEntity<?>> ans = item.getProjectile(user.user().level(), user.user(), stack, time);
			ans.setBaseDamage(user.user().getAttributeValue(Attributes.ATTACK_DAMAGE));
			ans.pickup = AbstractArrow.Pickup.DISALLOWED;
			return ans;
		}
		return null;
	}

}
