package dev.xkmc.mob_weapon_api.registry;

import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ICrossbowBehavior;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import dev.xkmc.mob_weapon_api.example.behavior.GeneralCrossbowBehavior;
import dev.xkmc.mob_weapon_api.example.behavior.SimpleBowBehavior;
import dev.xkmc.mob_weapon_api.example.behavior.TridentBehavior;
import dev.xkmc.mob_weapon_api.init.MobWeaponAPI;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;

public class WeaponRegistry {

	public static final RangedBehaviorRegistry<IBowBehavior> BOW = new RangedBehaviorRegistry<>(
			MobWeaponAPI.loc("bow"), e -> WeaponStatus.RANGED.of(e.getItem() instanceof BowItem),
			(golem, stack) -> new SimpleBowBehavior()
	);

	public static final RangedBehaviorRegistry<ICrossbowBehavior> CROSSBOW = new RangedBehaviorRegistry<>(
			MobWeaponAPI.loc("crossbow"), e -> WeaponStatus.RANGED.of(e.getItem() instanceof CrossbowItem),
			(golem, stack) -> new GeneralCrossbowBehavior()
	);

	public static final RangedBehaviorRegistry<IInstantWeaponBehavior> INSTANT = new RangedBehaviorRegistry<>(MobWeaponAPI.loc("instant"));
	public static final RangedBehaviorRegistry<IHoldWeaponBehavior> HOLD = new RangedBehaviorRegistry<>(MobWeaponAPI.loc("hold"));

	public static void init() {
		HOLD.register(MobWeaponAPI.loc("trident"), stack -> WeaponStatus.OFFENSIVE.of(stack.is(Items.TRIDENT)),
				(e, stack) -> new TridentBehavior(), 10);
	}

}
