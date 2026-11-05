package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.WaterBoltEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInstantWeaponBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 海之权杖。原版 {@code SeaStaffItem#use} 只接受 {@code Player}，这里按同一套参数自行生成水弹。
 * <p>
 * 已复刻的附魔：{@code TRIPLE_SPLASH}（三重飞溅，三发扇形）、{@code SOAK_SEEKING}（浸透追踪，追踪强度）、
 * {@code ENVELOPING_BUBBLE}（包裹气泡）、{@code BOUNCING_BOLT}（弹跳水弹）。
 * 原版靠"视线最近的实体"作为追踪目标，傀儡改为直接追踪当前目标。
 */
public class SeaStaffBehavior implements IInstantWeaponBehavior {

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 32;
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target) {
		var golem = user.user();
		var level = golem.level();
		var hand = InteractionHand.MAIN_HAND;
		level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				ACSoundRegistry.SEA_STAFF_CAST.get(), SoundSource.PLAYERS, 0.5F,
				level.getRandom().nextFloat() * 0.45F + 0.75F);
		golem.swing(hand);

		float seekAmount = stack.getEnchantmentLevel(ACEnchantmentRegistry.SOAK_SEEKING.get());
		if (!level.isClientSide) {
			int bolts = stack.getEnchantmentLevel(ACEnchantmentRegistry.TRIPLE_SPLASH.get()) > 0 ? 3 : 1;
			for (int i = 0; i < bolts; i++) {
				float shootRot = i == 0 ? 0 : i == 1 ? -50 : 50;
				WaterBoltEntity bolt = new WaterBoltEntity(level, golem);
				float rot = golem.yHeadRot + 45;
				bolt.setPos(
						golem.getX() - (double) golem.getBbWidth() * 1.1F * (double) Mth.sin(rot * ((float) Math.PI / 180F)),
						golem.getEyeY() - 0.4F,
						golem.getZ() + (double) golem.getBbWidth() * 1.1F * (double) Mth.cos(rot * ((float) Math.PI / 180F)));
				// 朝目标而不是朝视线：AI 的视线未必已经转到位，用 getXRot/getYRot 会让水弹从敌人旁边飞过。
				float aimYaw = golem.getYRot() + shootRot;
				float aimPitch = golem.getXRot();
				if (target != null) {
					Vec3 to = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(golem.getEyePosition());
					aimYaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90.0F + shootRot;
					aimPitch = (float) -(Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG);
				}
				bolt.shootFromRotation(golem, aimPitch, aimYaw, -20.0F,
						i > 0 ? 1F : 2F, 12F);
				if (stack.getEnchantmentLevel(ACEnchantmentRegistry.ENVELOPING_BUBBLE.get()) > 0) {
					bolt.setBubbling(golem.getRandom().nextBoolean());
				}
				if (stack.getEnchantmentLevel(ACEnchantmentRegistry.BOUNCING_BOLT.get()) > 0) {
					bolt.ricochet = true;
				}
				bolt.seekAmount = 0.3F + seekAmount * 0.2F;
				bolt.setArcingTowards(target.getUUID());
				level.addFreshEntity(bolt);
			}
			if (!user.bypassAllConsumption()) {
				stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(hand));
			}
		}
		setCD(golem, stack, 20);
		return 20;
	}

}
