package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.entity.item.DarkArrowEntity;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/**
 * 恐惧之弓（Dreadbow）。它继承 {@code ProjectileWeaponItem} 而不是 {@code BowItem}，兜底谓词覆盖不到，
 * 需要显式登记。
 * <p>
 * 原版 {@code DreadbowItem#releaseUsing} 只接受 {@code Player}，并且把"视线落点"当成箭雨中心。
 * 傀儡没有玩家视线，这里改为以目标位置为箭雨中心，其余（箭数、散布、附魔、消耗）逐条照搬原版。
 * <p>
 * 已复刻的附魔：{@code PRECISE_VOLLEY}（精准齐射，零散布）、{@code SHADED_RESPITE}（暗影喘息，无箭也射且不耗耐久）、
 * {@code TWILIGHT_PERFECTION}（完美射击，伤害翻倍 / 暗箭 perfectShot 标记）、{@code DARK_NOCK}（影响满蓄力时间）。
 */
public class DreadbowBehavior implements IBowBehavior {

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		// 暗影喘息附魔允许在没箭时射击，所以这里放宽
		if (stack.getEnchantmentLevel(ACEnchantmentRegistry.SHADED_RESPITE.get()) > 0) return true;
		return !user.getPreferredProjectile(stack).isEmpty();
	}

	/**
	 * 刺骨黑暗（{@code RELENTLESS_DARKNESS}）：原版 {@code releaseUsing} 在该附魔存在时直接不执行，
	 * 改为在 {@code onUseTick} 里每 3 tick 自动射一发，不需要蓄满。
	 * 这里把保持时间压到 3 tick，让 HOLD 节奏贴合原版的持续射击频率。
	 */
	public static boolean isRelentless(ItemStack stack) {
		return stack.getEnchantmentLevel(ACEnchantmentRegistry.RELENTLESS_DARKNESS.get()) > 0;
	}

	@Override
	public int getStandardPullTime(BowUseContext user, ItemStack stack) {
		// 刺骨黑暗：原版每 3 tick 射一发，所以拉弓时间压到 3
		if (isRelentless(stack)) return 3;
		// 对应原版 getMaxLoadTime：否则 40 - 8*DARK_NOCK
		return 40 - 8 * stack.getEnchantmentLevel(ACEnchantmentRegistry.DARK_NOCK.get());
	}

	@Override
	public float getPowerForTime(BowUseContext user, ItemStack stack, int pullTime) {
		int maxLoad = getStandardPullTime(user, stack);
		float f = (float) pullTime / (float) maxLoad;
		f = (f * f + f * 2.0F) / 3.0F;
		return Math.min(f, 1.0F);
	}

	@Override
	public int shootArrow(BowUseContext user, float power, ItemStack stack, InteractionHand hand) {
		var golem = user.user();
		var level = golem.level();
		if (!(golem instanceof Mob mob) || mob.getTarget() == null) return 10;
		var target = mob.getTarget();

		ItemStack ammo = user.getPreferredProjectile(stack);
		boolean respite = stack.getEnchantmentLevel(ACEnchantmentRegistry.SHADED_RESPITE.get()) > 0
				&& !DarknessIncarnateEffect.isInLight(golem, 11);
		if (respite && ammo.isEmpty()) ammo = new ItemStack(Items.ARROW);
		if (ammo.isEmpty()) return 10;

		// 刺骨黑暗：不抛箭雨，改为朝视线直射（对应原版 onUseTick 的分支）
		if (isRelentless(stack)) {
			return shootRelentless(golem, stack, ammo, target, user);
		}

		float f = Math.max(power, 0.1F);
		if (f <= 0.1F) return 10;

		boolean precise = stack.getEnchantmentLevel(ACEnchantmentRegistry.PRECISE_VOLLEY.get()) > 0;
		boolean perfectShot = stack.getEnchantmentLevel(ACEnchantmentRegistry.TWILIGHT_PERFECTION.get()) > 0;

		golem.playSound(ACSoundRegistry.DREADBOW_RELEASE.get(), 1.0F, 1.0F);

		// 原版用视线命中点做箭雨中心，傀儡改用目标位置
		Vec3 hit = target.position();
		BlockPos.MutableBlockPos skyPos = new BlockPos.MutableBlockPos(hit.x, hit.y + 1.5, hit.z);
		int maxFallHeight = 15;
		int k = 0;
		while (skyPos.getY() < level.getMaxBuildHeight() && level.isEmptyBlock(skyPos) && k < maxFallHeight) {
			skyPos.setY(skyPos.getY() + 1);
			k++;
		}

		AbstractArrow probe = ProjectileUtil.getMobArrow(golem, ammo, f);
		boolean darkArrows = probe instanceof Arrow arrow && arrow.getColor() == -1;
		int maxArrows = darkArrows ? 30 : 8;
		int count = (int) Math.ceil(maxArrows * f);

		for (int j = 0; j < count; j++) {
			AbstractArrow projectile;
			if (darkArrows) {
				DarkArrowEntity dark = new DarkArrowEntity(level, golem);
				dark.setShadowArrowDamage(precise ? 2.0F : 3.0F);
				dark.setPerfectShot(perfectShot);
				projectile = dark;
			} else {
				projectile = ProjectileUtil.getMobArrow(golem, ammo, f);
				if (perfectShot) projectile.setBaseDamage(projectile.getBaseDamage() * 2.0F);
			}

			// 在中心上方随机取一个无遮挡的落点
			Vec3 spawn = skyPos.getCenter().add(
					level.random.nextFloat() * 16 - 8,
					level.random.nextFloat() * 4 - 2,
					level.random.nextFloat() * 16 - 8);
			int clearTries = 0;
			while (clearTries < 6 && !level.isEmptyBlock(BlockPos.containing(spawn))
					&& level.getFluidState(BlockPos.containing(spawn)).isEmpty()) {
				clearTries++;
				spawn = skyPos.getCenter().add(
						level.random.nextFloat() * 16 - 8,
						level.random.nextFloat() * 4 - 2,
						level.random.nextFloat() * 16 - 8);
			}
			if (!level.isEmptyBlock(BlockPos.containing(spawn))
					&& level.getFluidState(BlockPos.containing(spawn)).isEmpty()) {
				spawn = skyPos.getCenter();
			}

			projectile.setPos(spawn);
			Vec3 dir = hit.subtract(spawn);
			float randomness = precise ? 0.0F : (darkArrows ? 20F : 5F) + level.random.nextFloat() * 10F;
			if (!precise && level.random.nextFloat() < 0.25F) {
				randomness = level.random.nextFloat();
			}
			projectile.shoot(dir.x, dir.y, dir.z, 0.5F + 1.5F * level.random.nextFloat(), randomness);
			projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			level.addFreshEntity(projectile);
		}

		if (darkArrows) {
			level.playSound(null, hit.x, hit.y, hit.z, ACSoundRegistry.DREADBOW_RAIN.get(),
					SoundSource.PLAYERS, 12.0F, 1.0F);
		}

		if (!user.bypassAllConsumption()) {
			if (!respite) {
				stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(hand));
			}
			if (!respite || !ammo.is(Items.ARROW)) {
				ammo.shrink(1);
			}
		}
		return 10;
	}

	/**
	 * 刺骨黑暗分支：每 3 tick 朝视线方向直射一发（暗箭时 1~2 发），不抛箭雨。
	 */
	private int shootRelentless(net.minecraft.world.entity.LivingEntity golem, ItemStack stack,
								ItemStack ammo, net.minecraft.world.entity.LivingEntity target, BowUseContext user) {
		var level = golem.level();
		boolean respite = stack.getEnchantmentLevel(ACEnchantmentRegistry.SHADED_RESPITE.get()) > 0
				&& !DarknessIncarnateEffect.isInLight(golem, 11);
		AbstractArrow probe = ProjectileUtil.getMobArrow(golem, ammo, 1.0F);
		boolean darkArrows = probe instanceof Arrow arrow && arrow.getColor() == -1;
		int count = darkArrows ? 1 + golem.getRandom().nextInt(2) : 1;
		float randomness = 0.5F;
		// 朝目标而不是朝视线（AI 的视线未必已经转到位）
		Vec3 look = target != null
				? target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(golem.getEyePosition()).normalize()
				: golem.getViewVector(1.0F);
		golem.playSound(ACSoundRegistry.DREADBOW_RELEASE.get(), 1.0F, 1.0F);
		for (int i = 0; i < count; i++) {
			AbstractArrow projectile;
			if (darkArrows) {
				DarkArrowEntity dark = new DarkArrowEntity(level, golem);
				dark.setShadowArrowDamage(2.0F);
				projectile = dark;
			} else {
				projectile = ProjectileUtil.getMobArrow(golem, ammo, 1.0F);
			}
			projectile.setPos(projectile.position().add(
					level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F));
			projectile.shoot(look.x, look.y, look.z, 4F + 3F * level.random.nextFloat(), randomness);
			randomness += 2.0F;
			projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			level.addFreshEntity(projectile);
		}
		if (!user.bypassAllConsumption()) {
			if (!respite) stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
			if (!respite || !ammo.is(Items.ARROW)) ammo.shrink(1);
		}
		return 3;
	}

}
