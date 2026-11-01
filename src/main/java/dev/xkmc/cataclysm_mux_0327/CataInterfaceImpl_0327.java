package dev.xkmc.cataclysm_mux_0327;

import com.github.L_Ender.cataclysm.client.particle.Options.AfterImageParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.Options.StormParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.Options.RoarParticleOptions;
import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Portal_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Mine_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Orb_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Portal_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Dimensional_Rift_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Portal_Abyss_Blast_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity;
import com.github.L_Ender.cataclysm.entity.effect.*;
import com.github.L_Ender.cataclysm.entity.projectile.*;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.github.L_Ender.cataclysm.init.ModTag;
import com.github.L_Ender.cataclysm.util.AttributeUtils;
import com.github.L_Ender.cataclysm.util.CMDamageTypes;
import com.github.L_Ender.cataclysm.util.EntityUtil;
import dev.xkmc.cataclysm_mux.CataInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CataInterfaceImpl_0327 implements CataInterface {

	// 逻辑
	public boolean isLaser(DamageSource source) {
		return source.getDirectEntity() instanceof Death_Laser_Beam_Entity;
	}

	public boolean isMissile(DamageSource source) {
		return source.getDirectEntity() instanceof Wither_Homing_Missile_Entity;
	}

	public boolean isWitherMissile(DamageSource source) {
		return source.getDirectEntity() instanceof Wither_Missile_Entity;
	}

	public boolean isHowitzer(DamageSource source) {
		return source.getDirectEntity() instanceof Wither_Howitzer_Entity;
	}

	public boolean isSandstorm(DamageSource source) {
		return source.getDirectEntity() instanceof Sandstorm_Entity;
	}

	public boolean isIgnisExplosive(Entity entity) {
		return entity instanceof Ignis_Fireball_Entity ||
				entity instanceof Ignis_Abyss_Fireball_Entity;
	}

	public boolean isSoul(Entity entity) {
		return entity instanceof Ignis_Fireball_Entity e && e.isSoul() ||
				entity instanceof Ignis_Abyss_Fireball_Entity ||
				entity instanceof Flame_Strike_Entity x && x.isSoul();

	}

	public boolean isAbyssFireball(Entity entity) {
		return entity instanceof Ignis_Abyss_Fireball_Entity;
	}

	public boolean isIgnisStrike(Entity entity) {
		return entity instanceof Flame_Strike_Entity;
	}

	public int getSandCurseLevel(LivingEntity e) {
		var ins = e.getEffect(ModEffect.EFFECTCURSE_OF_DESERT.get());
		if (ins == null) return 0;
		return ins.getAmplifier() + 1;

	}

	@Nullable
	public Entity addLaserBeam(LivingEntity user, int dur) {
		Death_Laser_Beam_Entity beam = new Death_Laser_Beam_Entity(ModEntities.DEATH_LASER_BEAM.get(),
				user.level(), user, user.getX(), user.getEyeY(), user.getZ(),
				(user.yHeadRot + 90.0F) * Mth.DEG_TO_RAD,
				-user.getXRot() * Mth.DEG_TO_RAD, dur, (float) CMCommonConfig.Harbinger.DeathLaserdamage, (float) CMCommonConfig.Harbinger.DeathLaserHpdamage);
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.DEATH_LASER.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(beam);
		return beam;
	}

	public float monstrosityEarthquakeDamage() {

		return (float) CMCommonConfig.NetheriteMonstrosity.SmashHpdamage;


	}

	public float maledictusEarthquakeDamage() {

		return (float) CMCommonConfig.Maledictus.SmashHpDamage;


	}

	public void updateLaser(LivingEntity golem, Entity e) {
		if (e instanceof Death_Laser_Beam_Entity beam) {
			beam.setYaw((float) ((golem.yHeadRot + 90.0F) * Math.PI / 180.0F));
			beam.setPitch((float) ((-golem.getXRot()) * Math.PI / 180.0F));
		}
		if (e instanceof Abyss_Blast_Entity beam) {
			beam.setYaw((float) ((golem.yHeadRot + 90.0F) * Math.PI / 180.0F));
			beam.setPitch((float) ((-golem.getXRot()) * Math.PI / 180.0F));
		}

	}

	public void stackBlazingBrand(LivingEntity user, LivingEntity target, float factor) {

		var eff = ModEffect.EFFECTBLAZING_BRAND.get();
		var old = target.getEffect(eff);
		int i = old == null ? 0 : Math.min(4, old.getAmplifier() + 1);
		MobEffectInstance ins = new MobEffectInstance(eff, 240, i, false, true, true);
		target.addEffect(ins);
		user.heal(factor * (float) CMCommonConfig.Ignis.HealingMultiplier * (float) (i + 1));

	}

	public void golemStackBlazingBrandRaw(LivingEntity golem, LivingEntity target, float dmg, int min) {

		var eff = ModEffect.EFFECTBLAZING_BRAND.get();
		var old = target.getEffect(eff);
		int i = old == null ? 0 : Math.min(4, old.getAmplifier() + 1);
		long time = target.getPersistentData().getLong("BlazingBrandApplyTime");
		long current = golem.level().getGameTime();
		if (time != current) {
			target.getPersistentData().putLong("BlazingBrandApplyTime", current);
			MobEffectInstance ins = new MobEffectInstance(eff, 240, i, false, true, true);
			target.addEffect(ins);
		}
		golem.heal(dmg * Math.max(min, i + 1));

	}

	public void inflictStun(LivingEntity user, LivingEntity target, int time) {

		var eff = ModEffect.EFFECTSTUN.get();
		MobEffectInstance ins = new MobEffectInstance(eff, time, 0, false, true, true);
		target.addEffect(ins);

	}

	@Nullable
	public LivingEntity getOwner(Entity entity) {

		if (entity instanceof Portal_Abyss_Blast_Entity e) {
			return e.caster;
		}
		if (entity instanceof Death_Laser_Beam_Entity e) {
			return e.caster;
		}
		if (entity instanceof Abyss_Blast_Entity e) {
			return e.caster;
		}

		return null;
	}

// 攻击 - 焰魔

	public void shootFireball(LivingEntity user, Vec3 shotAt, int timer, boolean abyss, boolean isBlue) {

		shotAt = shotAt.yRot(-user.getYRot() * 0.017453292F);
		Projectile shot;
		if (abyss) {
			var bullet = new Ignis_Abyss_Fireball_Entity(user.level(), user);
			bullet.setUp(timer);
			shot = bullet;
		} else {
			var bullet = new Ignis_Fireball_Entity(user.level(), user);
			bullet.setUp(timer);
			if (isBlue) {
				bullet.setSoul(true);
			}
			shot = bullet;
		}
		float rot = user.yBodyRot * 0.017453292F;
		double width = (user.getBbWidth() + 1.0F) * 0.15D;
		shot.setPos(user.getX() - width * Mth.sin(rot),
				user.getY() + 1.0D,
				user.getZ() + width * Mth.cos(rot));
		double d0 = shotAt.x;
		double d1 = shotAt.y;
		double d2 = shotAt.z;
		float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
		shot.shoot(d0, d1 + f, d2, 0.25F, 3.0F);
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.FLAME_BURST.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(shot);

	}

	public void createBlast(LivingEntity user, Vec3 pos, int dur, int delay, float radius, float dmg, boolean soul) {

		user.level().playSound(null, pos.x, pos.y, pos.z, ModSounds.FLAME_BURST.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(
				new Flame_Strike_Entity(user.level(), pos.x, pos.y, pos.z,
						user.getYRot(), dur, delay, delay, radius,
						dmg, 6f, soul, user));


	}

// 攻击 - 焰魔

	public void ignisEarthquakeFX(LivingEntity user, float shakeRadius) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.TOTEM_USE, user.getSoundSource(), 1.5F, 0.8F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), shakeRadius, 0.3F, 0, 20);
	}

	public void ignisAreaPunch(LivingEntity user, float radius) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		DamageSource source = user.damageSources().mobAttack(user);
		float base = (float) (user.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F);
		double bodyRot = user.yBodyRot % 360;
		if (bodyRot < 0) bodyRot += 360;
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(radius, 2.5F, radius))) {
			if (e == user || user.isAlliedTo(e)) continue;
			float dist = (float) Math.sqrt((e.getZ() - user.getZ()) * (e.getZ() - user.getZ()) + (e.getX() - user.getX()) * (e.getX() - user.getX()));
			if (dist > radius) continue;
			float angle = (float) ((Math.atan2(e.getZ() - user.getZ(), e.getX() - user.getX()) * (180 / Math.PI) - 90) % 360);
			if (angle < 0) angle += 360;
			double rel = angle - bodyRot;
			if ((rel <= 22.5F && rel >= -22.5F) || (rel >= 360 - 22.5F || rel <= -360 + 22.5F)) {
				if (e.hurt(source, base + e.getMaxHealth() * 0.15F)) {
					if (e.isDamageSourceBlocked(source) && e instanceof Player player) {
						EntityUtil.disableShield(player, 200);
					}
				}
			}
		}
	}

	public void ignisQuakeWave(LivingEntity user, int distance, float mxy, float vec, boolean grab, float damage, float hpdamage, float airborne, boolean blue) {
		ignisQuakeWaveAt(user, user.position(), distance, mxy, vec, grab, damage, hpdamage, airborne, blue);
	}

	public void ignisQuakeWaveAt(LivingEntity user, Vec3 center, int distance, float mxy, float vec, boolean grab, float damage, float hpdamage, float airborne, boolean blue) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		double bodyRotRad = user.yBodyRot * (Math.PI / 180.0);
		double sinBodyRot = Math.sin(bodyRotRad);
		double cosBodyRot = Math.cos(bodyRotRad);
		double facingAngle = bodyRotRad + Math.PI / 2.0;
		double baseX = center.x + vec * -sinBodyRot;
		double baseZ = center.z + vec * cosBodyRot;
		int hitY = Mth.floor(center.y - 0.5);
		double spread = Math.PI * 2.0;
		int arcLen = Mth.ceil(distance * spread);
		double minY = center.y - 1;
		double maxY = center.y + mxy;
		float factor = 1.0F - (float) distance / 12.0F;
		DamageSource source = user.damageSources().mobAttack(user);
		float baseDamage = (float) (user.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage);
		var particle = blue ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
		for (int i = 0; i < arcLen; i++) {
			double theta = (arcLen > 1 ? (double) i / (arcLen - 1) : 0.5);
			theta = (theta - 0.5) * spread + facingAngle;
			double vx = Math.cos(theta);
			double vz = Math.sin(theta);
			double px = baseX + vx * distance;
			double pz = baseZ + vz * distance;
			int hitX = Mth.floor(px);
			int hitZ = Mth.floor(pz);
			BlockPos pos = new BlockPos(hitX, hitY, hitZ);
			BlockState block = level.getBlockState(pos);
			for (int depth = 0; depth < 256; depth++) {
				if (block.getRenderShape() == RenderShape.MODEL) break;
				pos = pos.below();
				block = level.getBlockState(pos);
			}
			if (block.getRenderShape() == RenderShape.MODEL) {
				Cm_Falling_Block_Entity falling = new Cm_Falling_Block_Entity(level, hitX + 0.5D, pos.getY() + 1.0D, hitZ + 0.5D, block, 10);
				falling.push(0, 0.2D + user.getRandom().nextGaussian() * 0.15D, 0);
				level.addFreshEntity(falling);
			}
			level.addParticle(particle, px, maxY, pz, 0, 0.05, 0);
			AABB selection = new AABB(px - 0.5, minY, pz - 0.5, px + 0.5, maxY, pz + 0.5);
			for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, selection)) {
				if (entity != user && !user.isAlliedTo(entity)) {
					float finalDamage = baseDamage + entity.getMaxHealth() * hpdamage;
					if (entity.hurt(source, finalDamage)) {
						if (grab) {
							double magnitude = -4;
							double x = vx * (1 - factor) * magnitude;
							double y = entity.onGround() ? 0.15 : 0.0;
							double z = vz * (1 - factor) * magnitude;
							entity.setDeltaMovement(entity.getDeltaMovement().add(x, y, z));
						} else {
							entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, airborne * distance + level.random.nextDouble() * 0.15, 0.0D));
						}
					}
				}
			}
		}
	}

	public void ignisQuakePush(LivingEntity user, int distance, float mxy, float vec, float damage, float hpdamage, float airborne, boolean blue) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		double bodyRotRad = user.yBodyRot * (Math.PI / 180.0);
		double sinBodyRot = Math.sin(bodyRotRad);
		double cosBodyRot = Math.cos(bodyRotRad);
		double facingAngle = bodyRotRad + Math.PI / 2.0;
		double baseX = user.getX() + vec * -sinBodyRot;
		double baseZ = user.getZ() + vec * cosBodyRot;
		int hitY = Mth.floor(user.getBoundingBox().minY - 0.5);
		double spread = Math.PI * 2.0;
		int arcLen = Mth.ceil(distance * spread);
		double minY = user.getY() - 1;
		double maxY = user.getY() + mxy;
		float factor = 1.0F - (float) distance / 12.0F;
		DamageSource source = user.damageSources().mobAttack(user);
		float baseDamage = (float) (user.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage);
		var particle = blue ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
		for (int i = 0; i < arcLen; i++) {
			double theta = (arcLen > 1 ? (double) i / (arcLen - 1) : 0.5);
			theta = (theta - 0.5) * spread + facingAngle;
			double vx = Math.cos(theta);
			double vz = Math.sin(theta);
			double px = baseX + vx * distance;
			double pz = baseZ + vz * distance;
			int hitX = Mth.floor(px);
			int hitZ = Mth.floor(pz);
			BlockPos pos = new BlockPos(hitX, hitY, hitZ);
			BlockState block = level.getBlockState(pos);
			for (int depth = 0; depth < 256; depth++) {
				if (block.getRenderShape() == RenderShape.MODEL) break;
				pos = pos.below();
				block = level.getBlockState(pos);
			}
			if (block.getRenderShape() == RenderShape.MODEL) {
				Cm_Falling_Block_Entity falling = new Cm_Falling_Block_Entity(level, hitX + 0.5D, pos.getY() + 1.0D, hitZ + 0.5D, block, 10);
				falling.push(0, 0.2D + user.getRandom().nextGaussian() * 0.15D, 0);
				level.addFreshEntity(falling);
			}
			level.addParticle(particle, px, maxY, pz, 0, 0.05, 0);
			AABB selection = new AABB(px - 0.5, minY, pz - 0.5, px + 0.5, maxY, pz + 0.5);
			for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, selection)) {
				if (entity != user && !user.isAlliedTo(entity)) {
					float finalDamage = baseDamage + entity.getMaxHealth() * hpdamage;
					if (entity.hurt(source, finalDamage)) {
						double magnitude = 4;
						double x = vx * (1 - factor) * magnitude;
						double y = airborne * 0.7 + (entity.onGround() ? 0.2 : 0.0);
						double z = vz * (1 - factor) * magnitude;
						entity.setDeltaMovement(entity.getDeltaMovement().add(x, y, z));
					}
				}
			}
		}
	}

	// 焰魔 - 骨风暴

	public void ignisBoneStorm(LivingEntity user, float radius) {
		if (user.level().isClientSide()) return;
		user.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 0.75F);
		for (int i = 0; i < 8; ++i) {
			float angle = (float) Math.toRadians(user.getYRot() + 90.0F) + (float) i * (float) Math.PI / 4.0F;
			double x = user.getX() + (double) Mth.cos(angle) * radius;
			double y = user.getY() + (double) user.getBbHeight() * 0.62D;
			double z = user.getZ() + (double) Mth.sin(angle) * radius;
			Blazing_Bone_Entity bone = new Blazing_Bone_Entity(user.level(), (float) CMCommonConfig.IgnitedRevenant.BlazingBoneDamage, user);
			bone.moveTo(x, y, z, (float) i * 45.0F, user.getXRot());
			bone.shoot(Mth.cos(angle), 0.0D, Mth.sin(angle), 0.5F, 1.0F);
			user.level().addFreshEntity(bone);
		}
	}

