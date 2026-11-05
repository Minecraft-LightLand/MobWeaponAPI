package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.GumballEntity;
import com.github.alexmodguy.alexscaves.server.item.ShotGumItem;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

/**
 * 口香糖枪（ShotGum）。原版走"先上膛再发射"：{@code use()} 置 {@code Shooting} 标志，
 * 再由 {@code inventoryTick()} 延迟 5 tick 生成 {@code GumballEntity}。傀儡不需要这套节奏，
 * 这里直接复刻发射段（参考机械动力土豆加农炮的兼容写法：直接生成投射物、按冷却控制射速）。
 * <p>
 * 弹药是 {@code GUMBALL_PILE}，不属于原版弓的弹药谓词，所以不能走 {@code getProjectile(stack)}，
 * 改为通过物品处理器能力（{@code IItemHandler}）在持有者身上查找。
 * <p>
 * 已复刻的附魔：{@code TARGETED_RICOCHET}、{@code TRIPLE_SPLIT}、{@code EXPLOSIVE_FLAVOR}、{@code BOUNCY_BALL}。
 */
public class ShotGumBehavior implements IInstantWeaponBehavior {

	/**
	 * 枪内有弹，或能从持有者身上找到口香糖堆，才算"还有弹药"。
	 * 两者都没有时返回 false，{@code checkSwitch} 会把傀儡切到近战，而不是原地空放。
	 */
	@Override
	public boolean hasAmmo(ProjectileWeaponUser user, ItemStack stack) {
		return ShotGumItem.hasAmmo(stack) || !findGumball(user.user()).isEmpty();
	}

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 16;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		var golem = user.user();
		var level = golem.level();
		if (level.isClientSide) {
			setCD(golem, stack, 10);
			return 10;
		}

		if (!ShotGumItem.hasAmmo(stack)) {
			int slot = findGumballSlot(golem);
			if (slot >= 0) {
				ShotGumItem.setGumballsLeft(stack, ShotGumItem.MAX_AMMO);
				// 必须走 extractItem：getStackInSlot 拿到的不一定是可写回的栈
				// （CombinedInvWrapper 之类返回副本，shrink 只改副本），弹药就永远扣不掉，
				// hasAmmo 恒为真 → 装填永远"成功" → 配合 checkSwitch 的时序表现为反复切手。
				if (!user.bypassAllConsumption()) {
					golem.getCapability(ForgeCapabilities.ITEM_HANDLER)
							.ifPresent(h -> h.extractItem(slot, 1, false));
				}
				level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
						ACSoundRegistry.SHOTGUM_RELOAD.get(), golem.getSoundSource(), 1.0F, 1.0F);
			} else {
				level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
						ACSoundRegistry.SHOTGUM_EMPTY.get(), golem.getSoundSource(), 1.0F, 1.0F);
				setCD(golem, stack, 20);
				return 20;
			}
		}

		level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				ACSoundRegistry.SHOTGUM_SHOOT.get(), golem.getSoundSource(), 1.0F, 1.0F);

		boolean ricochet = stack.getEnchantmentLevel(ACEnchantmentRegistry.TARGETED_RICOCHET.get()) > 0;
		boolean split = stack.getEnchantmentLevel(ACEnchantmentRegistry.TRIPLE_SPLIT.get()) > 0;
		boolean explosive = stack.getEnchantmentLevel(ACEnchantmentRegistry.EXPLOSIVE_FLAVOR.get()) > 0;
		int maxBounces = 4 + stack.getEnchantmentLevel(ACEnchantmentRegistry.BOUNCY_BALL.get()) * 2;

		boolean mainHand = golem.getMainHandItem() == stack;
		boolean leftHand = mainHand && golem.getMainArm() == HumanoidArm.LEFT
				|| !mainHand && golem.getMainArm() == HumanoidArm.RIGHT;

		int count = explosive ? 1 : 2;
		for (int i = 0; i < count; i++) {
			GumballEntity gumball = new GumballEntity(level, golem);
			Vec3 relative = new Vec3(
					(explosive ? 0.0F : (i < 1 ? 0.15F : -0.15F)) + (leftHand ? 0.35F : -0.35F),
					0, 0.75F)
					.xRot(-golem.getXRot() * ((float) Math.PI / 180F))
					.yRot(-golem.yHeadRot * ((float) Math.PI / 180F));
			gumball.setPos(golem.position().add(0, golem.getBbHeight() * 0.8F, 0).add(relative));
			gumball.setTargetsOnBounce(ricochet);
			gumball.setSplitsOnHit(split);
			gumball.setExplosive(explosive);
			// 朝目标而不是朝视线（同上：视线可能还没转到位）
			float aimYaw = golem.getYRot();
			float aimPitch = golem.getXRot();
			if (target != null) {
				Vec3 to = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(golem.getEyePosition());
				aimYaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90.0F;
				aimPitch = (float) -(Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG);
			}
			gumball.shootFromRotation(golem, aimPitch, aimYaw, 0.0F,
					explosive ? 0.5F : 1.5F, 5.0F);
			gumball.setMaximumBounces(maxBounces);
			gumball.setDamage(4.0F);
			level.addFreshEntity(gumball);
		}

		ShotGumItem.setGumballsLeft(stack, Math.max(ShotGumItem.getGumballsLeft(stack) - 1, 0));
		golem.swing(InteractionHand.MAIN_HAND);
		setCD(golem, stack, 10);
		return 10;
	}

	/**
	 * 通过物品处理器能力在持有者身上找口香糖堆。
	 * <p>
	 * 用能力而不是直接访问傀儡字段，是为了让本库保持对模组零依赖（MobWeaponAPI 是 MG 的下游依赖，
	 * 反向引用会形成循环）。{@code CombinedInvWrapper} 这类实现会把双手、装备槽、
	 * 以及傀儡自己的弹药槽都聚合进同一个 {@code IItemHandler}。
	 */
	private static ItemStack findGumball(LivingEntity golem) {
		int slot = findGumballSlot(golem);
		if (slot < 0) return ItemStack.EMPTY;
		var handler = golem.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		return handler.map(h -> h.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
	}

	/** 找到第一个装口香糖堆的槽位；没有返回 -1。 */
	private static int findGumballSlot(LivingEntity golem) {
		var handler = golem.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (handler.isEmpty()) return -1;
		IItemHandler inv = handler.get();
		for (int slot = 0; slot < inv.getSlots(); slot++) {
			ItemStack s = inv.getStackInSlot(slot);
			if (!s.isEmpty() && ShotGumItem.AMMO.test(s)) return slot;
		}
		return -1;
	}

}
