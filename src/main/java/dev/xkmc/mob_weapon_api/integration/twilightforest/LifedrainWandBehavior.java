package dev.xkmc.mob_weapon_api.integration.twilightforest;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.RechargeableInstantBehavior;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import twilightforest.data.tags.EntityTagGenerator;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFSounds;
import twilightforest.util.EntityUtil;

public class LifedrainWandBehavior extends RechargeableInstantBehavior {

	protected LifedrainWandBehavior() {
		super(0, TFIntegration.LIFEDRAIN_WAND);
	}

	@Override
	public double range(ProjectileWeaponUser user, ItemStack stack) {
		return 18;
	}

	@Override
	public void triggerImpl(ProjectileWeaponUser ctx, ItemStack stack, LivingEntity target) {
		var user = ctx.user();
		var level = user.level();
		makeRedMagicTrail((ServerLevel) level, user, target.getEyePosition());
		if (target.hurt(TFDamageTypes.getEntityDamageSource(level, TFDamageTypes.LIFEDRAIN, user), 1.0F) && !level.isClientSide()) {
			if (target.getHealth() <= 1.0F) {
				kill(level, user, target);
			} else {
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2));
				user.heal(1.0F);
			}
			if (!ctx.bypassAllConsumption()) {
				stack.hurt(1, level.getRandom(), null);
			}
		}
		if (target.getHealth() <= user.getHealth()) {
			target.setDeltaMovement(0.0, 0.15, 0.0);
		}
	}

	private void kill(Level level, LivingEntity user, LivingEntity target) {
		if (!target.getType().is(EntityTagGenerator.LIFEDRAIN_DROPS_NO_FLESH)) {
			target.spawnAtLocation(new ItemStack(Items.ROTTEN_FLESH, level.getRandom().nextInt(3)));
			animateTargetShatter((ServerLevel) level, target);
		}
		if (target instanceof Mob mob) {
			mob.spawnAnim();
		}
		target.playSound(TFSounds.SCEPTER_DRAIN.get(), 1.0F, user.getVoicePitch());
		SoundEvent deathSound = EntityUtil.getDeathSound(target);
		if (deathSound != null) {
			level.playSound(null, target.blockPosition(), deathSound, SoundSource.HOSTILE, 1.0F, target.getVoicePitch());
		}
		if (!target.isDeadOrDying()) {
			if (target instanceof Player) {
				target.hurt(TFDamageTypes.getEntityDamageSource(level, TFDamageTypes.LIFEDRAIN, user), Float.MAX_VALUE);
			} else {
				target.die(TFDamageTypes.getEntityDamageSource(level, TFDamageTypes.LIFEDRAIN, user));
				target.discard();
			}
		}
	}

	private static void makeRedMagicTrail(ServerLevel level, LivingEntity source, Vec3 target) {
		int n = 32;
		Vec3 src = source.getEyePosition();
		Vec3 diff = target.subtract(src);
		for (int i = 0; i < n; ++i) {
			double trailFactor = i / (n - 1.0);
			Vec3 pos = src.add(diff.scale(trailFactor));
			level.sendParticles(ParticleTypes.ENTITY_EFFECT, pos.x, pos.y, pos.z,
					0, 1, 0.5f, 0.5f, 1);
		}
	}

	private static void animateTargetShatter(ServerLevel level, LivingEntity target) {
		ItemStack itemId = Items.ROTTEN_FLESH.getDefaultInstance();
		double explosionPower = level.getRandom().nextInt(100) == 0 ? 0.5 : 0.15;
		var r = level.getRandom();
		for (int i = 0; i < 50 + (int) target.getBbWidth() * 75; ++i) {
			Vec3 pos = target.position().add(new Vec3(r.nextFloat(), r.nextFloat(), r.nextFloat())
					.multiply(target.getBbWidth() * 1.5F, target.getBbHeight(), target.getBbWidth() * 1.5F));
			level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemId),
					pos.x, pos.y, pos.z, 1, 0, 0, 0,
					0.05 * explosionPower);
		}
	}

}
