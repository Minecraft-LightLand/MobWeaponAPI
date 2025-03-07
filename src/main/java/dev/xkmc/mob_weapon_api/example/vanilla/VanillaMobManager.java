package dev.xkmc.mob_weapon_api.example.vanilla;

import dev.xkmc.mob_weapon_api.api.goals.WeaponGoalRegistry;
import dev.xkmc.mob_weapon_api.example.goal.SmartBowAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartCrossbowAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartHoldRangedAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartInstantRangedAttackGoal;
import dev.xkmc.mob_weapon_api.init.MobWeaponAPI;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;

public class VanillaMobManager {

	public static final WeaponGoalRegistry<PathfinderMob> REGISTRY = new WeaponGoalRegistry<>();

	public static void init() {
		REGISTRY.register(MobWeaponAPI.loc("bow"),
				(e, stack, hand) -> WeaponRegistry.BOW.getProperties(stack),
				(e, melee) -> new SmartBowAttackGoal<>(e, melee, 1.0D, 25)
		);
		REGISTRY.register(MobWeaponAPI.loc("crossbow"),
				(e, stack, hand) -> WeaponRegistry.CROSSBOW.getProperties(stack),
				(e, melee) -> new SmartCrossbowAttackGoal<>(e, melee, 1.0D, 25)
		);
		REGISTRY.register(MobWeaponAPI.loc("instant"),
				(e, stack, hand) -> WeaponRegistry.INSTANT.getProperties(stack),
				(e, melee) -> new SmartInstantRangedAttackGoal<>(e, melee, 1.0D)
		);
		REGISTRY.register(MobWeaponAPI.loc("hold"),
				(e, stack, hand) -> WeaponRegistry.HOLD.getProperties(stack),
				(e, melee) -> new SmartHoldRangedAttackGoal<>(e, melee, 1.0D)
		);
	}

	public static boolean attachGoal(PathfinderMob mob, ItemStack stack) {
		var result = REGISTRY.find(mob, stack, InteractionHand.MAIN_HAND);
		if (result == null) return false;

		int index = 0;
		MeleeAttackGoal melee = null;
		for (var e : mob.goalSelector.getAvailableGoals()) {
			if (e.getGoal() instanceof MeleeAttackGoal goal) {
				melee = goal;
				index = e.getPriority();
			}
		}
		if (melee == null) {
			index = 2;
			melee = new MeleeAttackGoal(mob, 1, false);
			mob.goalSelector.addGoal(2, melee);
		}
		var wrapped = new MeleeGoalWrapper(mob, melee);
		var ranged = result.entry().goal().create(mob, wrapped);
		mob.goalSelector.addGoal(index - 1, ranged);
		return true;
	}

}
