package dev.xkmc.mob_weapon_api.integration.create;

import com.simibubi.create.AllItems;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;

public class CreateIntegration {

	public static void init() {
		WeaponRegistry.BOW.register(AllItems.POTATO_CANNON.getId(),
				e -> WeaponStatus.RANGED.of(e.is(AllItems.POTATO_CANNON.get())),
				(golem, stack) -> new PotatoCannonBehavior(), 0
		);
	}

}
