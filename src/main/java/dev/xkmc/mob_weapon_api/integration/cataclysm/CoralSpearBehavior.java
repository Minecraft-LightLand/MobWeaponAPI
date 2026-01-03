package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.example.behavior.ThrowableBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CoralSpearBehavior extends ThrowableBehavior {

	@Override
	protected @Nullable Projectile getProjectile(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		return CataclysmProxy.coralSpear(user.user(), user.user().level(), stack);
	}

}
