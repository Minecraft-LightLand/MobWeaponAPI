package dev.xkmc.mob_weapon_api.integration.l2weaponry;

import dev.xkmc.l2weaponry.content.item.base.BaseThrowableWeaponItem;
import dev.xkmc.l2weaponry.init.L2Weaponry;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.resources.ResourceLocation;

public class L2WeaponryIntegration {

	public static void init() {
		WeaponRegistry.HOLD.register(new ResourceLocation(L2Weaponry.MODID, "throwable"),
				e -> WeaponStatus.RANGED.of(e.getItem() instanceof BaseThrowableWeaponItem),
				(golem, stack) -> new L2ThrowableBehavior(), 10
		);
	}

}
