package dev.xkmc.mob_weapon_api.api.goals;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public interface IRangedWeaponGoal<E extends Mob & IWeaponHolder> extends IWeaponGoal<E> {

	void performRangedAttack(LivingEntity target, float power, ItemStack stack, InteractionHand hand);

}