// 攻击 - 先驱者

	public void addMissile(LivingEntity user, LivingEntity target, Vec3 pos) {

		var diff = target.getEyePosition().subtract(pos).normalize();
		Wither_Homing_Missile_Entity laserBeam = new Wither_Homing_Missile_Entity(user, diff, user.level(), (float) CMCommonConfig.Harbinger.WitherMissiledamage, target);
		laserBeam.setPosRaw(pos.x(), pos.y(), pos.z());
		user.level().playSound(null, pos.x(), pos.y(), pos.z(), ModSounds.HARBINGER_PREPARE.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(laserBeam);

	}

// 攻击 - 远古遗魂

	public void sandstormAttack(LivingEntity golem, LivingEntity target, int life) {

		Vec3 diff = target.position().subtract(golem.position()).normalize();
		float angle = (float) Math.atan2(diff.z, diff.x);
		double sx = target.getX();
		double sy = target.getY();
		double sz = target.getZ();
		Sandstorm_Entity projectile = new Sandstorm_Entity(golem.level(), sx, sy, sz, life, angle, golem);
		golem.level().playSound(null, sx, sy, sz, ModSounds.SANDSTORM.get(), SoundSource.HOSTILE, 0.8F, 1.0F);
		golem.level().addFreshEntity(projectile);

	}

	public void launchTornado(LivingEntity user, Vec3 dir) {
		Level worldIn = user.level();
		Vec3 offset = dir.multiply(1, 0, 1);
		double x = user.getX() + offset.x;
		double z = user.getZ() + offset.z;
		Sandstorm_Projectile b = new Sandstorm_Projectile(user, dir.x, dir.y, dir.z, user.level(), 6.0F);
		b.setState(1);
		b.setPos(x, user.getEyeY() - 0.5, z);
		worldIn.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SANDSTORM.get(), SoundSource.HOSTILE, 0.8F, 1.0F);
		worldIn.addFreshEntity(b);
	}

// 攻击 - 末影守卫

	public int spawnVortex(LivingEntity user, Vec3 pos) {

		int minY = Mth.floor(user.getY()) - 10;
		float yrot = (float) Math.toRadians(90.0F + user.getYRot());
		user.level().playSound(null, pos.x, pos.y, pos.z, ModSounds.VOID_RUNE_RISING.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
		if (spawnVortex(pos.x, pos.y, pos.z, minY, yrot, user.level(), user))
			return CMCommonConfig.GauntletOfMaelstrom.cooldown;

		return 20;
	}

	public int spawnVoidFangs(LivingEntity user, Vec3 dir) {

		int standingOnY = Mth.floor(user.getY()) - 3;
		Level level = user.level();
		double headY = user.getY() + 1.0;
		Vec3[] all = new Vec3[]{dir, dir.yRot(0.3F), dir.yRot(-0.3F), dir.yRot(0.6F), dir.yRot(-0.6F), dir.yRot(0.9F), dir.yRot(-0.9F)};
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.VOID_RUNE_RISING.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
		level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.5F, 1.0F / (user.getRandom().nextFloat() * 0.4F + 0.8F));
		ScreenShake_Entity.ScreenShake(level, user.position(), 30.0F, 0.1F, 0, 30);
		for (Vec3 vector3d : all) {
			float f = (float) Mth.atan2(vector3d.z, vector3d.x);
			for (int i = 0; i < 5; ++i) {
				double d2 = 1.75 * (double) (i + 1);
				spawnFangs(user.getX() + (double) Mth.cos(f) * d2, headY, user.getZ() + (double) Mth.sin(f) * d2, standingOnY, f, i, level, user);
			}
		}
		return CMCommonConfig.VoidForge.cooldown;
	}

	public void addRune(LivingEntity user, LivingEntity target) {
		double minY = Math.min(target.getY(), user.getY());
		double maxY = Math.max(target.getY(), user.getY()) + 1;
		Vec3 v = target.getEyePosition().subtract(user.getEyePosition()).normalize();
		float angle = (float) (Mth.atan2(v.z, v.x));
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.VOID_RUNE_RISING.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
		for (int j = 1; j <= 15; ++j) {
			double dist = 1.25 * j;
			spawnFangs(user.getX() + Mth.cos(angle) * dist, maxY, user.getZ() + Mth.sin(angle) * dist, minY, angle, j, user.level(), user);

		}

	}

