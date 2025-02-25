package dev.xkmc.mob_weapon_api.integration.tinker;

import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TConstructIntegration {

	public static void init() {
		WeaponRegistry.BOW.register(new ResourceLocation(TConstruct.MOD_ID, "bow"),
				stack -> WeaponStatus.OFFENSIVE.of(stack.getItem() instanceof ModifiableBowItem && !ToolStack.from(stack).isBroken()),
				(golem, stack) -> new TinkerBowBehavior()
		);
		WeaponRegistry.CROSSBOW.register(new ResourceLocation(TConstruct.MOD_ID, "crossbow"),
				stack -> WeaponStatus.OFFENSIVE.of(stack.getItem() instanceof ModifiableCrossbowItem && !ToolStack.from(stack).isBroken()),
				(golem, stack) -> new TinkerCrossbowBehavior()
		);
	}

}
