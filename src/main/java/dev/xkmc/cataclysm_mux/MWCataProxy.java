package dev.xkmc.cataclysm_mux;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static dev.xkmc.cataclysm_mux.CataclysmMux.LOGGER;

public class MWCataProxy {

	@Nullable
	public static AbstractArrow createGhostArrow(Level level, LivingEntity player, LivingEntity target) {
		try {
			return CataInterface.get().createGhostArrow(level, player, target);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

	@Nullable
	public static Entity createGhostStorm(LivingEntity user, Vec3 pos, Vec3 rot, LivingEntity target) {
		try {
			return CataInterface.get().createGhostStorm(user, pos, rot, target);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

	public static void shootLaserGatling(LivingEntity user, Vec3 vec3) {
		try {
			CataInterface.get().shootLaserGatling(user, vec3);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
	}

	public static @Nullable CataInterface.ProjectileData shootVoid(LivingEntity player) {
		try {
			return CataInterface.get().shootVoid(player);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

	public static int shootMissile(LivingEntity player, Vec3 dir) {
		try {
			return CataInterface.get().shootMissile(player, dir);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}

	public static int spawnHalberd(LivingEntity player) {
		try {
			return CataInterface.get().spawnHalberd(player.position(), player, 0);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}

	public static void launchTornado(LivingEntity user, Vec3 dir) {
		try {
			CataInterface.get().launchTornado(user, dir);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
	}

	public static int spawnVortex(LivingEntity user, Vec3 pos) {
		try {
			return CataInterface.get().spawnVortex(user, pos);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}

	public static int spawnVoidFangs(LivingEntity user, Vec3 dir) {
		try {
			return CataInterface.get().spawnVoidFangs(user, dir);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}

	public static int infernalForge(LivingEntity user, LivingEntity target) {
		try {
			return CataInterface.get().infernalForge(user, target);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}

	public static int ceraunus(Level level, LivingEntity user, LivingEntity target) {
		try {
			return CataInterface.get().ceraunus(level, user, target);
		} catch (Throwable ignored) {
		}
		return 20;
	}

	public static int astrape(Level level, LivingEntity user, LivingEntity target) {
		try {
			return CataInterface.get().astrape(level, user, target);
		} catch (Throwable ignored) {
		}
		return 20;
	}

	@Nullable
	public static Projectile coralSpear(LivingEntity user, Level level, ItemStack stack) {
		try {
			return CataInterface.get().coralSpear(user, level, stack);
		} catch (Throwable ignored) {
		}
		return null;
	}

}
