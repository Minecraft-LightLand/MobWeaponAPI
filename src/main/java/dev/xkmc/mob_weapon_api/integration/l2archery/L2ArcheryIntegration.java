package dev.xkmc.mob_weapon_api.integration.l2archery;

import dev.xkmc.l2archery.content.item.GenericBowItem;
import dev.xkmc.l2archery.init.L2Archery;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.resources.ResourceLocation;

public class L2ArcheryIntegration {

	public static void init() {
		WeaponRegistry.BOW.register(new ResourceLocation(L2Archery.MODID, "bow"),
				e -> WeaponStatus.RANGED.of(e.getItem() instanceof GenericBowItem),
				(golem, stack) -> new L2BowBehavior(), 10
		);
	}

}