// 攻击 - 利维坦

	public void spawnBlastPortal(LivingEntity user, double x, double y, double z, float rotation, int delay) {

		user.level().playSound(null, x, y, z, ModSounds.PORTAL_ABYSS_BLAST.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(new Abyss_Blast_Portal_Entity(user.level(), x, y, z, rotation, delay,
				(float) CMCommonConfig.Leviathan.AbyssBlastDamage, (float) CMCommonConfig.Leviathan.AbyssBlastHpDamage, user));

	}

// 攻击 - 咒翼灵骸

	public int spawnHalberd(Vec3 pos, LivingEntity player, int delay) {

		player.level().playSound(null, pos.x, pos.y, pos.z, ModSounds.PHANTOM_SPEAR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		if (player.level() instanceof ServerLevel sl) {
			strikeWindmillHalberd(sl, pos, player, 7, 5, 2.0F, 2.0F, 0.2, delay);
		}
		return CMCommonConfig.SoulRender.cooldown;

	}

	public void spawnHalberdCircle(LivingEntity user, Vec3 pos, int count, float radius, int delay) {

		if (user.level() instanceof ServerLevel sl) {
			strikeWindmillHalberd(sl, pos, user, count, 1, radius, 0.0F, 0.0F, delay);
		}

	}

	public void spawnWindmillHalberd(LivingEntity user, Vec3 pos, int branches, int perBranch, float initRadius, float radiusIncr, float curve, int delay) {

		if (user.level() instanceof ServerLevel sl) {
			strikeWindmillHalberd(sl, pos, user, branches, perBranch, initRadius, radiusIncr, curve, delay);
		}

	}

// 武器兼容

	@Nullable
	public AbstractArrow createGhostArrow(Level level, LivingEntity player, LivingEntity target) {

		Phantom_Arrow_Entity hommingArrowEntity;
		hommingArrowEntity = new Phantom_Arrow_Entity(level, player, target);
		hommingArrowEntity.setBaseDamage(CMCommonConfig.Maledictus.PhantomArrowDamage);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
		return hommingArrowEntity;


	}

	@Nullable
	public Entity createGhostStorm(LivingEntity user, Vec3 pos, Vec3 rot, LivingEntity target) {

		Cursed_Sandstorm_Entity e = new Cursed_Sandstorm_Entity(user, rot.x, rot.y, rot.z, user.level(),
				(float) CMCommonConfig.Wadjet.Sandstorm_damage, target);
		e.setPos(pos.x, user.getEyeY() - 0.5, pos.z);
		e.setUp(15);
		user.level().playSound(null, pos.x, pos.y, pos.z, ModSounds.SANDSTORM.get(), SoundSource.HOSTILE, 0.8F, 1.0F);
		return e;

	}

	public void shootLaserGatling(LivingEntity user, Vec3 vec3) {

		Level level = user.level();
		Laser_Beam_Entity laser = new Laser_Beam_Entity(user, vec3, level, (float) CMCommonConfig.LaserGatling.damage);
		float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * Mth.RAD_TO_DEG) + 90;
		float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * Mth.RAD_TO_DEG));
		laser.setYRot(yRot);
		laser.setXRot(xRot);
		laser.setPosRaw(user.getX(), user.getY() + (double) (user.getEyeHeight() * 0.8F), user.getZ());
		RandomSource rand = level.getRandom();
		user.gameEvent(GameEvent.ITEM_INTERACT_START);
		user.playSound(ModSounds.HARBINGER_LASER.get(), 0.2F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
		level.addFreshEntity(laser);

	}

	public @Nullable CataInterface.ProjectileData shootVoid(LivingEntity player) {

		Level level = player.level();
		level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ROCKET_LAUNCH.get(), SoundSource.PLAYERS, 1.0F, 0.7F);
		Void_Howitzer_Entity rocket = new Void_Howitzer_Entity(ModEntities.VOID_HOWITZER.get(), level, player);
		return new CataInterface.ProjectileData(rocket, 1, 0.03f, CMCommonConfig.VASW.howitzerCooldown);

	}

	public int shootMissile(LivingEntity player, Vec3 dir) {

		Level level = player.level();
		Vec3 offset = dir.multiply(1, 0, 1).normalize();
		double x = player.getX() + offset.x;
		double z = player.getZ() + offset.y;
		Wither_Missile_Entity rocket = new Wither_Missile_Entity(ModEntities.WITHER_MISSILE.get(),
				player, x, player.getEyeY(), z, dir, (float) CMCommonConfig.WASW.missileDamage, level);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ROCKET_LAUNCH.get(), SoundSource.PLAYERS, 1.0F, 0.7F);
		level.addFreshEntity(rocket);
		return CMCommonConfig.WASW.missileCooldown;


	}

	public int infernalForge(LivingEntity user, LivingEntity target) {
		int radius = 4;
		Level level = user.level();
		ScreenShake_Entity.ScreenShake(level, target.position(), 30.0F, 0.1F, 0, 30);
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.EXPLOSION.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
		level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS,
				1.5F, 1.0F / (user.getRandom().nextFloat() * 0.4F + 0.8F));
		List<Entity> list = level.getEntities(user, target.getBoundingBox().inflate(radius, radius, radius));
		for (Entity entity : list) {
			if (entity instanceof LivingEntity le && !le.isAlliedTo(user) && !user.isAlliedTo(le)) {
				entity.hurt(level.damageSources().mobAttack(user), (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE));
				entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.0, 2.0, 0.0));
			}
		}
		infernalForgeParticles((ServerLevel) level, user, radius);
		return CMCommonConfig.InfernalForge.cooldown;

	}

	public @Nullable Projectile brontes(LivingEntity user, ItemStack stack, LivingEntity target) {
		Brontes_Entity brontes = new Brontes_Entity(user.level(), user);
		brontes.setBaseDamage(AttributeUtils.OriginDamage(target, stack));
		brontes.setAreaDamage((float) CMCommonConfig.Brontes.stormareadamage);
		brontes.setStormDamage((float) com.github.L_Ender.cataclysm.config.CMCommonConfig.Brontes.stormdamage);
		return brontes;
	}

	public int ceraunus(Level level, LivingEntity user, LivingEntity target) {

		var diff = target.getEyePosition().subtract(user.getEyePosition()).normalize();
		double vec = 2.0F;
		double spawnX = user.getX() + diff.x * vec;
		double spawnY = user.getY();
		double spawnZ = user.getZ() + diff.z * vec;
		int numberOfWaves = 4;
		float angleStep = 25.0F;
		double firstAngleOffset = (double) (numberOfWaves - 1) / (double) 2.0F * (double) angleStep;
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.HEAVY_SMASH.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
		for (int k = 0; k < numberOfWaves; ++k) {
			double angle = (double) user.getYRot() - firstAngleOffset + (double) ((float) k * angleStep);
			double rad = Math.toRadians(angle);
			double dx = -Math.sin(rad);
			double dz = Math.cos(rad);
			Wave_Entity WaveEntity = new Wave_Entity(level, user, 60, (float) CMCommonConfig.Ceraunus.waveDamage);
			WaveEntity.setPos(spawnX, spawnY, spawnZ);
			WaveEntity.setState(1);
			WaveEntity.setYRot(-((float) (Mth.atan2(dx, dz) * (180D / Math.PI))));
			level.addFreshEntity(WaveEntity);
		}
		return CMCommonConfig.Ceraunus.cooldown;
	}

	public int astrape(Level level, LivingEntity user, LivingEntity target) {

		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.EMP_ACTIVATED.get(), SoundSource.PLAYERS, 1.0F, 0.8F);
		Vec3 vec3 = target.getEyePosition().subtract(user.getEyePosition()).normalize();
		double x = user.getX() + vec3.x;
		double y = user.getEyeY();
		double Z = user.getZ() + vec3.z;
		float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * (180D / Math.PI)) + 90.0F;
		float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * (180D / Math.PI)));
		Lightning_Spear_Entity lightning = new Lightning_Spear_Entity(user, vec3.normalize(), level, (float) CMCommonConfig.Astrape.damage, 2.5);
		lightning.accelerationPower = 0.15;
		lightning.setYRot(yRot);
		lightning.setXRot(xRot);
		lightning.setPosRaw(x, y, Z);
		lightning.setAreaDamage((float) CMCommonConfig.Astrape.areaDamage);
		lightning.setAreaRadius(1.0F);
		level.addFreshEntity(lightning);
		return CMCommonConfig.Astrape.cooldown;
	}

	public int astrape(Level level, LivingEntity user, LivingEntity target, float lateral) {

		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.EMP_ACTIVATED.get(), SoundSource.PLAYERS, 1.0F, 0.8F);
		double dx = target.getX() - user.getX();
		double dz = target.getZ() - user.getZ();
		double len = Math.sqrt(dx * dx + dz * dz);
		double rx = len < 1e-6 ? 0 : -dz / len;
		double rz = len < 1e-6 ? 0 : dx / len;
		Vec3 vec3 = target.getEyePosition().subtract(user.getEyePosition()).normalize();
		double x = user.getX() + vec3.x + rx * lateral;
		double y = user.getEyeY();
		double Z = user.getZ() + vec3.z + rz * lateral;
		float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * (180D / Math.PI)) + 90.0F;
		float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * (180D / Math.PI)));
		Lightning_Spear_Entity lightning = new Lightning_Spear_Entity(user, vec3.normalize(), level, (float) CMCommonConfig.Astrape.damage, 2.5);
		lightning.accelerationPower = 0.15;
		lightning.setYRot(yRot);
		lightning.setXRot(xRot);
		lightning.setPosRaw(x, y, Z);
		lightning.setAreaDamage((float) CMCommonConfig.Astrape.areaDamage);
		lightning.setAreaRadius(1.0F);
		level.addFreshEntity(lightning);
		return CMCommonConfig.Astrape.cooldown;
	}

	@Nullable
	public Projectile coralSpear(LivingEntity user, Level level, ItemStack stack) {

		ThrownCoral_Spear_Entity throwntrident = new ThrownCoral_Spear_Entity(level, user, stack);
		throwntrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		throwntrident.setPos(user.getEyePosition().add(user.getForward()));
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.DEEPLING_SWING.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		return throwntrident;
	}

// 攻击 - 远古遗魂

	public void spawnDesertStele(LivingEntity user, double x, double y, double z, int delay) {

		double d0 = 0.0;
		BlockPos blockpos = BlockPos.containing(x, y, z);
		if (!user.level().isEmptyBlock(blockpos)) {
			BlockState blockstate1 = user.level().getBlockState(blockpos);
			VoxelShape voxelshape = blockstate1.getCollisionShape(user.level(), blockpos);
			if (!voxelshape.isEmpty()) {
				d0 = voxelshape.max(Direction.Axis.Y);
			}
		}
		float rotation = (float) Mth.atan2(user.getLookAngle().z, user.getLookAngle().x);
		user.level().addFreshEntity(new Ancient_Desert_Stele_Entity(user.level(), x, y + d0 - 3.0, z, rotation, delay,
				(float) CMCommonConfig.AncientRemnant.AncientDesertSteledamage, user));

	}

	public void spawnEarthQuake(LivingEntity user) {

		Vec3 floor = findFloor(user.level(), user.position(), 32);
		if (floor == null) return;
		float vec = 0.0f, math = 0.8f;
		double theta = user.getYRot() * (Math.PI / 180) + Math.PI / 2;
		double vecX = Math.cos(theta);
		double vecZ = Math.sin(theta);
		float f = (float) Math.cos(user.getYRot() * (Math.PI / 180));
		float f1 = (float) Math.sin(user.getYRot() * (Math.PI / 180));
		EarthQuake_Entity peq = new EarthQuake_Entity(user.level(), user);
		peq.setDamage((float) CMCommonConfig.AncientRemnant.EarthQuakeDamage);
		peq.setPos(floor.x + vec * vecX + f * math, floor.y + 0.4, floor.z + vec * vecZ + f1 * math);
		peq.setDeltaMovement(vecX * 0.45, 0, vecZ * 0.45);
		user.level().addFreshEntity(peq);

	}

	public void remnantRoarParticle(LivingEntity golem, float vec, float math, float y) {
		if (!(golem.level() instanceof ServerLevel sl)) return;
		float f = Mth.cos(golem.yBodyRot * (float) Math.PI / 180F);
		float f1 = Mth.sin(golem.yBodyRot * (float) Math.PI / 180F);
		double theta = golem.yBodyRot * (Math.PI / 180);
		double vecX = Math.cos(theta += 1.5707963267948966);
		double vecZ = Math.sin(theta);
		sl.sendParticles(new RoarParticleOptions(10, 255, 255, 255, 0.4F, 1.0F, 0.8F, 3.5F),
				golem.getX() + vec * vecX + f * math, golem.getY() + y, golem.getZ() + vec * vecZ + f1 * math, 1, 0.0, 0.0, 0.0, 0.0);
	}

	@Nullable
	private static Vec3 findFloor(Level level, Vec3 p, int diff) {
		double x = p.x, maxY = p.y, z = p.z, minY = maxY - diff;
		BlockPos pos = BlockPos.containing(x, maxY, z);
		double dy = 0.0F;

		do {
			BlockPos low = pos.below();
			BlockState lowState = level.getBlockState(low);
			if (lowState.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState state = level.getBlockState(pos);
					VoxelShape shape = state.getCollisionShape(level, pos);
					if (!shape.isEmpty()) {
						dy = shape.max(Direction.Axis.Y);
					}
				}
				return new Vec3(x, pos.getY() + dy, z);
			}
			pos = pos.below();
		} while (pos.getY() >= minY);
		return null;
	}

// 攻击 - 魔训兽

	public void spawnFlameJet(LivingEntity user, double x, double y, double z, int delay) {

		float rotation = (float) Mth.atan2(user.getLookAngle().z, user.getLookAngle().x);
		user.level().addFreshEntity(new Flame_Jet_Entity(user.level(), x, y, z, rotation, delay,
				(float) CMCommonConfig.NetheriteMonstrosity.FlameJetDamage, user));

	}

