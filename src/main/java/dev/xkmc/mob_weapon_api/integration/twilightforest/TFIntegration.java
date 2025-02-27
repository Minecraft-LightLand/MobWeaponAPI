package dev.xkmc.mob_weapon_api.integration.twilightforest;

import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import twilightforest.init.TFItems;
import twilightforest.item.TripleBowItem;

public class TFIntegration {

	public static void init() {
		WeaponRegistry.BOW.register(TFItems.TRIPLE_BOW.getId(),
				e -> WeaponStatus.RANGED.of(e.getItem() instanceof TripleBowItem),
				(golem, stack) -> new TripleBowBehavior(), 0
		);
	}

}
