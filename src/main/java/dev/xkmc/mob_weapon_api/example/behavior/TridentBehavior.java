package dev.xkmc.mob_weapon_api.example.behavior;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TridentBehavior extends ThrowableBehavior {

	@Override
	protected @Nullable Projectile getProjectile(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		var ans = new ThrownTrident(user.user().level(), user.user(), stack);
		ans.pickup = AbstractArrow.Pickup.DISALLOWED;
		return ans;
	}

}
