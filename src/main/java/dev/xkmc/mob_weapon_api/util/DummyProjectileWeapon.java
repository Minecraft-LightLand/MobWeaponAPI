package dev.xkmc.mob_weapon_api.util;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.mob_weapon_api.init.MobWeaponAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class DummyProjectileWeapon extends ProjectileWeaponItem {

	public static ItemEntry<DummyProjectileWeapon> create(String id, Predicate<ItemStack> pred) {
		return MobWeaponAPI.REGISTRATE.item(id, p -> new DummyProjectileWeapon(p, pred))
				.model((ctx, pvd) -> pvd.withExistingParent("item/" + ctx.getName(), "block/air"))
				.register();
	}

	private final Predicate<ItemStack> pred;

	public DummyProjectileWeapon(Properties prop, Predicate<ItemStack> pred) {
		super(prop);
		this.pred = pred;
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return pred;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 0;
	}

	@Override
	protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

	}
	
}