// 攻击 - 利维坦

	@Nullable
	public Entity addAbyssBlast(LivingEntity user, int dur) {

		Abyss_Blast_Entity beam = new Abyss_Blast_Entity(ModEntities.ABYSS_BLAST.get(), user.level(), user,
				user.getX(), user.getEyeY(), user.getZ(),
				(user.yHeadRot + 90.0F) * Mth.DEG_TO_RAD,
				-user.getXRot() * Mth.DEG_TO_RAD, dur, 0, (float) CMCommonConfig.Leviathan.AbyssBlastDamage,
				(float) CMCommonConfig.Leviathan.AbyssBlastHpDamage);
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.PORTAL_ABYSS_BLAST.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(beam);
		return beam;

	}

	public void spawnAbyssOrb(LivingEntity user, Vec3 dir, LivingEntity target) {

		Abyss_Orb_Entity orb = new Abyss_Orb_Entity(user, dir.x, dir.y, dir.z, user.level(),
				(float) CMCommonConfig.Leviathan.AbyssOrbdamage, target);
		orb.setUp(40);
		user.level().addFreshEntity(orb);

	}

	public void spawnAbyssMine(LivingEntity user, double x, double y, double z, int delay) {

		user.level().addFreshEntity(new Abyss_Mine_Entity(user.level(), x, y, z, 0, delay, user));

	}

	public void spawnDimensionalRift(LivingEntity user, double x, double y, double z) {

		user.level().addFreshEntity(new Dimensional_Rift_Entity(user.level(), x, y, z, user));

	}

