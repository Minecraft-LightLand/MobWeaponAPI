package dev.xkmc.mob_weapon_api.api.goals;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.mob_weapon_api.util.WeaponUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class AbstractWeaponManager<T extends Mob & IWeaponHolder> {

	private final WeaponGoalRegistry<T> registry;
	private final T user;
	private final IMeleeGoal meleeGoal;
	private final LinkedHashMap<ResourceLocation, WeaponGoalHolder<?>> goals = new LinkedHashMap<>();

	@Nullable
	private WeaponGoalHolder<?> currentGoal = null;
	private boolean meleeActive = false;

	public AbstractWeaponManager(WeaponGoalRegistry<T> registry, T user, IMeleeGoal melee) {
		this.registry = registry;
		this.user = user;
		meleeGoal = melee;
	}

	private @Nullable WeaponHolder<T, ?> getGoalForWeapon(ItemStack stack, @Nullable InteractionHand hand) {
		var ent = registry.find(user, stack, hand);
		if (ent == null) return null;
		if (goals.containsKey(ent.id())) {
			return new WeaponHolder<>(user, stack, goals.get(ent.id()), ent.status());
		} else {
			var ans = new WeaponGoalHolder<>(ent.id(), ent.entry().goal().create(user, meleeGoal));
			goals.put(ent.id(), ans);
			return new WeaponHolder<>(user, stack, ans, ent.status());
		}
	}

	public void reassessWeaponGoal() {
		if (user.level().isClientSide) return;
		InteractionHand hand = user.getWeaponHand();
		ItemStack weapon = user.getItemInHand(hand);
		if (!weapon.isEmpty()) {
			var ans = getGoalForWeapon(weapon, hand);
			if (ans != null) {
				if (currentGoal != null) {
					user.goalSelector.removeGoal(currentGoal.goal());
				}
				currentGoal = ans.holder();
				user.goalSelector.addGoal(2, currentGoal.goal());

				// 只有远程武器"真的能打"时才移除近战 goal。
				// 之前无条件移除，导致"远程 + 盾牌"组合里：弓没弹药 → 换手后主手是盾牌、
				// 而 getWeaponHand() 因主手是盾牌返回副手，于是这里找到的是副手的弓（没弹药），
				// 远程 goal 因 canUse 为 false 不激活，近战 goal 又被移除 → 傀儡站着不动。
				boolean rangedUsable = ans.isRanged() && ans.isAvailable();
				if (!ans.status().isMelee() && rangedUsable) {
					if (meleeActive) {
						user.goalSelector.removeGoal(meleeGoal.asGoal());
						meleeActive = false;
					}
					return;
				}
			}
		}
		// 走到这里意味着"没有可用的远程/武器 goal"：启用近战 goal 兜底。
		// 触发情形包括：
		//   - 主手为空（远程武器被换到副手/备用槽）；
		//   - 主手是盾牌等非武器物品（例如"远程 + 盾牌"组合里弓没弹药时，
		//     getAltWeaponHand 返回副手盾牌，换手后盾牌到了主手，而 getGoalForWeapon 找不到 goal）。
		// 后一种情况如果不兜底，傀儡会站着不动。
		if (!meleeActive) {
			user.goalSelector.addGoal(3, meleeGoal.asGoal());
			meleeActive = true;
		}
	}

	public void performRangedAttack(LivingEntity target, float power) {
		if (currentGoal != null && currentGoal.goal() instanceof IRangedWeaponGoal goal) {
			InteractionHand hand = user.getWeaponHand();
			ItemStack stack = user.getItemInHand(hand);
			goal.performRangedAttack(target, power, stack, hand);
		}
	}

	public boolean isRangedModeAvailable(ItemStack stack) {
		var goal = getGoalForWeapon(stack, null);
		return goal != null && goal.isRanged() && goal.isAvailable();
	}

	public boolean checkSwitch(@Nullable LivingEntity target, ItemWrapper mainhand, ItemWrapper offhand) {
		ItemStack main = mainhand.getItem();
		ItemStack off = offhand.getItem();
		var mainGoal = getGoalForWeapon(main, null);
		var offGoal = getGoalForWeapon(off, null);
		if (mainGoal != null && mainGoal.isRanged()) {
			if (target == null) return false;
			// 主手远程武器、副手为空（或只有箭）：没弹药就换手，让副手的近战武器或拳头接手。
			// 用 isAvailable()（只看弹药）而不是 mayActivate()（含冷却）：后者在冷却期间会误判成
			// "打不出去"，换手后新主手仍在冷却，下一轮又换回来，形成主副手来回切换的死循环。
			if (off.isEmpty() || off.getItem() instanceof ArrowItem)
				return !mainGoal.isAvailable();
			// 用 isAvailable()（只看弹药）而不是 mayActivate()（含冷却）：
			// 冷却期间 mayActivate 为假，会误判成"打不出去"而换手；换过去后副手的远程武器仍在冷却，
			// 下一轮又被换回来——主副手来回切换。只有弹药真的耗尽时才该让位给近战。
			if (!mainGoal.isAvailable()) {
				if (!mainGoal.isMelee(off))
					return true;
			}
			if (offGoal != null) {
				if (offGoal.priority() > mainGoal.priority()) {
					if (offGoal.isWithinRange(target, 0))
						return true;
				}
				if (!offGoal.isMelee(main) && offGoal.isRanged()) {
					return false;
				}
			}
			boolean reach = meleeGoal.canReachTarget(target);
			if (reach && !mainGoal.isMelee(off)) return true;
			// main doubles as melee weapon (e.g. OFFENSIVE hold weapons): swap only if offhand hits harder
			return reach && offGoal == null && WeaponUtil.isBetterWeapon(user, off, main);
		}
		if (offGoal != null && offGoal.isRanged()) {
			// 同上：用 isAvailable() 而不是 mayActivate()，避免因冷却导致反复换手
			if (!offGoal.isAvailable()) return false;
			if (target == null) return true;
			if (mainGoal != null) {
				if (!mainGoal.isWithinRange(target, 4)) {
					return true;
				}
				if (offGoal.priority() < mainGoal.priority()) {
					return false;
				}
			}
			if (!meleeGoal.canReachTarget(target)) return true;
			// 主手没有可用武器（空槽 / 非武器物品）：换回副手的远程武器。
			// 少了这一句，远程武器因弹药耗尽被换到副手后，即使补上弹药也永远回不到主手
			// ——主手一直空着，goal 走的是近战兜底。
			if (mainGoal == null) return true;
			// in reach: swap to off only if it is melee-usable and actually better (avoids swap oscillation)
			return offGoal.isMelee(main) && WeaponUtil.isBetterWeapon(user, off, main);
		}
		// 只在主手为空时切回副手的东西。
		// 不能写成 {@code main.isEmpty() && !off.isEmpty()} 之外还额外放行——主手是远程武器但
		// 弹药耗尽时，前面已经把武器换到了副手；这里若无条件"看到 off 非空就切回"，
		// 就会 没弹药→切走→切回→没弹药 无限循环（口香糖枪、射线枪都是这个症状）。
		return main.isEmpty() && !off.isEmpty();
	}

	private record WeaponGoalHolder<T extends Goal & IWeaponGoal>(ResourceLocation id, T goal) {
	}

	private record WeaponHolder<E extends Mob, T extends Goal & IWeaponGoal>(
			E golem, ItemStack stack, WeaponGoalHolder<T> holder, WeaponStatus status
	) {

		public T goal() {
			return holder().goal();
		}

		public int priority() {
			return mayActivate() ? status().priority() : -100;
		}

		public boolean isMelee() {
			return isMelee(ItemStack.EMPTY);
		}

		public boolean isMelee(ItemStack other) {
			return status().isMelee() && holder.goal.shouldUseForMelee(other);
		}

		public boolean isRanged() {
			return status().isRanged();
		}

		public boolean mayActivate() {
			return goal().mayActivate(stack);
		}

		public boolean isAvailable() {
			return goal().isAvailable(stack);
		}

		public boolean isWithinRange(LivingEntity target, double buffer) {
			return golem.distanceTo(target) < goal().range(stack) + buffer;
		}

	}

}
