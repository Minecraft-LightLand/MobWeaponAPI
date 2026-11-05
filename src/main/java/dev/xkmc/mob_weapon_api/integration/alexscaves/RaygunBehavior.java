package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.item.RaygunItem;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

/**
 * 雷射枪。原版逻辑全在 {@code RaygunItem#onUseTick} 里逐 tick 推进：蓄力 15 tick 后开始发射光束并结算伤害。
 * 傀儡只要持续 {@code startUsingItem} 就能复用同一套逻辑，因此这里只需给出合适的保持时长。
 * <p>
 * 附魔（{@code X_RAY} 透视、{@code GAMMA_RAY} 伽马、{@code ENERGY_EFFICIENCY} 节能）都由原版
 * {@code onUseTick} 自行读取，无需在 behavior 里重复处理。
 */
public class RaygunBehavior implements IHoldWeaponBehavior {

	/**
	 * 射线枪是"按住持续开火"的武器：原版 {@code onUseTick} 每 tick 推进光束。
	 * 返回 true 让 HOLD goal 保持使用状态、不主动 {@code stopUsingItem}，
	 * 否则会表现为"用一下停一下"。
	 */
	@Override
	public boolean infiniteUse(LivingEntity user, ItemStack stack) {
		return true;
	}

	/**
	 * 没电时不能"激活"：{@code SmartHoldRangedAttackGoal#canUse} 走的是 {@code mayActivate} →
	 * {@code isValid}，只看冷却的话，充能耗尽后 goal 仍会每 tick {@code startUsingItem}，
	 * 于是 {@code onUseTick} 反复撞 {@code !hasCharge} 分支——表现为"一直试图开火、每 tick 播空枪音效、
	 * 光束还在画"。把充能并进 isValid 就能让 goal 在没电时停下来。
	 */
	@Override
	public boolean isValid(ProjectileWeaponUser user, ItemStack stack) {
		return RaygunItem.hasCharge(stack) && IHoldWeaponBehavior.super.isValid(user, stack);
	}

	/**
	 * 「枪里还有电」<b>或</b>「身上找得到铀棒」都算有弹药——和口香糖枪同一个语义。
	 * <p>
	 * 只看枪内充能的话，一旦耗尽 {@code isAvailable} 就变 false，{@code checkSwitch} 会把枪换到副手；
	 * 而装填逻辑只认主手，于是即使补了铀棒也永远回不到主手、也永远补不上弹。
	 * 把"能找到弹药"也算进来，枪就会留在/换回主手，随后由装填逻辑把铀棒消耗掉。
	 */
	@Override
	public boolean hasAmmo(ProjectileWeaponUser user, ItemStack stack) {
		return RaygunItem.hasCharge(stack) || !findUranium(user.user()).isEmpty();
	}

	/** 通过物品处理器能力找铀棒：傀儡的 CombinedInvWrapper 会聚合双手、装备槽和弹药槽。 */
	private static ItemStack findUranium(LivingEntity golem) {
		var handler = golem.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (handler.isEmpty()) return ItemStack.EMPTY;
		IItemHandler inv = handler.get();
		for (int slot = 0; slot < inv.getSlots(); slot++) {
			ItemStack s = inv.getStackInSlot(slot);
			if (!s.isEmpty() && RaygunItem.AMMO.test(s)) return s;
		}
		return ItemStack.EMPTY;
	}

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
		var golem = user.user();
		// 原版光束伤害由 releaseUsing 结算，这里先补一次，避免傀儡因不经过 releaseUsing 而白放
		if (RaygunItem.hasCharge(stack)) {
			stack.hurtAndBreak(1, golem, e -> {
			});
		}
		setCD(golem, stack, 30);
		return 30;
	}

}