// 攻击 - 斯库拉

	public void lightningStorm(LivingEntity user, double x, double y, double z, float size) {

		float rotation = (float) Mth.atan2(user.getLookAngle().z, user.getLookAngle().x);
		user.level().addFreshEntity(new Lightning_Storm_Entity(user.level(), x, y, z, rotation, 0,
				(float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage, user, size));

	}

	public void summonStormSerpent(LivingEntity user, LivingEntity target) {

		Vec3 diff = target.getEyePosition().subtract(user.getEyePosition()).normalize();
		float rotation = (float) Mth.atan2(diff.z, diff.x);
		user.level().addFreshEntity(new Storm_Serpent_Entity(user.level(), user.getX(), user.getY(), user.getZ(),
				rotation, 0, user, (float) CMCommonConfig.Scylla.SnakeDamage, target, true));

	}

// 攻击 - 先驱者

	public void shootHowitzer(LivingEntity user, Vec3 dir) {

		Wither_Howitzer_Entity howitzer = new Wither_Howitzer_Entity(ModEntities.WITHER_HOWITZER.get(), user.level(), user);
		howitzer.setPos(user.getX(), user.getEyeY(), user.getZ());
		howitzer.setRadius(3.0f);
		howitzer.shoot(dir.x, dir.y, dir.z, 0.6F, 60.0F);
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.ROCKET_LAUNCH.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(howitzer);

	}

// 攻击 - 新代理

	public void fallingBlocks(LivingEntity user, double inflate) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		AABB aabb = user.getBoundingBox().inflate(inflate, 0.2, inflate);
		boolean flag = false;
		for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
			BlockState state = level.getBlockState(pos);
			if (state.isAir() || !state.canEntityDestroy(level, pos, user)
					|| state.is(ModTag.NETHERITE_MONSTROSITY_IMMUNE) || state.is(ModTag.HARBINGER_IMMUNE)
					|| !ForgeEventFactory.onEntityDestroyBlock(user, pos, state)) {
				continue;
			}
			if (user.getRandom().nextInt(3) == 0 && !state.hasBlockEntity()) {
				Cm_Falling_Block_Entity falling = new Cm_Falling_Block_Entity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state, 20);
				flag = level.destroyBlock(pos, false, user) || flag;
				falling.setDeltaMovement(falling.getDeltaMovement().add(user.position().subtract(falling.position())
						.multiply((user.getRandom().nextFloat() - 1.2F) / 3.0F, 0.0, (user.getRandom().nextFloat() - 1.2F) / 3.0F)));
				level.addFreshEntity(falling);
			} else {
				flag = level.destroyBlock(pos, false, user) || flag;
			}
		}
		if (flag) {
			level.levelEvent(null, 1022, user.blockPosition(), 0);
		}
	}

	public void shootEnderBullet(LivingEntity user, LivingEntity target) {
		if (target == null) return;
		Level level = user.level();
		BlockPos tgt = target.blockPosition();
		double tx = tgt.getX();
		double tz = tgt.getZ();
		double ty = target.getY() + 0.1;
		BlockPos pos = user.blockPosition();
		double sx = pos.getX();
		double sz = pos.getZ();
		Direction dir = Direction.getNearest(tx - sx, 0, tz - sz);
		double cx = dir.getStepX();
		double cz = dir.getStepZ();
		for (int i = -4; i <= 4; i++) {
			double angle = (double) (i - 2) * Math.toRadians(6.0);
			double x = cx * Math.cos(angle) + cz * Math.sin(angle);
			double z = -cx * Math.sin(angle) + cz * Math.cos(angle);
			Ender_Guardian_Bullet_Entity bullet = new Ender_Guardian_Bullet_Entity(level, user, x, user.getY() + 2.0, z);
			bullet.setOwner(user);
			bullet.setPos(user.getX(), user.getY() - 2.0 + user.getRandom().nextFloat() * 4.0, user.getZ());
			bullet.setUp(30, cx, 0, cz, tx - 7.0 * cx + (double) i * cz, ty, tz - 7.0 * cz + (double) i * cx);
			level.addFreshEntity(bullet);
		}
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.ENDER_GUARDIAN_FIST.get(), SoundSource.HOSTILE, 0.5F, 1.0F);
	}

	public void gravityPull(LivingEntity user) {
		if (user.level().isClientSide()) return;
		float angle = (float) Math.PI / 180 * user.yBodyRot;
		double extraX = Mth.sin((float) (Math.PI + angle));
		double extraZ = Mth.cos(angle);
		double theta = user.yBodyRot * (Math.PI / 180);
		double vecX = Math.cos(theta += Math.PI / 2);
		double vecZ = Math.sin(theta);
		Vec3 pull = user.position().add(2.0 * vecX + extraX * 0.25, 0, 2.0 * vecZ + extraZ * 0.25);
		for (LivingEntity e : user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(12, 12, 12))) {
			if (e == user || e.isAlliedTo(user)) continue;
			Vec3 diff = pull.subtract(e.position()).normalize().scale(0.085);
			e.setDeltaMovement(e.getDeltaMovement().add(diff));
		}
	}

	public void massDestruction(LivingEntity user) {
		// 对齐原版 MassDestruction(3.0F, 1.5F, 200)：范围 3，伤害 atk*1.5，击飞公式保持原版一致
		if (user.level().isClientSide()) return;
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		for (LivingEntity e : user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(3.0))) {
			if (e == user || e.isAlliedTo(user)) continue;
			e.hurt(user.level().damageSources().mobAttack(user), (float) (atk * 1.5));
			launch(user, e);
		}
	}

	public void burst(LivingEntity user) {
		guardianAreaAttack(user, 7.5F, 6.0F, 1.0F, (float) CMCommonConfig.EnderGuardian.KnockbackHpdamage, 0, 0, true);
	}

	public void rocketPunch(LivingEntity user, LivingEntity target) {
		guardianAreaAttack(user, 7.0F, 7.0F, 1.25F, (float) CMCommonConfig.EnderGuardian.RocketPunchHpdamage, 0, 0, true);
	}

	public void hugMe(LivingEntity user, LivingEntity target) {
		guardianAreaAttack(user, 6.0F, 6.0F, 1.0F, (float) CMCommonConfig.EnderGuardian.TeleportAttackHpdamage, 60, 0.6F, false);
	}

	@Override
	public void guardianStompFangs(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.ENDER_GUARDIAN_FIST.get(), SoundSource.HOSTILE, 0.3F, 1.0F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 10, 0.1F, 0, 5);
		if (target == null) return;
		double d0 = Math.min(target.getY(), user.getY());
		double d1 = Math.max(target.getY(), user.getY()) + 1.0D;
		float angle2 = 0.01745329251F * user.yBodyRot;
		for (int k = 0; k < 6; ++k) {
			float f2 = angle2 + (float) k * (float) Math.PI * 2.0F / 6.0F + ((float) Math.PI * 2F / 5F);
			spawnGuardianFangs(user, user.getX() + (double) Mth.cos(f2) * 2.5D, user.getZ() + (double) Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
		}
		for (int k = 0; k < 11; ++k) {
			float f3 = angle2 + (float) k * (float) Math.PI * 2.0F / 11.0F + ((float) Math.PI * 2F / 10F);
			spawnGuardianFangs(user, user.getX() + (double) Mth.cos(f3) * 3.5D, user.getZ() + (double) Mth.sin(f3) * 3.5D, d0, d1, f3, 10);
		}
		for (int k = 0; k < 14; ++k) {
			float f4 = angle2 + (float) k * (float) Math.PI * 2.0F / 14.0F + ((float) Math.PI * 2F / 20F);
			spawnGuardianFangs(user, user.getX() + (double) Mth.cos(f4) * 4.5D, user.getZ() + (double) Mth.sin(f4) * 4.5D, d0, d1, f4, 15);
		}
		float f = (float) Mth.atan2(target.getZ() - user.getZ(), target.getX() - user.getX());
		switch (user.getRandom().nextInt(3)) {
			case 0 -> {
				for (float angle : new float[]{f - 0.4f, f, f + 0.4f}) {
					for (int l = 0; l < 13; ++l) {
						double d2 = 1.25D * (double) (l + 1);
						int j = (int) (0.75f * l);
						spawnGuardianFangs(user, user.getX() + (double) Mth.cos(angle) * d2, user.getZ() + (double) Mth.sin(angle) * d2, d0, d1, angle, j);
					}
				}
			}
			case 1 -> {
				for (float angle : new float[]{f - 0.1f, f + 0.1f}) {
					for (int l = 0; l < 13; ++l) {
						double d2 = 1.25D * (double) (l + 1);
						int j = (int) (0.25f * l);
						spawnGuardianFangs(user, user.getX() + (double) Mth.cos(angle) * d2, user.getZ() + (double) Mth.sin(angle) * d2, d0, d1, angle, j);
					}
				}
			}
			default -> {
				for (int l = 0; l < 13; ++l) {
					double d2 = 1.25D * (double) (l + 1);
					int j = (int) (0.5f * l);
					spawnGuardianFangs(user, user.getX() + (double) Mth.cos(f) * d2, user.getZ() + (double) Mth.sin(f) * d2, d0, d1, f, j);
				}
			}
		}
	}

	private void spawnGuardianFangs(LivingEntity user, double x, double z, double minY, double maxY, float rotation, int delay) {
		Level level = user.level();
		BlockPos blockpos = BlockPos.containing(x, maxY, z);
		boolean flag = false;
		double d0 = 0.0D;
		do {
			BlockPos below = blockpos.below();
			BlockState state = level.getBlockState(below);
			if (state.isFaceSturdy(level, below, Direction.UP)) {
				if (!level.isEmptyBlock(blockpos)) {
					BlockState state1 = level.getBlockState(blockpos);
					VoxelShape shape = state1.getCollisionShape(level, blockpos);
					if (!shape.isEmpty()) d0 = shape.max(Direction.Axis.Y);
				}
				flag = true;
				break;
			}
			blockpos = blockpos.below();
		} while (blockpos.getY() >= Mth.floor(minY) - 1);
		if (flag) {
			level.addFreshEntity(new Void_Rune_Entity(level, x, (double) blockpos.getY() + d0, z, rotation, delay, (float) CMCommonConfig.EnderGuardian.VoidRuneDamage, user));
		}
	}

	@Override
	public void guardianUppercut(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.EXPLOSION.get(), SoundSource.HOSTILE, 1.5F, 1.0F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 15, 0.3F, 0, 10);
		if (target == null) return;
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float arc = 60;
		for (LivingEntity e : user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(6.25D, 7.0D, 6.25D))) {
			if (e == user || e.isAlliedTo(user)) continue;
			float hitAngle = (float) ((Mth.atan2(e.getZ() - user.getZ(), e.getX() - user.getX()) * (180 / Math.PI) - 90) % 360);
			float attackAngle = user.yBodyRot % 360;
			if (hitAngle < 0) hitAngle += 360;
			if (attackAngle < 0) attackAngle += 360;
			float rel = hitAngle - attackAngle;
			float dist = (float) Math.sqrt((e.getZ() - user.getZ()) * (e.getZ() - user.getZ()) + (e.getX() - user.getX()) * (e.getX() - user.getX()));
			if (dist <= 6.25F && ((rel <= arc / 2 && rel >= -arc / 2) || (rel >= 360 - arc / 2 || rel <= -360 + arc / 2))) {
				DamageSource damagesource = user.level().damageSources().mobAttack(user);
				boolean flag = e.hurt(damagesource, (float) (atk * 1.4F + Math.min(atk * 1.4F, e.getMaxHealth() * (float) CMCommonConfig.EnderGuardian.UppercutHpdamage)));
				if (e.isDamageSourceBlocked(damagesource) && e instanceof Player player) EntityUtil.disableShield(player, 150);
				e.setDeltaMovement(e.getDeltaMovement().add(0.0D, 0.5D, 0.0D));
				if (flag) e.addEffect(new MobEffectInstance(ModEffect.EFFECTSTUN.get(), 60));
			}
		}
	}

	@Override
	public void guardianHugFX(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.EXPLOSION.get(), SoundSource.HOSTILE, 1.5F, 1.0F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 15, 0.2F, 0, 10);
	}

	@Override
	public void guardianGravityPunch(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.ENDER_GUARDIAN_FIST.get(), SoundSource.HOSTILE, 0.5F, 1.0F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 20, 0.2F, 0, 10);
		if (target == null) return;
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float arc = 70;
		for (LivingEntity e : user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(5.15D, 5.0D, 5.15D))) {
			if (e == user || e.isAlliedTo(user)) continue;
			float hitAngle = (float) ((Mth.atan2(e.getZ() - user.getZ(), e.getX() - user.getX()) * (180 / Math.PI) - 90) % 360);
			float attackAngle = user.yBodyRot % 360;
			if (hitAngle < 0) hitAngle += 360;
			if (attackAngle < 0) attackAngle += 360;
			float rel = hitAngle - attackAngle;
			float dist = (float) Math.sqrt((e.getZ() - user.getZ()) * (e.getZ() - user.getZ()) + (e.getX() - user.getX()) * (e.getX() - user.getX()));
			if (dist <= 5.15F && ((rel <= arc / 2 && rel >= -arc / 2) || (rel >= 360 - arc / 2 || rel <= -360 + arc / 2))) {
				DamageSource damagesource = user.level().damageSources().mobAttack(user);
				e.hurt(damagesource, (float) (atk + Math.min(atk, e.getMaxHealth() * (float) CMCommonConfig.EnderGuardian.GravityPunchHpdamage)));
				if (e.isDamageSourceBlocked(damagesource) && e instanceof Player player) EntityUtil.disableShield(player, 100);
			}
		}
	}

	@Override
	public void guardianSlamLanding(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.ENDER_GUARDIAN_FIST.get(), SoundSource.HOSTILE, 0.5F, 1.0F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 20, 0.3F, 0, 20);
		switch (user.getRandom().nextInt(3)) {
			case 0 -> strikeGuardianRune(user, 8, 0.5);
			case 1 -> strikeGuardianRune(user, 10, 0.75);
			default -> strikeGuardianRune(user, 12, 1);
		}
	}

	private void strikeGuardianRune(LivingEntity user, int rune, double time) {
		for (int i = 0; i < rune; i++) {
			float throwAngle = i * Mth.PI / (rune / 2);
			for (int k = 0; k < 8; ++k) {
				double d2 = 1.15D * (double) (k + 1);
				int d3 = (int) (time * (k + 1));
				spawnGuardianFangs(user, user.getX() + (double) Mth.cos(throwAngle) * 1.25D * d2,
						user.getZ() + (double) Mth.sin(throwAngle) * 1.25D * d2,
						user.getY(), user.getY() + 2, throwAngle, d3);
			}
		}
	}

	@Override
	public void guardianExplosionSound(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.EXPLOSION.get(), SoundSource.HOSTILE, 1.5F, 1.0F + user.getRandom().nextFloat() * 0.1F);
	}

	public void throwLavaBomb(LivingEntity user, LivingEntity target) {
		if (target == null) return;
		Level level = user.level();
		Lava_Bomb_Entity bomb = new Lava_Bomb_Entity(ModEntities.LAVA_BOMB.get(), level, user);
		double d0 = target.getX() - user.getX();
		double d1 = target.getBoundingBox().minY + target.getBbHeight() / 3.0 - bomb.getY();
		double d2 = target.getZ() - user.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		bomb.setMaxLavaTime(CMCommonConfig.NetheriteMonstrosity.LavabombDuration + user.getRandom().nextInt(CMCommonConfig.NetheriteMonstrosity.LavabombRandomDuration));
		bomb.shoot(d0, d1 + d3 * 0.2, d2, 1.0F, 24 - level.getDifficulty().getId() * 4);
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MONSTROSITYSHOOT.get(), SoundSource.HOSTILE, 2.0F, 1.0F);
		level.addFreshEntity(bomb);
	}

	public void throwFlareBomb(LivingEntity user, LivingEntity target) {
		if (target == null) return;
		Level level = user.level();
		float f = Mth.cos(user.yBodyRot * ((float) Math.PI / 180));
		float f1 = Mth.sin(user.yBodyRot * ((float) Math.PI / 180));
		for (int i = 0; i < 5; i++) {
			Flare_Bomb_Entity bomb = new Flare_Bomb_Entity(ModEntities.FLARE_BOMB.get(), level, user);
			bomb.setPos(user.getX() + (double) f * 3.4, user.getEyeY(), user.getZ() + (double) f1 * 3.4);
			double d0 = target.getX() - bomb.getX();
			double d1 = target.getBoundingBox().minY + target.getBbHeight() / 3.0 - bomb.getY();
			double d2 = target.getZ() - bomb.getZ();
			double d3 = Math.sqrt(d0 * d0 + d2 * d2);
			bomb.shoot(d0, d1 + d3 * 0.2, d2, 1.0F, 1 + i * 8);
			level.addFreshEntity(bomb);
		}
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MONSTROSITYSHOOT.get(), SoundSource.HOSTILE, 2.0F, 1.0F);
	}

	public void shoulderCheck(LivingEntity user, LivingEntity target) {
		if (target == null) return;
		Vec3 dir = target.position().subtract(user.position()).normalize();
		user.setDeltaMovement(user.getDeltaMovement().add(dir.scale(1.5)));
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		for (LivingEntity e : user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(2.5, 0.5, 2.5).move(dir.scale(1.25)))) {
			if (e == user || e.isAlliedTo(user)) continue;
			if (e.hurt(user.level().damageSources().mobAttack(user), (float) (atk * 0.4))) {
				e.addEffect(new MobEffectInstance(ModEffect.EFFECTBONE_FRACTURE.get(), 100));
				e.setDeltaMovement(e.getDeltaMovement().add(dir.scale(2.0)));
			}
		}
	}

	public void shootSpark(LivingEntity user, float areaRadius) {
		Level level = user.level();
		Spark_Entity spark = new Spark_Entity(level, user);
		spark.setDamage((float) CMCommonConfig.Scylla.LightningStormDamage);
		spark.setAreaDamage((float) CMCommonConfig.Scylla.LightningAreaDamage);
		spark.setAreaRadius(areaRadius);
		spark.setHpDamage((float) CMCommonConfig.Scylla.StormHpDamage);
		double d0 = (user.getRandom().nextFloat() - 0.5) * 0.5;
		double d1 = user.getRandom().nextFloat() * 0.4 + 0.01;
		double d2 = (user.getRandom().nextFloat() - 0.5) * 0.5;
		spark.shoot(d0, d1, d2, 1.0F, 1.0F);
		spark.setPos(user.getX(), user.getY() + 0.03, user.getZ());
		level.addFreshEntity(spark);
	}

	public void spawnLightningArea(LivingEntity user, double x, double y, double z) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		Lightning_Area_Effect_Entity cloud = new Lightning_Area_Effect_Entity(level, x, y, z);
		cloud.setRadius(3.5F);
		cloud.setOwner(user);
		cloud.setRadiusOnUse(-1.5F);
		cloud.setDamage((float) CMCommonConfig.Scylla.LightningAreaDamage);
		cloud.setWaitTime(20);
		cloud.setDuration(cloud.getDuration() / 2);
		cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
		level.addFreshEntity(cloud);
	}

	public void scyllaSummonWave(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		double vecX = Math.cos(user.yBodyRot * (Math.PI / 180) + Math.PI / 2);
		double vecZ = Math.sin(user.yBodyRot * (Math.PI / 180));
		for (int i = 0; i < 5; i++) {
			double angle = user.yBodyRot - 50.0 + i * 25.0;
			double rad = Math.toRadians(angle);
			double dx = -Math.sin(rad);
			double dz = Math.cos(rad);
			Wave_Entity wave = new Wave_Entity(user.level(), user, 80, 9.0F);
			wave.setPos(user.getX() + vecX * 2.4, user.getY(), user.getZ() + vecZ * 2.4);
			wave.setState(1);
			wave.setYRot(-((float) (Mth.atan2(dx, dz) * 57.29577951308232)));
			user.level().addFreshEntity(wave);
		}
	}

	public void scyllaSummonWave2(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		double vecX = Math.cos(user.yBodyRot * (Math.PI / 180) + Math.PI / 2);
		double vecZ = Math.sin(user.yBodyRot * (Math.PI / 180));
		for (int i = 0; i < 4; i++) {
			double angle = user.yBodyRot - 37.5 + i * 25.0;
			double rad = Math.toRadians(angle);
			double dx = -Math.sin(rad);
			double dz = Math.cos(rad);
			Wave_Entity wave = new Wave_Entity(user.level(), user, 80, 9.0F);
			wave.setPos(user.getX() + vecX * 2.4, user.getY(), user.getZ() + vecZ * 2.4);
			wave.setState(1);
			wave.setYRot(-((float) (Mth.atan2(dx, dz) * 57.29577951308232)));
			user.level().addFreshEntity(wave);
		}
	}

	public void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning) {
		if (target == null) return;
		Level level = user.level();
		float f = Mth.cos(user.yBodyRot * ((float) Math.PI / 180));
		float f1 = Mth.sin(user.yBodyRot * ((float) Math.PI / 180));
		double d0 = user.getX() + f * -0.5;
		double d1 = user.getY() + user.getBbHeight() * 0.8;
		double d2 = user.getZ() + f1 * -0.5;
		double d3 = target.getX() - d0;
		double d4 = target.getY(0.35) - d1;
		double d5 = target.getZ() - d2;
		Vec3 vec3 = new Vec3(d3, d4, d5).normalize();
		float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * 57.29577951308232) + 90.0F;
		float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * 57.29577951308232));
		if (lightning) {
			Lightning_Spear_Entity spear = new Lightning_Spear_Entity(user, vec3, level, (float) CMCommonConfig.Scylla.SpearDamage, 2.0);
			spear.setYRot(yRot);
			spear.setXRot(xRot);
			spear.moveTo(d0, d1, d2);
			spear.setAreaDamage((float) CMCommonConfig.Scylla.LightningAreaDamage);
			spear.setHpDamage((float) CMCommonConfig.Scylla.StormHpDamage);
			spear.setAreaRadius(2.0F);
			level.addFreshEntity(spear);
		} else {
			Water_Spear_Entity spear = new Water_Spear_Entity(user, vec3, level, (float) CMCommonConfig.Scylla.SpearDamage, 2.0);
			spear.setYRot(yRot);
			spear.setXRot(xRot);
			spear.moveTo(d0, d1, d2);
			spear.setTotalBounces(8);
			level.addFreshEntity(spear);
		}
	}

	public void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning, float lateral) {
		if (target == null) return;
		Level level = user.level();
		float f = Mth.cos(user.yBodyRot * ((float) Math.PI / 180));
		float f1 = Mth.sin(user.yBodyRot * ((float) Math.PI / 180));
		double dx = target.getX() - user.getX();
		double dz = target.getZ() - user.getZ();
		double len = Math.sqrt(dx * dx + dz * dz);
		double rx = len < 1e-6 ? 0 : -dz / len;
		double rz = len < 1e-6 ? 0 : dx / len;
		double d0 = user.getX() + f * -0.5 + rx * lateral;
		double d1 = user.getY() + user.getBbHeight() * 0.8;
		double d2 = user.getZ() + f1 * -0.5 + rz * lateral;
		double d3 = target.getX() - d0;
		double d4 = target.getY(0.35) - d1;
		double d5 = target.getZ() - d2;
		Vec3 vec3 = new Vec3(d3, d4, d5).normalize();
		float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * 57.29577951308232) + 90.0F;
		float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * 57.29577951308232));
		if (lightning) {
			Lightning_Spear_Entity spear = new Lightning_Spear_Entity(user, vec3, level, (float) CMCommonConfig.Scylla.SpearDamage, 2.0);
			spear.setYRot(yRot);
			spear.setXRot(xRot);
			spear.moveTo(d0, d1, d2);
			spear.setAreaDamage((float) CMCommonConfig.Scylla.LightningAreaDamage);
			spear.setHpDamage((float) CMCommonConfig.Scylla.StormHpDamage);
			spear.setAreaRadius(2.0F);
			level.addFreshEntity(spear);
		} else {
			Water_Spear_Entity spear = new Water_Spear_Entity(user, vec3, level, (float) CMCommonConfig.Scylla.SpearDamage, 2.0);
			spear.setYRot(yRot);
			spear.setXRot(xRot);
			spear.moveTo(d0, d1, d2);
			spear.setTotalBounces(8);
			level.addFreshEntity(spear);
		}
	}

	public void scyllaFallingBurst(LivingEntity user, int sparkAmount) {
		double cx = user.getX(), cy = user.getY(), cz = user.getZ();
		if (user instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() != null) {
			LivingEntity target = mob.getTarget();
			cx = target.getX();
			cy = target.getY();
			cz = target.getZ();
		}
		double floorY = findFloorY(user.level(), cx, cy, cz);
		scyllaFallingBurstAt(user, cx, floorY, cz, sparkAmount);
	}

	public void scyllaFallingBurstAt(LivingEntity user, double x, double y, double z, int sparkAmount) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		level.playSound(null, x, y, z, ModSounds.EXPLOSION.get(), SoundSource.HOSTILE, 2.0F, 0.75F + level.random.nextFloat() * 0.1F);
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		double floorY = y;
		AABB hitBox = new AABB(x - 0.5, floorY, z - 0.5, x + 0.5, floorY + 2, z + 0.5);
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, hitBox)) {
			if (e == user || e.isAlliedTo(user)) continue;
			e.hurt(CMDamageTypes.causeLightningMobDamage(user), (float) (atk + Math.min(atk, e.getMaxHealth() * (float) CMCommonConfig.Scylla.HpDamage)));
		}
		int rune = 7;
		int time = 4;
		for (int i = 0; i < rune; i++) {
			float throwAngle = (float) i * (float) Math.PI / ((float) rune / 2.0F);
			for (int k = 0; k < 5; k++) {
				double d = 1.75 * (k + 1);
				int delay = time * (k + 1) - 9;
				level.addFreshEntity(new Lightning_Storm_Entity(level,
						x + Mth.cos(throwAngle) * 1.25 * d,
						floorY,
						z + Mth.sin(throwAngle) * 1.25 * d,
						throwAngle, delay,
						(float) CMCommonConfig.Scylla.LightningStormDamage,
						(float) CMCommonConfig.Scylla.StormHpDamage, user, 2.0F));
			}
		}
		for (int i = 0; i < sparkAmount; i++) {
			Spark_Entity peq = new Spark_Entity(level, user);
			peq.setDamage((float) CMCommonConfig.Scylla.LightningStormDamage);
			peq.setAreaDamage((float) CMCommonConfig.Scylla.LightningAreaDamage);
			peq.setAreaRadius(1.0F);
			peq.setHpDamage((float) CMCommonConfig.Scylla.StormHpDamage);
			peq.shoot((user.getRandom().nextFloat() - 0.5) * 0.5, user.getRandom().nextFloat() * 0.4F + 0.01F, (user.getRandom().nextFloat() - 0.5) * 0.5, 1.0F, 1.0F);
			peq.setPos(x, floorY + 0.03, z);
			level.addFreshEntity(peq);
		}
	}

	public void scyllaLightningExplosion(LivingEntity user, double x, double y, double z) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float rotation = (float) Mth.atan2(user.getLookAngle().z, user.getLookAngle().x);
		level.addFreshEntity(new Lightning_Storm_Entity(level, x, y + 1.0, z, rotation, -9,
				(float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage, user, 4.0F));
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(x - 4, y - 4, z - 4, x + 4, y + 4, z + 4))) {
			if (e == user || e.isAlliedTo(user)) continue;
			e.hurt(CMDamageTypes.causeLightningMobDamage(user), (float) (atk + Math.min(atk, e.getMaxHealth() * (float) CMCommonConfig.Scylla.HpDamage)));
		}
	}

	public void scyllaBurstLightning(LivingEntity user) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		float rotation = (float) Mth.atan2(user.getLookAngle().z, user.getLookAngle().x);
		double vecX = Math.cos(user.yBodyRot * (Math.PI / 180) + Math.PI / 2);
		double vecZ = Math.sin(user.yBodyRot * (Math.PI / 180));
		double f = Math.cos(user.yBodyRot * (Math.PI / 180));
		double f1 = Math.sin(user.yBodyRot * (Math.PI / 180));
		level.addFreshEntity(new Lightning_Storm_Entity(level, user.getX() + vecX * 1.2 + f * 0.65, user.getY() + 0.03, user.getZ() + vecZ * 1.2 + f1 * 0.65, rotation, -9,
				(float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage, user, 3.0F));
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(4.0))) {
			if (e == user || e.isAlliedTo(user)) continue;
			double d0 = e.getX() - user.getX();
			double d1 = e.getZ() - user.getZ();
			double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
			e.push(d0 / d2 * 2.5, 0.3, d1 / d2 * 2.5);
		}
		int rune = 7;
		int time = 4;
		for (int i = 0; i < rune; i++) {
			float throwAngle = (float) i * (float) Math.PI / ((float) rune / 2);
			for (int k = 0; k < 5; k++) {
				double d = 1.75 * (k + 1);
				int delay = time * (k + 1) - 9;
				spawnLightningToGround(user, user.getX() + Mth.cos(throwAngle) * 1.25 * d, user.getZ() + Mth.sin(throwAngle) * 1.25 * d, user.getY(), user.getY() + 2.0, throwAngle, delay, 2.0F);
			}
		}
	}

	public void scyllaWhip(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		double vecX = Math.cos(user.yBodyRot * (Math.PI / 180) + Math.PI / 2);
		double vecZ = Math.sin(user.yBodyRot * (Math.PI / 180));
		double px = user.getX() + vecX * 6.0;
		double pz = user.getZ() + vecZ * 6.0;
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(6.75, 6.75, 6.75))) {
			if (e == user || e.isAlliedTo(user)) continue;
			float entityHitAngle = (float) ((Math.atan2(e.getZ() - user.getZ(), e.getX() - user.getX()) * 57.29577951308232 - 90.0) % 360.0);
			float entityAttackingAngle = user.yBodyRot % 360.0F;
			if (entityHitAngle < 0) entityHitAngle += 360.0F;
			if (entityAttackingAngle < 0) entityAttackingAngle += 360.0F;
			float rel = entityHitAngle - entityAttackingAngle;
			float dist = (float) e.distanceTo(user);
			if (!(dist <= 6.75 && rel <= 20.0F && rel >= -20.0F || rel >= 340.0F || rel <= -340.0F)) continue;
			e.hurt(level.damageSources().mobAttack(user), (float) (atk * 1.2 + Math.min(atk * 1.2, e.getMaxHealth() * (float) CMCommonConfig.Scylla.HpDamage)));
		}
		for (int i = 0; i < 16; i++) {
			Spark_Entity peq = new Spark_Entity(level, user);
			peq.setDamage((float) CMCommonConfig.Scylla.LightningStormDamage);
			peq.setAreaDamage((float) CMCommonConfig.Scylla.LightningAreaDamage);
			peq.setAreaRadius(2.0F);
			peq.setHpDamage((float) CMCommonConfig.Scylla.StormHpDamage);
			peq.shoot((user.getRandom().nextFloat() - 0.5) * 0.5, user.getRandom().nextFloat() * 0.4F + 0.01F, (user.getRandom().nextFloat() - 0.5) * 0.5, 1.0F, 1.0F);
			peq.setPos(px, user.getY() + 0.1, pz);
			level.addFreshEntity(peq);
		}
	}

	public void scyllaSpin(LivingEntity user) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(5.5, 3.0, 5.5))) {
			if (e == user || e.isAlliedTo(user)) continue;
			if (e.hurt(level.damageSources().mobAttack(user), (float) (atk + Math.min(atk, e.getMaxHealth() * (float) CMCommonConfig.Scylla.SpinHpDamage)))) {
				double d0 = e.getX() - user.getX();
				double d1 = e.getZ() - user.getZ();
				double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
				e.push(d0 / d2 * 0.6, 0.0, d1 / d2 * 0.6);
			}
		}
	}

	@Override
	public void scyllaSpinSound(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.STRONGSWING.get(), SoundSource.HOSTILE, 2.0F, 1.1F);
	}

	@Override
	public void scyllaStormParticles(LivingEntity user) {
		if (!(user.level() instanceof ServerLevel sl)) return;
		var rand = user.getRandom();
		sl.sendParticles(new StormParticleOptions(99 / 255F, 194 / 255F, 224 / 255F,
						6f + rand.nextFloat() * 0.25f, 1.5F + rand.nextFloat() * 0.45f, user.getId()),
				user.getX(), user.getY(), user.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		sl.sendParticles(new StormParticleOptions(143 / 255F, 241 / 255F, 215 / 255F,
						4f + rand.nextFloat() * 1.2f, 1.0F + rand.nextFloat() * 0.45f, user.getId()),
				user.getX(), user.getY(), user.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		sl.sendParticles(new StormParticleOptions(143 / 255F, 241 / 255F, 215 / 255F,
						2f + rand.nextFloat() * 0.7f, 0.35F + rand.nextFloat() * 0.45f, user.getId()),
				user.getX(), user.getY(), user.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	public void scyllaThunderCloud(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		if (target == null) return;
		for (int i = 0; i < 30; i++) {
			scyllaThunderCloudSpear(user, target, user.getX(), user.getY() + user.getBbHeight() * 2.8f, user.getZ());
		}
	}

	public void scyllaThunderCloudStorm(LivingEntity user, double x, double y, double z) {
		if (user.level().isClientSide()) return;
		if (!(user.level() instanceof ServerLevel serverLevel)) return;
		for (int j = 0; j < 6; j++) {
			float f2 = user.getRandom().nextFloat() * (float) Math.PI * 2;
			float f3 = Mth.sqrt(user.getRandom().nextFloat()) * 3.5f;
			double d0 = x + Math.cos(f2) * f3;
			double d2 = z + Math.sin(f2) * f3;
			serverLevel.sendParticles(ModParticle.RAIN_CLOUD.get(), d0, y, d2, 1, 0.05, 0.01, 0.05, 0.0);
		}
	}

	public void scyllaThunderCloudSpear(LivingEntity user, LivingEntity target, double cx, double cy, double cz) {
		if (user.level().isClientSide()) return;
		if (target == null) return;
		Level level = user.level();
		float f2 = user.getRandom().nextFloat() * ((float) Math.PI * 2);
		float f3 = Mth.sqrt(user.getRandom().nextFloat()) * 5.0f;
		double d0 = cx + (double) (Mth.cos(f2) * f3);
		double d1 = cy;
		double d2 = cz + (double) (Mth.sin(f2) * f3);
		double d3 = target.getX() - (user.getRandom().nextFloat() - 0.5) * 6.0 - d0;
		double d4 = target.getY(0.35) - user.getRandom().nextFloat() - d1;
		double d5 = target.getZ() - (user.getRandom().nextFloat() - 0.5) * 6.0 - d2;
		Vec3 vec3 = new Vec3(d3, d4, d5).normalize();
		float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * 57.29577951308232) + 90.0F;
		float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * 57.29577951308232));
		Water_Spear_Entity water = new Water_Spear_Entity(user, vec3, level, (float) CMCommonConfig.Scylla.SpearDamage, 1.0);
		water.setYRot(yRot);
		water.setXRot(xRot);
		water.moveTo(d0, d1, d2);
		water.setTotalBounces(3);
		level.addFreshEntity(water);
		Lightning_Spear_Entity lightning = new Lightning_Spear_Entity(user, vec3, level, (float) CMCommonConfig.Scylla.SnakeDamage, 1.0);
		lightning.setYRot(yRot);
		lightning.setXRot(xRot);
		lightning.moveTo(d0, d1, d2);
		lightning.setAreaDamage((float) CMCommonConfig.Scylla.LightningAreaDamage);
		lightning.setHpDamage((float) CMCommonConfig.Scylla.StormHpDamage);
		lightning.setAreaRadius(2.0F);
		level.addFreshEntity(lightning);
	}

	// 备用 - 8 Boss补全（傀儡可能用到）

	public void spawnMiniAbyssBlast(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		float yaw = (user.yHeadRot + 90.0F) * Mth.DEG_TO_RAD;
		float pitch = -user.getXRot() * Mth.DEG_TO_RAD;
		var beam = new Mini_Abyss_Blast_Entity(ModEntities.MINI_ABYSS_BLAST.get(), user.level(), user,
				user.getX(), user.getEyeY(), user.getZ(), yaw, pitch, 40, 0.0F);
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.PORTAL_ABYSS_BLAST.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(beam);
	}

	public void spawnPortalAbyssBlast(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		float yaw = (user.yHeadRot + 90.0F) * Mth.DEG_TO_RAD;
		float pitch = -user.getXRot() * Mth.DEG_TO_RAD;
		float dmg = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		var beam = new Portal_Abyss_Blast_Entity(ModEntities.PORTAL_ABYSS_BLAST.get(), user.level(), user,
				user.getX(), user.getEyeY(), user.getZ(), yaw, pitch, 40, 0.0F, dmg, 0.0F);
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.PORTAL_ABYSS_BLAST.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		user.level().addFreshEntity(beam);
	}

	public void spawnAbyssPortal(LivingEntity user, double x, double y, double z) {
		if (user.level().isClientSide()) return;
		var portal = new Abyss_Portal_Entity(ModEntities.ABYSS_PORTAL.get(), user.level());
		portal.setPos(x, y, z);
		portal.setLifespan(200);
		portal.setEntrance(true);
		user.level().addFreshEntity(portal);
	}

	public void spawnAbyssMark(LivingEntity user, LivingEntity target) {
		if (user.level().isClientSide()) return;
		if (target == null) return;
		user.level().addFreshEntity(new Abyss_Mark_Entity(user.level(),
				target.getX(), target.getY(), target.getZ(), 60, 3.0F, 3.0F, user.getUUID(), user));
	}

	public void spawnTidalTentacle(LivingEntity user, double x, double y, double z) {
		if (user.level().isClientSide()) return;
		var e = new Tidal_Tentacle_Entity(ModEntities.TIDAL_TENTACLE.get(), user.level());
		e.setPos(x, y, z);
		user.level().addFreshEntity(e);
	}

	public void spawnTidalHook(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		var e = new Tidal_Hook_Entity(ModEntities.TIDAL_HOOK.get(), level);
		e.setOwner(user);
		e.setPos(user.getX(), user.getEyeY(), user.getZ());
		e.shoot(dir.x, dir.y, dir.z, 2.0F, 1.0F);
		level.addFreshEntity(e);
	}

	public void spawnAshenBreath(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		float dmg = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		var e = new Ashen_Breath_Entity(ModEntities.ASHEN_BREATH.get(), level, dmg, user);
		e.setPos(user.getX() + dir.x * 1.5D, user.getEyeY(), user.getZ() + dir.z * 1.5D);
		e.setYRot((float) (Mth.atan2(dir.z, dir.x) * Mth.RAD_TO_DEG) - 90.0F);
		e.setXRot((float) (-(Mth.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)) * Mth.RAD_TO_DEG)));
		level.addFreshEntity(e);
	}

	public void spawnBoltStrike(LivingEntity user, double x, double y, double z) {
		if (user.level().isClientSide()) return;
		float dmg = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		user.level().addFreshEntity(new Bolt_strike_Entity(user.level(), x, y, z, 0.0F, 20, dmg, user));
	}

	public void spawnVoidShard(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		var e = new Void_Shard_Entity(ModEntities.VOID_SHARD.get(), level, user);
		e.setPos(user.getX(), user.getEyeY(), user.getZ());
		e.shoot(dir.x, dir.y, dir.z, 2.0F, 1.0F);
		level.addFreshEntity(e);
	}

	public void spawnVoidScatter(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		var e = new Void_Scatter_Arrow_Entity(level, user);
		e.setPos(user.getX(), user.getEyeY(), user.getZ());
		e.shoot(dir.x, dir.y, dir.z, 2.5F, 1.0F);
		level.addFreshEntity(e);
	}

	public void spawnAxeBlade(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		float dmg = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float angle = user.getYRot();
		var e = new Axe_Blade_Entity(user, dir.x, dir.y, dir.z, level, dmg, angle);
		e.setPos(user.getX(), user.getY() + user.getBbHeight() * 0.5D, user.getZ());
		level.addFreshEntity(e);
	}

	public void spawnSandstormProjectile(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		float dmg = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		var e = new Sandstorm_Projectile(user, dir.x, dir.y, dir.z, level, dmg);
		e.setPos(user.getX(), user.getEyeY() - 0.5D, user.getZ());
		level.addFreshEntity(e);
	}

	public void spawnSandstormOrbit(LivingEntity user, int life) {
		spawnSandstormOrbit(user, life, 4.0F, 0.08F, 0.0F);
	}

	public void spawnSandstormOrbit(LivingEntity user, int life, float radius, float speed, float baseOffset) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		for (int i = 0; i < 3; i++) {
			float offset = baseOffset + (float) i * (float) Math.PI * 2f / 3f;
			SandstormOrbitEntity_0327 orbit = new SandstormOrbitEntity_0327(level, user.getX(), user.getY(), user.getZ(),
					life, offset, user, radius, speed);
			level.addFreshEntity(orbit);
		}
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SANDSTORM.get(), SoundSource.HOSTILE,
				0.8F, 1.0F);
	}

	public List<Entity> spawnSandstormGroup(LivingEntity user, int count, int life, float baseRadius, float speed) {
		if (user.level().isClientSide()) return List.of();
		Level level = user.level();
		List<Entity> ans = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			// 复刻原本近战模式：每环三个，环基准相位差 1/3PI，环内差 2/3PI
			float ringBase = (float) (i / 3) * (float) Math.PI / 3f;
			float inRing = (float) (i % 3) * (float) Math.PI * 2f / 3f;
			float offset = ringBase + inRing;
			// 直接生在环上，否则一帧后就派目标的话看起来像从傀儡身上喷出去
			float radius = baseRadius + (i / 3) * 4.0F;
			double sx = user.getX() + Math.cos(offset) * radius;
			double sy = user.getY();
			double sz = user.getZ() + Math.sin(offset) * radius;
			SandstormOrbitEntity_0327 storm = new SandstormOrbitEntity_0327(level, sx, sy, sz,
					life, offset, user, baseRadius, speed);
			storm.setSlot(i);
			level.addFreshEntity(storm);
			ans.add(storm);
		}
		level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SANDSTORM.get(), SoundSource.HOSTILE,
				0.8F, 1.0F);
		return ans;
	}

	public List<Entity> getSandstorms(LivingEntity user) {
		Level level = user.level();
		if (level.isClientSide()) return List.of();
		List<Entity> ans = new ArrayList<>();
		var box = user.getBoundingBox().inflate(32.0D);
		for (var e : level.getEntitiesOfClass(SandstormOrbitEntity_0327.class, box)) {
			if (e.getCaster() == user) ans.add(e);
		}
		ans.sort(Comparator.comparingInt(e -> ((SandstormOrbitEntity_0327) e).getSlot()));
		return ans;
	}

	public void setSandstormOrder(Entity storm, int slot, @Nullable LivingEntity target) {
		if (!(storm instanceof SandstormOrbitEntity_0327 orbit)) return;
		orbit.setSlot(slot);
		orbit.setTarget(target);
	}

	public void spawnPlayerCeraunus(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		var e = new Player_Ceraunus_Entity(level, user);
		e.setPos(user.getX(), user.getEyeY(), user.getZ());
		e.shoot(dir.x, dir.y, dir.z, 2.5F, 1.0F);
		level.addFreshEntity(e);
	}

	public void spawnAccretion(LivingEntity user, Vec3 dir) {
		if (user.level().isClientSide()) return;
		Level level = user.level();
		var e = new Accretion_Entity(ModEntities.ACCRETION.get(), level, user);
		e.setPos(user.getX(), user.getEyeY(), user.getZ());
		e.shoot(dir.x, dir.y, dir.z, 1.6F, 1.0F);
		level.addFreshEntity(e);
	}

	public void spawnWitherSmoke(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().addFreshEntity(new Wither_Smoke_Effect_Entity(user.level(),
				user.getX(), user.getY() + 0.5D, user.getZ()));
	}

	public void screenShake(LivingEntity user, float radius) {
		if (user.level().isClientSide()) return;
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), radius, 0.1F, 0, 20);
	}

	@Override
	public void summonSoulRing(LivingEntity golem) {
		if (golem.level().isClientSide()) return;
		if (golem.level() instanceof ServerLevel sl) {
			double x = golem.getX();
			double y = golem.getY() + golem.getBbHeight() * 0.5;
			double z = golem.getZ();
			float yaw = (float) Math.toRadians(-golem.getYRot());
			float pitch = (float) Math.toRadians(-golem.getXRot());
			// 两个环以相反方向各倾斜一定角度，形成 X 型交叉
			float tilt = (float) Math.toRadians(20);
			float scale = 10.0f * golem.getBbHeight();
			var a = new RingParticleOptions(yaw, pitch + tilt, 40, 86, 236, 204, 1.0f, scale, false, 3);
			var b = new RingParticleOptions(yaw, pitch - tilt, 40, 86, 236, 204, 1.0f, scale, false, 3);
			sl.sendParticles(a, x, y, z, 1, 0, 0, 0, 0);
			sl.sendParticles(b, x, y, z, 1, 0, 0, 0, 0);
		}
	}

	@Override
	public void maledictusChargeSound(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.PHANTOM_SPEAR.get(), SoundSource.PLAYERS, 1.0F, 1.0F + user.getRandom().nextFloat() * 0.1F);
	}

	@Override
	public void maledictusShortRoar(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.MALEDICTUS_SHORT_ROAR.get(), SoundSource.HOSTILE, 1.0F, 1.0F + user.getRandom().nextFloat() * 0.1F);
	}

	@Override
	public void harbingerChargePrepare(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.HARBINGER_CHARGE_PREPARE.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
	}

	@Override
	public void harbingerChargeSound(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.HARBINGER_CHARGE.get(), SoundSource.HOSTILE, 4.0F, 0.65F);
	}

	@Override
	public void maledictusAfterImage(LivingEntity user) {
		if (!(user.level() instanceof ServerLevel sl)) return;
		sl.sendParticles(new AfterImageParticleOptions(user.getId(), 255, 255, 255, false, 5),
				user.getX(), user.getY(), user.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	@Override
	public void monstrosityEarthquakeFX(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.EXPLOSION.get(), SoundSource.HOSTILE, 1.5F, 1.0F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 20, 0.3F, 0, 20);
		if (!(user.level() instanceof ServerLevel sl)) return;
		Vec3 floor = findFloor(user.level(), user.position(), 8);
		double floorY = floor == null ? user.getY() : floor.y;
		sl.sendParticles(new RingParticleOptions(0f, (float) Math.PI / 2f, 35, 255, 255, 255, 1.0F, 30f, false, 0),
				user.getX(), floorY + 0.2, user.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	@Override
	public void scyllaAnchorSlamFX(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.EXPLOSION.get(), SoundSource.HOSTILE, 2.0F, 0.75F + user.getRandom().nextFloat() * 0.1F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 35, 0.1F, 0, 50);
	}

	@Override
	public void remnantChargeSound(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.REMNANT_CHARGE_ROAR.get(), SoundSource.HOSTILE, 3.0F, 1.0F);
	}

	@Override
	public void remnantChargeStep(LivingEntity user) {
		if (user.level().isClientSide()) return;
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
				ModSounds.REMNANT_CHARGE_STEP.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		ScreenShake_Entity.ScreenShake(user.level(), user.position(), 30.0F, 0.1F, 0, 10);
	}

	@Override
	public void remnantTailSlamSound(LivingEntity user) {
		if (user.level().isClientSide()) return;
		// TAIL_SLAM_1 自带延迟感，落地砸地只用 2 和 3
		if (user.getRandom().nextBoolean()) {
			user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
					ModSounds.REMNANT_TAIL_SLAM_2.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		} else {
			user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
					ModSounds.REMNANT_TAIL_SLAM_3.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		}
	}

	private void spawnLightningToGround(LivingEntity user, double x, double z, double minY, double maxY, float rotation, int delay, float size) {
		Level world = user.level();
		BlockPos blockpos = BlockPos.containing(x, maxY, z);
		boolean flag = false;
		double d0 = 0.0;
		do {
			BlockPos below = blockpos.below();
			BlockState bs = world.getBlockState(below);
			if (!bs.isFaceSturdy(world, below, Direction.UP)) continue;
			if (!world.isEmptyBlock(blockpos)) {
				VoxelShape shape = world.getBlockState(blockpos).getCollisionShape(world, blockpos);
				if (!shape.isEmpty()) d0 = shape.max(Direction.Axis.Y);
			}
			flag = true;
			break;
		} while ((blockpos = blockpos.below()).getY() >= Mth.floor(minY) - 1);
		if (flag) {
			world.addFreshEntity(new Lightning_Storm_Entity(world, x, blockpos.getY() + d0, z, rotation, delay,
					(float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage, user, size));
		}
	}

	private double findFloorY(Level world, double x, double y, double z) {
		BlockPos pos = BlockPos.containing(x, y, z);
		double minY = y - 32;
		do {
			BlockPos low = pos.below();
			BlockState lowState = world.getBlockState(low);
			if (lowState.isFaceSturdy(world, low, Direction.UP)) {
				double dy = 0.0;
				if (!world.isEmptyBlock(pos)) {
					VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
					if (!shape.isEmpty()) dy = shape.max(Direction.Axis.Y);
				}
				return pos.getY() + dy;
			}
			pos = pos.below();
		} while (pos.getY() >= minY);
		return y;
	}

// 工具方法

	private void guardianAreaAttack(LivingEntity user, float range, float height, float damage, float hpdamage, int stunticks, float airborne, boolean knockback) {
		if (user.level().isClientSide()) return;
		double atk = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		for (LivingEntity e : user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(range, height, range))) {
			if (e == user || e.isAlliedTo(user)) continue;
			boolean flag = e.hurt(user.level().damageSources().mobAttack(user), (float) (atk * damage + Math.min(atk * damage, e.getMaxHealth() * hpdamage)));
			if (airborne > 0) e.setDeltaMovement(e.getDeltaMovement().add(0, airborne, 0));
			if (flag && stunticks > 0) e.addEffect(new MobEffectInstance(ModEffect.EFFECTSTUN.get(), stunticks));
			if (knockback) launch(user, e);
		}
	}

	private void launch(LivingEntity user, LivingEntity target) {
		double d0 = target.getX() - user.getX();
		double d1 = target.getZ() - user.getZ();
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
		target.push(d0 / d2 * 4.0, 0.2, d1 / d2 * 4.0);
	}

	private void strikeWindmillHalberd(ServerLevel level, Vec3 pos, LivingEntity user, int numberOfBranches, int particlesPerBranch, double initialRadius, double radiusIncrement, double curveFactor, int delay) {
		float angleIncrement = (float) ((Math.PI * 2D) / (double) numberOfBranches);

		for (int branch = 0; branch < numberOfBranches; ++branch) {
			float baseAngle = angleIncrement * (float) branch;

			for (int i = 0; i < particlesPerBranch; ++i) {
				double currentRadius = initialRadius + (double) i * radiusIncrement;
				float currentAngle = (float) ((double) baseAngle + (double) ((float) i * angleIncrement) / initialRadius + (double) ((float) ((double) i * curveFactor)));
				double xOffset = currentRadius * Math.cos(currentAngle);
				double zOffset = currentRadius * Math.sin(currentAngle);
				double spawnX = pos.x() + xOffset;
				double spawnY = pos.y() + 0.3;
				double spawnZ = pos.z() + zOffset;
				int d3 = delay + i + 1;
				level.sendParticles(ModParticle.PHANTOM_WING_FLAME.get(), spawnX, spawnY, spawnZ, 1, 0.0F, 0.0F, 0.0F, 0.007);
				spawnHalberd(spawnX, spawnZ, pos.y() - (double) 5.0F, pos.y() + (double) 3.0F, currentAngle, d3, level, user);
			}
		}

	}

	private void spawnHalberd(double x, double z, double minY, double maxY, float rotation, int delay, Level world, LivingEntity player) {
		BlockPos blockpos = BlockPos.containing(x, maxY, z);
		boolean flag = false;
		double d0 = 0.0;

		do {
			BlockPos blockpos1 = blockpos.below();
			BlockState blockstate = world.getBlockState(blockpos1);
			if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
				if (!world.isEmptyBlock(blockpos)) {
					BlockState blockstate1 = world.getBlockState(blockpos);
					VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
					if (!voxelshape.isEmpty()) {
						d0 = voxelshape.max(Direction.Axis.Y);
					}
				}

				flag = true;
				break;
			}

			blockpos = blockpos.below();
		} while (blockpos.getY() >= Mth.floor(minY) - 1);

		if (flag) {
			world.addFreshEntity(new Phantom_Halberd_Entity(world, x, (double) blockpos.getY() + d0, z, rotation, delay, player, (float) CMCommonConfig.SoulRender.phantomHalberdDamage));
		}

	}

	private boolean spawnVortex(double x, double y, double z, int lowestYCheck, float rotation, Level world, LivingEntity player) {
		BlockPos blockpos = BlockPos.containing(x, y, z);
		boolean flag = false;
		double d0 = 0.0;

		do {
			BlockPos blockpos1 = blockpos.below();
			BlockState blockstate = world.getBlockState(blockpos1);
			if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
				if (!world.isEmptyBlock(blockpos)) {
					BlockState blockstate1 = world.getBlockState(blockpos);
					VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
					if (!voxelshape.isEmpty()) {
						d0 = voxelshape.max(Direction.Axis.Y);
					}
				}

				flag = true;
				break;
			}

			blockpos = blockpos.below();
		} while (blockpos.getY() >= lowestYCheck);

		if (flag) {
			world.addFreshEntity(new Void_Vortex_Entity(world, x, (double) blockpos.getY() + d0, z, rotation, player, 150));
			return true;
		} else {
			return false;
		}
	}

	private boolean spawnFangs(double x, double maxY, double z, double minY, float rot, int delay, Level level, LivingEntity player) {
		BlockPos pos = BlockPos.containing(x, maxY, z);
		boolean flag = false;
		double dy = 0.0;

		do {
			BlockPos below = pos.below();
			BlockState state = level.getBlockState(below);
			if (state.isFaceSturdy(level, below, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState next = level.getBlockState(pos);
					VoxelShape shape = next.getCollisionShape(level, pos);
					if (!shape.isEmpty()) {
						dy = shape.max(Direction.Axis.Y);
					}
				}
				flag = true;
				break;
			}

			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);

		if (flag) {
			level.addFreshEntity(new Void_Rune_Entity(level, x, pos.getY() + dy, z, rot, delay, (float) CMCommonConfig.VoidForge.runeDamage, player));
			return true;
		} else {
			return false;
		}
	}

	private void infernalForgeParticles(ServerLevel level, LivingEntity entity, double radius) {
		BlockState block = level.getBlockState(entity.blockPosition().below());
		double n = radius * 4.0;
		for (double i = 0.0; i < 80.0; ++i) {
			double d0 = entity.getX() + radius * (double) Mth.sin((float) (i / n * 360.0));
			double d1 = entity.getY() + 0.15;
			double d2 = entity.getZ() + radius * (double) Mth.cos((float) (i / n * 360.0));
			level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, block), d0, d1, d2, 1,
					0, 0, 0, 0.2);
		}
	}

}
