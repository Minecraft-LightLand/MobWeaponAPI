package dev.xkmc.mob_weapon_api.integration.cataclysm;

import com.github.L_Ender.cataclysm.init.ModItems;
import com.github.L_Ender.cataclysm.items.Cursed_bow;
import com.github.L_Ender.cataclysm.items.Wrath_of_the_desert;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;

public class CataclysmIntegration {

	public static void init() {
		WeaponRegistry.BOW.register(ModItems.CURSED_BOW.getId(),
				e -> WeaponStatus.RANGED.of(e.getItem() instanceof Cursed_bow),
				(golem, stack) -> new CursedBowBehavior()
		);
		WeaponRegistry.BOW.register(ModItems.WRATH_OF_THE_DESERT.getId(),
				e -> WeaponStatus.RANGED.of(e.getItem() instanceof Wrath_of_the_desert),
				(golem, stack) -> new WrathBowBehavior()
		);
	}

}
