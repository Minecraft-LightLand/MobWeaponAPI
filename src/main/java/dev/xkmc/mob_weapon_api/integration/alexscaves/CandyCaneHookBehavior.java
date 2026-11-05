package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.item.CandyCaneHookItem;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 拐杖糖钩锁。原版 {@code CandyCaneHookItem#use} 发射 {@code CandyCaneHookEntity} 把敌人勾过来，
 * 再次使用时把钩子收回（此时会把自身拉过去）。两个分支都只接受 {@code Player}。
 * <p>
 * 定位为<b>远程武器</b>，用 INSTANT 节奏（一触发就执行，不需要蓄力）：
 * <ul>
 *   <li>没有钩子在飞 → 发射；</li>
 *   <li>钩子已勾住目标/方块 → 拉回；</li>
 *   <li>钩子还在飞但没勾中 → 什么都不做，等下一轮（不会空拉）。</li>
 * </ul>
 * 拉回后目标被拽到近身，由 {@code checkSwitch} 自然切换到近战。
 */
public class CandyCaneHookBehavior implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 20;
	}

	/**
	 * 钩锁的作用就是把敌人拽过来，傀儡必须贴近才有意义，不能像弓那样边打边退。
	 */
	@Override
	public boolean keepDistance() {
		return false;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		var golem = user.user();
		var level = golem.level();
		if (level.isClientSide) return 5;

		CandyCaneHookEntity hook = findHook(golem, stack);
		if (hook != null) {
			// 勾中目标/方块，或钩子已经飞了 20 tick（1 秒）→ 收回。
			// 加时间上限是为了避免"永远没勾中就永远不收"的死等。
			boolean hooked = hook.getHookedEntity() != null || hook.getHookedPosition() != null;
			boolean timeout = hook.tickCount >= 20;
			if (hooked || timeout) {
				CandyCaneHookItem.setReelingIn(stack, true);
				hook.setReeling(true);
				stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
				level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
						ACSoundRegistry.CANDY_CANE_HOOK_REEL.get(), SoundSource.NEUTRAL, 1.0F,
						0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
			}
			// 没勾中：不拉回，等下一轮
			setCD(golem, stack, 5);
			return 5;
		}
		// 没有钩子在飞，但 NBT 还留着 LastLaunchedHookUUID：清理掉。
		// CandyCaneHookItem#isActive 只看这个 UUID 存不存在，不清的话物品会永远停在
		// "已扔出"状态——玩家版靠 inventoryTick 里"物品不在手上就收线"那条自动清理，
		// 傀儡的装备槽不走背包 tick，所以永远清不掉。
		if (CandyCaneHookItem.isActive(stack)) {
			CandyCaneHookItem.setLastLaunchedHookUUID(stack, null);
			CandyCaneHookItem.setReelingIn(stack, false);
		}

		level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				ACSoundRegistry.CANDY_CANE_HOOK_LAUNCH.get(), SoundSource.NEUTRAL, 0.5F,
				0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
		CandyCaneHookEntity newHook = ACEntityRegistry.CANDY_CANE_HOOK.get().create(level);
		if (newHook != null) {
			newHook.setOwner(golem);
			newHook.setReeling(false);
			// 朝目标发射，而不是朝傀儡的视线方向：AI 的视线未必已经转到位，
			// 用 getLookAngle() 会出现「钩子从敌人旁边飞过去」。
			var look = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(golem.getEyePosition());
			if (look.lengthSqr() < 1.0E-6) {
				look = golem.getLookAngle();
			} else {
				look = look.normalize();
			}
			newHook.moveTo(golem.getX() + look.x * 0.7, golem.getEyeY(), golem.getZ() + look.z * 0.7,
					golem.getYRot(), golem.getXRot());
			double spread = 0.5D + stack.getEnchantmentLevel(ACEnchantmentRegistry.FAR_FLUNG.get()) * 0.2D;
			newHook.setDeltaMovement(look.scale(spread + 0.5D));
			newHook.setYRot((float) (Math.atan2(look.x, look.z) * (180F / (float) Math.PI)));
			newHook.setXRot((float) (Math.atan2(look.y, look.horizontalDistance()) * (180F / (float) Math.PI)));
			newHook.setOffhand(false);
			newHook.yRotO = newHook.getYRot();
			newHook.xRotO = newHook.getXRot();
			newHook.setDamage(stack.getEnchantmentLevel(ACEnchantmentRegistry.SHARP_CANE.get()) * 3.0F);
			level.addFreshEntity(newHook);
			CandyCaneHookItem.setLastLaunchedHookUUID(stack, newHook.getUUID());
			CandyCaneHookItem.setReelingIn(stack, false);
		}
		golem.swing(InteractionHand.MAIN_HAND);
		setCD(golem, stack, 5);
		return 5;
	}

	/**
	 * 找当前属于这个傀儡的钩子实体。
	 * <p>
	 * 优先按物品 NBT 里记录的 UUID 查；查不到时退回"扫描周围同类实体并按 owner 匹配"——
	 * NBT 在某些情况下会被清掉（物品被复制、或 {@code setLastLaunchedHookUUID} 未落盘），
	 * 只靠 UUID 会导致"找不到钩子 → 每 tick 都当新钩子发射"，或反之"永远以为没有钩子在飞"。
	 */
	private static CandyCaneHookEntity findHook(LivingEntity golem, ItemStack stack) {
		if (!(golem.level() instanceof net.minecraft.server.level.ServerLevel sl)) return null;
		var uuid = CandyCaneHookItem.getLaunchedHookUUID(stack);
		if (uuid != null) {
			var e = sl.getEntity(uuid);
			if (e instanceof CandyCaneHookEntity hook && hook.isAlive()) return hook;
		}
		// 退路：按 owner 扫描
		for (var hook : sl.getEntitiesOfClass(CandyCaneHookEntity.class, golem.getBoundingBox().inflate(48))) {
			var owner = hook.getOwner();
			if (owner != null && owner.getUUID().equals(golem.getUUID()) && hook.isAlive()) {
				return hook;
			}
		}
		return null;
	}

}
