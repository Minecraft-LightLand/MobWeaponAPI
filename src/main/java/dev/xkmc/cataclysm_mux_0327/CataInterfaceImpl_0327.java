package dev.xkmc.cataclysm_mux_0327;

import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Portal_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Portal_Abyss_Blast_Entity;
import com.github.L_Ender.cataclysm.entity.effect.*;
import com.github.L_Ender.cataclysm.entity.projectile.*;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CataInterfaceImpl_0327 implements CataInterface {

	// 逻辑
	public boolean isLaser(DamageSource source) {
		return source.getDirectEntity() instanceof Death_Laser_Beam_Entity;
	}

	public boolean isMissile(DamageSource source) {
		return source.getDirectEntity() instanceof Wither_Homing_Missile_Entity;
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
		user.level().addFreshEntity(shot);

	}

	public void createBlast(LivingEntity user, Vec3 pos, int dur, int delay, float radius, float dmg, boolean soul) {

		user.level().addFreshEntity(
				new Flame_Strike_Entity(user.level(), pos.x, pos.y, pos.z,
						user.getYRot(), dur, delay, delay, radius,
						dmg, 6f, soul, user));


	}

// 攻击 - 先驱者

	public void addMissile(LivingEntity user, LivingEntity target, Vec3 pos) {

		var diff = target.getEyePosition().subtract(pos).normalize();
		Wither_Homing_Missile_Entity laserBeam = new Wither_Homing_Missile_Entity(user, diff, user.level(), (float) CMCommonConfig.Harbinger.WitherMissiledamage, target);
		laserBeam.setPosRaw(pos.x(), pos.y(), pos.z());
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
		worldIn.addFreshEntity(b);
	}

// 攻击 - 末影守卫

	public int spawnVortex(LivingEntity user, Vec3 pos) {

		int minY = Mth.floor(user.getY()) - 10;
		float yrot = (float) Math.toRadians(90.0F + user.getYRot());
		if (spawnVortex(pos.x, pos.y, pos.z, minY, yrot, user.level(), user))
			return CMCommonConfig.GauntletOfMaelstrom.cooldown;

		return 20;
	}

	public int spawnVoidFangs(LivingEntity user, Vec3 dir) {

		int standingOnY = Mth.floor(user.getY()) - 3;
		Level level = user.level();
		double headY = user.getY() + 1.0;
		Vec3[] all = new Vec3[]{dir, dir.yRot(0.3F), dir.yRot(-0.3F), dir.yRot(0.6F), dir.yRot(-0.6F), dir.yRot(0.9F), dir.yRot(-0.9F)};
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
		for (int j = 1; j <= 15; ++j) {
			double dist = 1.25 * j;
			spawnFangs(user.getX() + Mth.cos(angle) * dist, maxY, user.getZ() + Mth.sin(angle) * dist, minY, angle, j, user.level(), user);

		}

	}

// 攻击 - 利维坦

	public void spawnBlastPortal(LivingEntity user, double x, double y, double z, float rotation, int delay) {

		user.level().addFreshEntity(new Abyss_Blast_Portal_Entity(user.level(), x, y, z, rotation, delay,
				(float) CMCommonConfig.Leviathan.AbyssBlastDamage, (float) CMCommonConfig.Leviathan.AbyssBlastHpDamage, user));

	}

// 攻击 - 咒翼灵骸

	public int spawnHalberd(Vec3 pos, LivingEntity player, int delay) {

		if (player.level() instanceof ServerLevel sl) {
			strikeWindmillHalberd(sl, pos, player, 7, 5, 1.0F, 1.0F, 0.2, delay);
		}
		return CMCommonConfig.SoulRender.cooldown;

	}

// 武器兼容

	@Nullable
	public AbstractArrow createGhostArrow(Level level, LivingEntity player, LivingEntity target) {

		Phantom_Arrow_Entity hommingArrowEntity;
		hommingArrowEntity = new Phantom_Arrow_Entity(level, player, target);
		hommingArrowEntity.setBaseDamage(CMCommonConfig.Maledictus.PhantomArrowDamage);
		return hommingArrowEntity;


	}

	@Nullable
	public Entity createGhostStorm(LivingEntity user, Vec3 pos, Vec3 rot, LivingEntity target) {

		Cursed_Sandstorm_Entity e = new Cursed_Sandstorm_Entity(user, rot.x, rot.y, rot.z, user.level(),
				(float) CMCommonConfig.Wadjet.Sandstorm_damage, target);
		e.setPos(pos.x, user.getEyeY() - 0.5, pos.z);
		e.setUp(15);
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
		level.addFreshEntity(rocket);
		return CMCommonConfig.WASW.missileCooldown;


	}

	public int infernalForge(LivingEntity user, LivingEntity target) {
		int radius = 4;
		Level level = user.level();
		ScreenShake_Entity.ScreenShake(level, target.position(), 30.0F, 0.1F, 0, 30);
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
		Lightning_Spear_Entity lightning = new Lightning_Spear_Entity(user, vec3.normalize(), level, (float) CMCommonConfig.Astrape.damage, 0.0);
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
		return throwntrident;
	}

// 工具方法

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
