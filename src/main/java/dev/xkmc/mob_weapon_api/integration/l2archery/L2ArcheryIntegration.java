package dev.xkmc.mob_weapon_api.integration.l2archery;

import dev.xkmc.l2archery.content.item.GenericBowItem;
import dev.xkmc.l2archery.init.L2Archery;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;

public class L2ArcheryIntegration {

	public static void init() {
		WeaponRegistry.BOW.register(L2Archery.loc("bow"),
				e -> WeaponStatus.RANGED.of(e.getItem() instanceof GenericBowItem),
				(golem, stack) -> new L2BowBehavior(), 10
		);
	}

}
