package dev.xkmc.mob_weapon_api.integration.twilightforest;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.mob_weapon_api.util.DummyProjectileWeapon;
import net.minecraft.world.item.Items;
import twilightforest.init.TFItems;

public class TFIntegration {

	public static final ItemEntry<DummyProjectileWeapon> TWILIGHT_WAND =
			DummyProjectileWeapon.create("twilight_wand", e -> e.is(Items.ENDER_PEARL));
	public static final ItemEntry<DummyProjectileWeapon> LIFEDRAIN_WAND =
			DummyProjectileWeapon.create("lifedrain_wand", e -> e.is(Items.FERMENTED_SPIDER_EYE));

	public static void register() {

	}

	public static void init() {
		WeaponRegistry.BOW.register(TFItems.TRIPLE_BOW.getId(),
				e -> WeaponStatus.RANGED.of(e.is(TFItems.TRIPLE_BOW.get())),
				(golem, stack) -> new TripleBowBehavior(), 0
		);
		WeaponRegistry.INSTANT.register(TFItems.TWILIGHT_SCEPTER.getId(),
				e -> WeaponStatus.RANGED.of(e.is(TFItems.TWILIGHT_SCEPTER.get())),
				(golem, stack) -> new TwilightWandBehavior(), 0
		);
		WeaponRegistry.INSTANT.register(TFItems.LIFEDRAIN_SCEPTER.getId(),
				e -> WeaponStatus.RANGED.of(e.is(TFItems.LIFEDRAIN_SCEPTER.get())),
				(golem, stack) -> new LifedrainWandBehavior(), 0
		);
	}

}
