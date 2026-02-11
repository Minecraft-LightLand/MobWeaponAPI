package dev.xkmc.mob_weapon_api.integration.l2complements;

import dev.xkmc.l2complements.init.registrate.LCItems;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;

public class L2ComplementsIntegration {

	public static void init() {
		WeaponRegistry.HOLD.register(LCItems.SONIC_SHOOTER.getId(),
				e -> WeaponStatus.RANGED.of(e.is(LCItems.SONIC_SHOOTER.get())),
				(golem, stack) -> new SonicShooterBehavior(), 10
		);

		WeaponRegistry.HOLD.register(LCItems.HELLFIRE_WAND.getId(),
				e -> WeaponStatus.RANGED.of(e.is(LCItems.HELLFIRE_WAND.get())),
				(golem, stack) -> new HellfireWandBehavior(), 10
		);

		WeaponRegistry.HOLD.register(LCItems.WINTERSTORM_WAND.getId(),
				e -> WeaponStatus.MELEE.of(e.is(LCItems.WINTERSTORM_WAND.get())),
				(golem, stack) -> new WinterstormWandBehavior(), 50
		);

		WeaponRegistry.HOLD.register(LCItems.HELIOS_SCEPTER.getId(),
				e -> WeaponStatus.RANGED.of(e.is(LCItems.HELIOS_SCEPTER.get())),
				(golem, stack) -> new HeliosScepterBehavior(), 10
		);

		WeaponRegistry.HOLD.register(LCItems.BOREAS_CEEPTER.getId(),
				e -> WeaponStatus.OFFENSIVE.of(e.is(LCItems.BOREAS_CEEPTER.get())),
				(golem, stack) -> new BoreasScepterBehavior(), 50
		);

	}

}
