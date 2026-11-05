package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.WaveEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 螺纹长枪。原版右键蓄力，{@code releaseUsing} 时按蓄力时长给自己前冲速度并释放水波。
 * 行为等价于"激流三叉戟"：拉开距离蓄力 → 冲过去。
 * <p>
 * 这里逐条复刻原版 {@code releaseUsing}：蓄力值换算、前冲、左右对称水波、{@code SECOND_WAVE} 二段波、
 * AABB 范围伤害与 {@code stopRiding}。区别只有一处：原版用"视线方向"，傀儡改用<b>目标方向</b>，
 * 因为傀儡的视线不一定朝向目标。
 * <p>
 * 已复刻附魔：{@code FLINGING}、{@code TSUNAMI}、{@code SECOND_WAVE}、{@code SEA_SWING}（近战命中）。
 */
public class OrtholanceBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 12;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 60;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		var golem = user.user();
		var level = golem.level();

		// 原版：i = clamp(useDuration - useTime, 0, 60)，即蓄力 tick 数（上限 60）
		int i = Mth.clamp(time, 0, 60);
		int flinging = stack.getEnchantmentLevel(ACEnchantmentRegistry.FLINGING.get());
		boolean tsunami = stack.getEnchantmentLevel(ACEnchantmentRegistry.TSUNAMI.get()) > 0;
		if (i <= 0) return 20;

		float f = 0.1F * i + flinging * 0.1F;
		// 原版用视线；傀儡改用目标方向（水平）
		Vec3 forward = target != null
				? target.position().subtract(golem.position()).multiply(1, 0, 1)
				: golem.getViewVector(1.0F).multiply(1, 0, 1);
		if (forward.lengthSqr() < 1e-6) forward = golem.getViewVector(1.0F);
		Vec3 dir = forward.normalize();
		Vec3 push = dir.scale(f).add(0, f * 0.15F, 0);
		Vec3 newMotion = golem.getDeltaMovement().add(push);

		if (i >= 10 && !level.isClientSide) {
			level.playSound(null, golem, ACSoundRegistry.ORTHOLANCE_WAVE.get(), SoundSource.NEUTRAL, 4.0F, 1.0F);
			stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(net.minecraft.world.InteractionHand.MAIN_HAND));

			int maxWaves = i / 5;
			float baseYaw = -(float) (Mth.atan2(dir.x, dir.z) * (180F / (float) Math.PI));

			if (tsunami) {
				Vec3 center = golem.position().add(push);
				WaveEntity big = new WaveEntity(level, golem);
				big.setPos(center.x, golem.getY(), center.z);
				big.setLifespan(20);
				big.setWaveScale(5.0F);
				big.setWaitingTicks(2);
				big.setYRot(baseYaw);
				level.addFreshEntity(big);
			} else {
				for (int wave = 0; wave < maxWaves; wave++) {
					float f1 = (float) wave / maxWaves;
					int lifespan = 3 + (int) ((1F - f1) * 3);
					Vec3 center = golem.position().add(push.scale(f1 * 2));
					spawnWave(level, golem, center, lifespan, baseYaw + 60 - 15 * wave, 0);
					spawnWave(level, golem, center, lifespan, baseYaw - 60 + 15 * wave, 0);
				}
				if (stack.getEnchantmentLevel(ACEnchantmentRegistry.SECOND_WAVE.get()) > 0) {
					int maxSecond = Math.max(1, maxWaves - 1);
					for (int wave = 0; wave < maxSecond; wave++) {
						float f1 = (float) wave / maxSecond;
						int lifespan = 3 + (int) ((1F - f1) * 3);
						Vec3 center = golem.position().add(push.scale(f1 * 2));
						spawnWave(level, golem, center, lifespan, baseYaw + 60 - 15 * wave, 8);
						spawnWave(level, golem, center, lifespan, baseYaw - 60 + 15 * wave, 8);
					}
				}
			}

			// 原版的范围伤害
			AABB aabb = new AABB(golem.position(), golem.position().add(push.scale(maxWaves))).inflate(1);
			var source = golem.damageSources().mobAttack(golem);
			double d = 0;
			for (var modifier : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
				d += modifier.getAmount();
			}
			for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
				if (!golem.isAlliedTo(entity) && !golem.equals(entity) && golem.hasLineOfSight(entity)) {
					entity.hurt(source, (float) d);
					entity.stopRiding();
				}
			}
		}

		// 原版最后才把速度写回去（含起跳）
		golem.setDeltaMovement(newMotion.add(0, (golem.onGround() ? 0.2F : 0) + flinging * 0.1F, 0));
		golem.hasImpulse = true;
		setCD(golem, stack, 20);
		return 20;
	}

	private static void spawnWave(net.minecraft.world.level.Level level, LivingEntity owner, Vec3 pos,
								  int lifespan, float yaw, int waiting) {
		WaveEntity w = new WaveEntity(level, owner);
		w.setPos(pos.x, owner.getY(), pos.z);
		w.setLifespan(lifespan);
		w.setYRot(yaw);
		if (waiting > 0) w.setWaitingTicks(waiting);
		level.addFreshEntity(w);
	}

}
