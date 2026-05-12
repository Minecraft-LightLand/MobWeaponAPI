package dev.xkmc.cataclysm_mux;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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

}
