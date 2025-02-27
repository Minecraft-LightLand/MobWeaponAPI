package dev.xkmc.mob_weapon_api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Optional;

public class RangedBehaviorRegistry<T> {

	private final ResourceLocation id;
	private final LinkedHashMap<ResourceLocation, RangedBehaviorEntry<T>> MAP = new LinkedHashMap<>();
	@Nullable
	private final RangedBehaviorEntry<T> fallback;

	public RangedBehaviorRegistry(ResourceLocation id) {
		this.id = id;
		fallback = null;
	}

	public RangedBehaviorRegistry(ResourceLocation id, RangedStatusPredicate item, RangedBehaviorFactory<T> factory) {
		this.id = id;
		this.fallback = new RangedBehaviorEntry<>(item, factory, 0);
	}

	public void register(ResourceLocation id, RangedStatusPredicate item, RangedBehaviorFactory<T> factory, int priority) {
		MAP.put(id, new RangedBehaviorEntry<>(item, factory, priority));
	}

	public boolean isValidItem(ItemStack stack) {
		return getProperties(stack).map(WeaponStatus::isRanged).orElse(false);
	}

	public Optional<WeaponStatus> getProperties(ItemStack stack) {
		for (var e : MAP.values()) {
			var status = e.item().getProperties(stack);
			if (status.isPresent())
				return Optional.of(status.get().withPriority(e.priority));
		}
		return fallback == null ? Optional.empty() : fallback.item().getProperties(stack);
	}

	public Optional<T> get(LivingEntity user, ItemStack stack) {
		for (var e : MAP.values()) {
			var status = e.item().getProperties(stack);
			if (status.isPresent())
				return Optional.of(e.factory().create(user, stack));
		}
		if (fallback != null && fallback.item().getProperties(stack).isPresent())
			return Optional.of(fallback.factory().create(user, stack));
		return Optional.empty();
	}

	private record RangedBehaviorEntry<T>(
			RangedStatusPredicate item,
			RangedBehaviorFactory<T> factory,
			int priority
	) {

	}

}
