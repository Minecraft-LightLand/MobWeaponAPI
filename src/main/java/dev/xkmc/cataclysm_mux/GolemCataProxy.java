package dev.xkmc.cataclysm_mux;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.xkmc.cataclysm_mux.CataclysmMux.LOGGER;

public class GolemCataProxy {

	public static void sandstormAttack(LivingEntity golem, LivingEntity target, int life) {
		try {
			CataInterface.get().sandstormAttack(golem, target, life);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static boolean isLaser(DamageSource source) {
		try {
			return CataInterface.get().isLaser(source);
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isMissile(DamageSource source) {
		try {
			return CataInterface.get().isMissile(source);
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * 凋灵飞弹（Wither_Missile_Entity）。与 {@link #isMissile} 的巡导弹不同，是另一个发射物。
	 */
	public static boolean isWitherMissile(DamageSource source) {
		try {
			return CataInterface.get().isWitherMissile(source);
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * 凋灵榴弹（Wither_Howitzer_Entity）。
	 */
	public static boolean isHowitzer(DamageSource source) {
		try {
			return CataInterface.get().isHowitzer(source);
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isSandstorm(DamageSource source) {
		try {
			return CataInterface.get().isSandstorm(source);
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isIgnisExplosive(Entity entity) {
		try {
			return CataInterface.get().isIgnisExplosive(entity);
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isSoul(Entity entity) {
		try {
			return CataInterface.get().isSoul(entity);
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isAbyssFireball(Entity entity) {
		try {
			return CataInterface.get().isAbyssFireball(entity);
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isIgnisStrike(Entity entity) {
		try {
			return CataInterface.get().isIgnisStrike(entity);
		} catch (Throwable e) {
			return false;
		}
	}

	public static int getSandCurseLevel(LivingEntity e) {
		try {
			return CataInterface.get().getSandCurseLevel(e);
		} catch (Throwable ignored) {
			return 0;
		}
	}

	@Nullable
	public static Entity addLaserBeam(LivingEntity user, int dur) {
		try {
			return CataInterface.get().addLaserBeam(user, dur);
		} catch (Throwable e) {
			LOGGER.error(e);
			return null;
		}
	}

	public static void addMissile(LivingEntity user, LivingEntity target, Vec3 pos) {
		try {
			CataInterface.get().addMissile(user, target, pos);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void addRune(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().addRune(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnBlastPortal(LivingEntity user, double x, double y, double z, float rotation, int delay) {
		try {
			CataInterface.get().spawnBlastPortal(user, x, y, z, rotation, delay);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void stackBlazingBrand(LivingEntity golem, LivingEntity target, float dmg, int min) {
		try {
			CataInterface.get().golemStackBlazingBrandRaw(golem, target, dmg, min);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void shootFireball(LivingEntity user, Vec3 shotAt, int timer, boolean abyss, boolean isBlue) {
		try {
			CataInterface.get().shootFireball(user, shotAt, timer, abyss, isBlue);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void createBlast(LivingEntity user, Vec3 pos, int dur, int delay, float radius, float dmg, boolean soul) {
		try {
			CataInterface.get().createBlast(user, pos, dur, delay, radius, dmg, soul);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static float monstrosityEarthquakeDamage() {
		try {
			return CataInterface.get().monstrosityEarthquakeDamage();
		} catch (Throwable e) {
			LOGGER.error(e);
		}
		return 0;
	}

	public static float maledictusEarthquakeDamage() {
		try {
			return CataInterface.get().maledictusEarthquakeDamage();
		} catch (Throwable e) {
			LOGGER.error(e);
		}
		return 0;
	}

	public static void updateLaser(LivingEntity golem, Entity e) {
		try {
			CataInterface.get().updateLaser(golem, e);
		} catch (Throwable ignored) {
		}
	}

	public static void inflictStun(LivingEntity user, LivingEntity le, int time) {
		try {
			CataInterface.get().inflictStun(user, le, time);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	@Nullable
	public static LivingEntity getOwner(Entity entity) {
		try {
			return CataInterface.get().getOwner(entity);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
		return null;
	}

	public static void spawnDesertStele(LivingEntity user, double x, double y, double z, int delay) {
		try {
			CataInterface.get().spawnDesertStele(user, x, y, z, delay);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnEarthQuake(LivingEntity user) {
		try {
			CataInterface.get().spawnEarthQuake(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void remnantRoarParticle(LivingEntity golem, float vec, float math, float y) {
		try {
			CataInterface.get().remnantRoarParticle(golem, vec, math, y);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void remnantChargeSound(LivingEntity user) {
		try {
			CataInterface.get().remnantChargeSound(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void remnantChargeStep(LivingEntity user) {
		try {
			CataInterface.get().remnantChargeStep(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void remnantTailSlamSound(LivingEntity user) {
		try {
			CataInterface.get().remnantTailSlamSound(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnFlameJet(LivingEntity user, double x, double y, double z, int delay) {
		try {
			CataInterface.get().spawnFlameJet(user, x, y, z, delay);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	@Nullable
	public static Entity addAbyssBlast(LivingEntity user, int dur) {
		try {
			return CataInterface.get().addAbyssBlast(user, dur);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
		return null;
	}

	public static void spawnAbyssOrb(LivingEntity user, Vec3 dir, LivingEntity target) {
		try {
			CataInterface.get().spawnAbyssOrb(user, dir, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnAbyssMine(LivingEntity user, double x, double y, double z, int delay) {
		try {
			CataInterface.get().spawnAbyssMine(user, x, y, z, delay);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnDimensionalRift(LivingEntity user, double x, double y, double z) {
		try {
			CataInterface.get().spawnDimensionalRift(user, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void lightningStorm(LivingEntity user, double x, double y, double z, float size) {
		try {
			CataInterface.get().lightningStorm(user, x, y, z, size);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void summonStormSerpent(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().summonStormSerpent(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void shootHowitzer(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().shootHowitzer(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void fallingBlocks(LivingEntity user, double inflate) {
		try {
			CataInterface.get().fallingBlocks(user, inflate);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void shootEnderBullet(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().shootEnderBullet(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void gravityPull(LivingEntity user) {
		try {
			CataInterface.get().gravityPull(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void massDestruction(LivingEntity user) {
		try {
			CataInterface.get().massDestruction(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void burst(LivingEntity user) {
		try {
			CataInterface.get().burst(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void rocketPunch(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().rocketPunch(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void hugMe(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().hugMe(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void throwLavaBomb(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().throwLavaBomb(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void throwFlareBomb(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().throwFlareBomb(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void shoulderCheck(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().shoulderCheck(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void shootSpark(LivingEntity user, float areaRadius) {
		try {
			CataInterface.get().shootSpark(user, areaRadius);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnLightningArea(LivingEntity user, double x, double y, double z) {
		try {
			CataInterface.get().spawnLightningArea(user, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaSummonWave(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().scyllaSummonWave(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaSummonWave2(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().scyllaSummonWave2(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning) {
		try {
			CataInterface.get().scyllaSpearThrow(user, target, lightning);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning, float lateral) {
		try {
			CataInterface.get().scyllaSpearThrow(user, target, lightning, lateral);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaFallingBurst(LivingEntity user, int sparkAmount) {
		try {
			CataInterface.get().scyllaFallingBurst(user, sparkAmount);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaFallingBurstAt(LivingEntity user, double x, double y, double z, int sparkAmount) {
		try {
			CataInterface.get().scyllaFallingBurstAt(user, x, y, z, sparkAmount);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaLightningExplosion(LivingEntity user, double x, double y, double z) {
		try {
			CataInterface.get().scyllaLightningExplosion(user, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaBurstLightning(LivingEntity user) {
		try {
			CataInterface.get().scyllaBurstLightning(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaWhip(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().scyllaWhip(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaSpin(LivingEntity user) {
		try {
			CataInterface.get().scyllaSpin(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void guardianStompFangs(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().guardianStompFangs(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void guardianUppercut(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().guardianUppercut(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void guardianHugFX(LivingEntity user) {
		try {
			CataInterface.get().guardianHugFX(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void guardianGravityPunch(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().guardianGravityPunch(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void guardianExplosionSound(LivingEntity user) {
		try {
			CataInterface.get().guardianExplosionSound(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void guardianSlamLanding(LivingEntity user) {
		try {
			CataInterface.get().guardianSlamLanding(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaSpinSound(LivingEntity user) {
		try {
			CataInterface.get().scyllaSpinSound(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaStormParticles(LivingEntity user) {
		try {
			CataInterface.get().scyllaStormParticles(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaThunderCloud(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().scyllaThunderCloud(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaThunderCloudStorm(LivingEntity user, double x, double y, double z) {
		try {
			CataInterface.get().scyllaThunderCloudStorm(user, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaThunderCloudSpear(LivingEntity user, LivingEntity target, double x, double y, double z) {
		try {
			CataInterface.get().scyllaThunderCloudSpear(user, target, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void ignisEarthquakeFX(LivingEntity user, float shakeRadius) {
		try {
			CataInterface.get().ignisEarthquakeFX(user, shakeRadius);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void ignisAreaPunch(LivingEntity user, float radius) {
		try {
			CataInterface.get().ignisAreaPunch(user, radius);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void ignisQuakeWave(LivingEntity user, int distance, float mxy, float vec, boolean grab, float damage, float hpdamage, float airborne, boolean blue) {
		try {
			CataInterface.get().ignisQuakeWave(user, distance, mxy, vec, grab, damage, hpdamage, airborne, blue);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void ignisQuakeWaveAt(LivingEntity user, Vec3 center, int distance, float mxy, float vec, boolean grab, float damage, float hpdamage, float airborne, boolean blue) {
		try {
			CataInterface.get().ignisQuakeWaveAt(user, center, distance, mxy, vec, grab, damage, hpdamage, airborne, blue);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void ignisQuakePush(LivingEntity user, int distance, float mxy, float vec, float damage, float hpdamage, float airborne, boolean blue) {
		try {
			CataInterface.get().ignisQuakePush(user, distance, mxy, vec, damage, hpdamage, airborne, blue);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void ignisBoneStorm(LivingEntity user, float radius) {
		try {
			CataInterface.get().ignisBoneStorm(user, radius);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	// 备用 - 8 Boss补全

	public static void spawnMiniAbyssBlast(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnMiniAbyssBlast(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnPortalAbyssBlast(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnPortalAbyssBlast(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnAbyssPortal(LivingEntity user, double x, double y, double z) {
		try {
			CataInterface.get().spawnAbyssPortal(user, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnAbyssMark(LivingEntity user, LivingEntity target) {
		try {
			CataInterface.get().spawnAbyssMark(user, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnTidalTentacle(LivingEntity user, double x, double y, double z) {
		try {
			CataInterface.get().spawnTidalTentacle(user, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnTidalHook(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnTidalHook(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnAshenBreath(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnAshenBreath(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnBoltStrike(LivingEntity user, double x, double y, double z) {
		try {
			CataInterface.get().spawnBoltStrike(user, x, y, z);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnVoidShard(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnVoidShard(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnVoidScatter(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnVoidScatter(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnAxeBlade(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnAxeBlade(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnSandstormProjectile(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnSandstormProjectile(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnSandstormOrbit(LivingEntity user, int life) {
		try {
			CataInterface.get().spawnSandstormOrbit(user, life);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnSandstormOrbit(LivingEntity user, int life, float radius, float speed, float baseOffset) {
		try {
			CataInterface.get().spawnSandstormOrbit(user, life, radius, speed, baseOffset);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static List<Entity> spawnSandstormGroup(LivingEntity user, int count, int life, float baseRadius, float speed) {
		try {
			return CataInterface.get().spawnSandstormGroup(user, count, life, baseRadius, speed);
		} catch (Throwable e) {
			LOGGER.error(e);
			return List.of();
		}
	}

	public static List<Entity> getSandstorms(LivingEntity user) {
		try {
			return CataInterface.get().getSandstorms(user);
		} catch (Throwable e) {
			LOGGER.error(e);
			return List.of();
		}
	}

	public static void setSandstormOrder(Entity storm, int slot, @Nullable LivingEntity target) {
		try {
			CataInterface.get().setSandstormOrder(storm, slot, target);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnPlayerCeraunus(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnPlayerCeraunus(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnAccretion(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().spawnAccretion(user, dir);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void spawnWitherSmoke(LivingEntity user) {
		try {
			CataInterface.get().spawnWitherSmoke(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void screenShake(LivingEntity user, float radius) {
		try {
			CataInterface.get().screenShake(user, radius);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void summonSoulRing(LivingEntity golem) {
		try {
			CataInterface.get().summonSoulRing(golem);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void maledictusChargeSound(LivingEntity user) {
		try {
			CataInterface.get().maledictusChargeSound(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void maledictusShortRoar(LivingEntity user) {
		try {
			CataInterface.get().maledictusShortRoar(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void harbingerChargePrepare(LivingEntity user) {
		try {
			CataInterface.get().harbingerChargePrepare(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void harbingerChargeSound(LivingEntity user) {
		try {
			CataInterface.get().harbingerChargeSound(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void maledictusAfterImage(LivingEntity user) {
		try {
			CataInterface.get().maledictusAfterImage(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void monstrosityEarthquakeFX(LivingEntity user) {
		try {
			CataInterface.get().monstrosityEarthquakeFX(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void scyllaAnchorSlamFX(LivingEntity user) {
		try {
			CataInterface.get().scyllaAnchorSlamFX(user);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

}
