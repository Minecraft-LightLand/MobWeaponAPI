package dev.xkmc.mob_weapon_api.api.goals;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.registry.IWeaponStatusPredicate;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class WeaponGoalRegistry<E extends Mob & IWeaponHolder> {

	private final LinkedHashMap<ResourceLocation, WeaponGoalEntry<E>> knowledge = new LinkedHashMap<>();

	public <T extends Goal & IWeaponGoal<E>> void register(ResourceLocation id, IWeaponStatusPredicate item, IWeaponGoalFactory<E, T> goal) {
		knowledge.put(id, new WeaponGoalEntry<>(item, goal));
	}

	@Nullable
	public WeaponSearchResult<E> find(LivingEntity user, ItemStack weapon, @Nullable InteractionHand hand) {
		for (var ent : knowledge.entrySet()) {
			var status = ent.getValue().item().getProperties(user, weapon, hand);
			if (status.isPresent())
				return new WeaponSearchResult<>(ent.getKey(), status.get(), ent.getValue());
		}
		return null;
	}

	public record WeaponSearchResult<E extends Mob & IWeaponHolder>(
			ResourceLocation id, WeaponStatus status, WeaponGoalEntry<E> entry
	) {
	}

	public record WeaponGoalEntry<E extends Mob & IWeaponHolder>(
			IWeaponStatusPredicate item,
			IWeaponGoalFactory<E, ?> goal
	) {
	}

}
