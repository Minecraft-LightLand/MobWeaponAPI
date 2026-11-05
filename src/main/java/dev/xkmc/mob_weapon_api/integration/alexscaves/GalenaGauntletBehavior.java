package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

/**
 * 方铅护手：另一只手握着磁力武器时持续使用，把武器"投"出去变成环绕的磁力武器实体。
 * <p>
 * 原版 {@code onUseTick} 会把另一只手的武器 {@code setCount(0)} 抹掉；傀儡接不住
 * {@code MagneticWeaponEntity} 的 pickup，武器就真的没了。所以
 * {@link dev.xkmc.mob_weapon_api.mixin.GalenaGauntletGolemMixin} 对非玩家持有者跳过了那次清零，
 * 武器留在手上，环绕武器照样生成。
 * <p>
 * 这里把"另一只手的磁力武器"当成弹药：没有就不该开火（{@code hasAmmo} / {@code isValid}），
 * 这样 {@code checkSwitch} 会在没武器时让位给近战，而不是抱着护手空转。
 * 盾牌不算——原版 tag 也把盾牌包含在内，但召唤一面盾牌去砸人显然不是想要的效果。
 */
public class GalenaGauntletBehavior implements IHoldWeaponBehavior {

	/**
	 * 找到可被召唤的磁力武器：先看另一只手，没有就扫能力槽。
	 * <p>
	 * 傀儡的 {@code CombinedInvWrapper} 把双手、装备槽、<b>副武器槽</b>（{@code backupHand}）
	 * 和弹药槽都聚合进同一个 {@code IItemHandler}，所以副手放盾牌、武器放副武器槽时也能找到。
	 * 盾牌不算——原版 tag 也把盾牌包含在内，但召唤一面盾牌去砸人显然不是想要的效果。
	 */
	public static ItemStack findWeapon(LivingEntity golem, ItemStack gauntlet) {
		ItemStack main = golem.getMainHandItem();
		ItemStack off = golem.getOffhandItem();
		ItemStack direct = filterMagnetic(main == gauntlet ? off : main, gauntlet);
		if (!direct.isEmpty()) return direct;
		// 缓存一次扫描结果：isValid / hasAmmo 每 tick 会被 canUse、checkSwitch 等多处调用，
		// 每次遍历整个 IItemHandler 会让"有没有武器"的判定随调用时机抖动，
		// 而射线枪的 isValid 只读 tag，所以它不抖。这里按 (实体, 游戏时间) 缓存。
		long key = (long) golem.getId() * 1_000_000_000L + golem.level().getGameTime();
		ItemStack cached = SCAN_CACHE.get(key);
		if (cached != null) return cached;
		ItemStack found = scanWeapon(golem, gauntlet);
		SCAN_CACHE.clear();
		SCAN_CACHE.put(key, found);
		return found;
	}

	/** 每次扫描结果，按 (实体 id, gameTime) 缓存，只在当前 tick 内有效。 */
	private static final java.util.Map<Long, ItemStack> SCAN_CACHE = new java.util.HashMap<>();

	private static ItemStack scanWeapon(LivingEntity golem, ItemStack gauntlet) {
		var handler = golem.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (handler.isEmpty()) return ItemStack.EMPTY;
		var inv = handler.get();
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty() || stack.getItem() instanceof GalenaGauntletItem) continue;
			ItemStack found = filterMagnetic(stack, gauntlet);
			if (!found.isEmpty()) return found;
		}
		return ItemStack.EMPTY;
	}

	private static ItemStack filterMagnetic(ItemStack stack, ItemStack gauntlet) {
		if (stack.isEmpty() || stack.getItem() instanceof ShieldItem) return ItemStack.EMPTY;
		boolean crystallization = gauntlet.getEnchantmentLevel(ACEnchantmentRegistry.CRYSTALLIZATION.get()) > 0;
		return stack.is(crystallization ? ACTagRegistry.GALENA_GAUNTLET_CRYSTALLIZATION_ITEMS
				: ACTagRegistry.MAGNETIC_ITEMS) ? stack : ItemStack.EMPTY;
	}

	@Override
	public boolean isValid(ProjectileWeaponUser user, ItemStack stack) {
		return hasAmmo(user, stack) && IHoldWeaponBehavior.super.isValid(user, stack);
	}

	/** 没有可召唤的武器就等于没弹药：goal 停用，让近战接管。 */
	@Override
	public boolean hasAmmo(ProjectileWeaponUser user, ItemStack stack) {
		LivingEntity holder = user.user();
		// 只看"有没有可召唤的武器"：这里不能要求有目标。
		// SmartHoldRangedAttackGoal 的 canContinueToUse() 是 canUse() || !navigation.isDone()，
		// 用 hasAmmo 卡目标会让 goal 在 target 消失后仍靠 navigation 续命，一直 startUsingItem。
		// 停手交给 SmartRangedAttackGoal.canUse()（它已经检查 mob.getTarget()）。
		return !findWeapon(holder, stack).isEmpty();
	}


	/**
	 * 护手没有攻击力，不能当作近战武器挥。{@code SmartHoldRangedAttackGoal.tick()} 里
	 * {@code if (weapon.get().canMelee()) doMelee();} 会让它在贴身时调 {@code doHurtTarget}——
	 * 打不出伤害，还会挡住 {@code checkSwitch} 把近战武器换到主手。
	 */
	@Override
	public boolean canMelee() {
		return false;
	}

	/** 护手是"按住不放"的持续型，交给 onUseTick 每 tick 推进。 */
	@Override
	public boolean infiniteUse(LivingEntity user, ItemStack stack) {
		return true;
	}

	/**
	 * 护手没有"射程"概念——召唤出去的武器自己会追，但 {@code range} 仍然决定傀儡的
	 * <b>站位与跟随距离</b>：{@code SmartHoldRangedAttackGoal} 用它判断 {@code withInRange}，
	 * 傀儡会在 {@code range} 外主动靠近。写 64 会让傀儡在 64 格外就站定不动（比跟随距离还远），
	 * 看起来像"不跟过来"。取全部武器里的最大值 25（射线枪、投掷矛、灾变各武器都是这个值），
	 * 与 {@code RaygunBehavior} 保持一致。
	 */
	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 25;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 20;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		return 10;
	}

}
