package dev.xkmc.mob_weapon_api.util;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.mob_weapon_api.init.MobWeaponAPI;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.function.Predicate;

public class DummyProjectileWeapon extends ProjectileWeaponItem {

	public static ItemEntry<DummyProjectileWeapon> create(Predicate<ItemStack> pred) {
		return MobWeaponAPI.REGISTRATE.item("dummy_furnace", p -> new DummyProjectileWeapon(p, pred))
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
}
