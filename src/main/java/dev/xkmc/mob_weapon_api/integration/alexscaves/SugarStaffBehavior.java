package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SpinningPeppermintEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SugarStaffHexEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 糖果权杖。
 * <p>
 * 原版有潜行 / 非潜行两种用法，傀儡没有潜行状态，这里按<b>目标距离</b>选：
 * 目标在 2 格内用潜行分支（{@code SugarStaffHexEntity} 持续伤害法阵），否则用常规的旋转薄荷糖。
 * <p>
 * 已复刻的附魔：{@code MULTIPLE_MINT}（数量 +1/级）、{@code PEPPERMINT_PUNTING}（直线弹出，朝目标）、
 * {@code SEEKCANDY}（追踪目标，转速与寿命提升）、{@code HUMUNGOUS_HEX}（法阵半径）、
 * {@code SPELL_LASTING}（法阵寿命）。
 */
public class SugarStaffBehavior implements IInstantWeaponBehavior {

	/** 目标贴到这个距离内改用潜行分支的法阵。 */
	private static final double HEX_RANGE = 2.0;

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		// 默认的旋转糖果只能贴身伤害，远程拉扯距离压短一些，免得傀儡一直在 16 格外空转；
		// 有凌空抽射（直线弹道）或追踪附魔时才值得拉开。
		boolean straight = stack.getEnchantmentLevel(ACEnchantmentRegistry.PEPPERMINT_PUNTING.get()) > 0;
		boolean seek = stack.getEnchantmentLevel(ACEnchantmentRegistry.SEEKCANDY.get()) > 0;
		return (straight || seek) ? 16 : 4;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		var golem = user.user();
		var level = golem.level();
		var hand = InteractionHand.MAIN_HAND;
		if (!level.isClientSide) {
			if (target != null && golem.distanceTo(target) <= HEX_RANGE) {
				shootHex(golem, stack, level);
				if (!user.bypassAllConsumption()) {
					stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(hand));
				}
				golem.swing(hand);
				setCD(golem, stack, 100);
				return 100;
			}
			shootMints(golem, stack, target, level);
			if (!user.bypassAllConsumption()) {
				stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(hand));
			}
		}
		golem.swing(hand);
		setCD(golem, stack, 100);
		return 100;
	}

	/** 潜行分支：脚下铺一个持续伤害法阵。 */
	private static void shootHex(LivingEntity golem, ItemStack stack, net.minecraft.world.level.Level level) {
		SugarStaffHexEntity hex = ACEntityRegistry.SUGAR_STAFF_HEX.get().create(level);
		if (hex == null) return;
		hex.setOwner(golem);
		hex.setPos(golem.getX(), golem.getY(), golem.getZ());
		hex.setHexScale(1.0F + 0.25F * stack.getEnchantmentLevel(ACEnchantmentRegistry.HUMUNGOUS_HEX.get()));
		hex.setLifespan(100 + 60 * stack.getEnchantmentLevel(ACEnchantmentRegistry.SPELL_LASTING.get()));
		level.addFreshEntity(hex);
		level.playSound(null, golem.blockPosition(), ACSoundRegistry.SUGAR_STAFF_CAST_HEX.get(),
				SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	/** 常规分支：在施法者四周生成数枚旋转薄荷糖。 */
	private static void shootMints(LivingEntity golem, ItemStack stack, LivingEntity target,
			net.minecraft.world.level.Level level) {
		int spawnIn = 3 + stack.getEnchantmentLevel(ACEnchantmentRegistry.MULTIPLE_MINT.get());
		// 凌空抽射是直线弹道，必须朝目标而不是朝视线（AI 的视线未必已经转到位）
		float baseYaw = 180 + golem.yHeadRot;
		if (target != null) {
			Vec3 to = target.position().subtract(golem.position());
			baseYaw = (float) (Mth.atan2(to.x, -to.z) * Mth.RAD_TO_DEG);
		}
		for (int i = 0; i < spawnIn; i++) {
			SpinningPeppermintEntity mint = ACEntityRegistry.SPINNING_PEPPERMINT.get().create(level);
			if (mint == null) break;
			mint.setPos(golem.position().add(0, golem.getBbHeight() * 0.45F, 0));
			if (stack.getEnchantmentLevel(ACEnchantmentRegistry.PEPPERMINT_PUNTING.get()) > 0) {
				mint.setStraight(true);
				mint.setYRot(baseYaw + (i - 1) * 15);
				mint.setSpinSpeed(8F);
			} else {
				mint.setStraight(false);
				mint.setYRot(180 + (i - 1) * 30);
				mint.setSpinSpeed(12F);
			}
			if (target != null && stack.getEnchantmentLevel(ACEnchantmentRegistry.SEEKCANDY.get()) > 0) {
				mint.setSeekingEntityId(target.getId());
				mint.setSpinSpeed(50F);
			}
			mint.setSpinRadius(3.5F);
			mint.setOwner(golem);
			mint.setStartAngle(i * 360 / (float) spawnIn);
			mint.setLifespan(80);
			level.addFreshEntity(mint);
		}
		level.playSound(null, golem.blockPosition(), ACSoundRegistry.SUGAR_STAFF_CAST_PEPPERMINT.get(),
				SoundSource.PLAYERS, 1.0F, 1.0F);
	}

}
