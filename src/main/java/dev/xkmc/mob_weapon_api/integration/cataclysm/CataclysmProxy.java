package dev.xkmc.mob_weapon_api.integration.cataclysm;

import com.github.L_Ender.cataclysm.config.CMConfig;
import com.github.L_Ender.cataclysm.entity.projectile.*;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class CataclysmProxy {

	public static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	public static AbstractArrow createGhostArrow(Level level, LivingEntity player, LivingEntity target) {
		try {
			Phantom_Arrow_Entity hommingArrowEntity;
			hommingArrowEntity = new Phantom_Arrow_Entity(level, player, target);
			hommingArrowEntity.setBaseDamage(CMConfig.PlayerPhantomArrowbasedamage);
			return hommingArrowEntity;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

	@Nullable
	public static Entity createGhostStorm(LivingEntity user, Vec3 pos, Vec3 rot, LivingEntity target) {
		try {
			Cursed_Sandstorm_Entity e = new Cursed_Sandstorm_Entity(user, rot.x, rot.y, rot.z, user.level(),
					(float) CMConfig.CursedSandstormDamage, target);
			e.setPos(pos.x, user.getEyeY() - 0.5, pos.z);
			e.setUp(15);
			return e;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

	public static void shootLaserGatling(LivingEntity user, Vec3 vec3) {
		try {
			Level level = user.level();
			Laser_Beam_Entity laser = new Laser_Beam_Entity(user, vec3.x, vec3.y, vec3.z, level, (float) CMConfig.Laserdamage);
			float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * Mth.RAD_TO_DEG) + 90;
			float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * Mth.RAD_TO_DEG));
			laser.setYRot(yRot);
			laser.setXRot(xRot);
			laser.setPosRaw(user.getX(), user.getY() + (double) (user.getEyeHeight() * 0.8F), user.getZ());
			RandomSource rand = level.getRandom();
			user.gameEvent(GameEvent.ITEM_INTERACT_START);
			user.playSound(ModSounds.HARBINGER_LASER.get(), 0.2F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			level.addFreshEntity(laser);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
	}

	public static @Nullable ProjectileData shootVoid(LivingEntity player) {
		try {
			Level level = player.level();
			level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ROCKET_LAUNCH.get(), SoundSource.PLAYERS, 1.0F, 0.7F);
			Void_Howitzer_Entity rocket = new Void_Howitzer_Entity(ModEntities.VOID_HOWITZER.get(), level, player);
			return new ProjectileData(rocket, 1, 0.03f, CMConfig.VASWCooldown);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

	public static int shootMissile(LivingEntity player, Vec3 dir) {
		try {
			Level level = player.level();
			Vec3 offset = dir.multiply(1, 0, 1).normalize();
			double x = player.getX() + offset.x;
			double z = player.getZ() + offset.y;
			Wither_Missile_Entity rocket = new Wither_Missile_Entity(ModEntities.WITHER_MISSILE.get(),
					player, x, player.getEyeY(), z, dir.x, dir.y, dir.z, (float) CMConfig.WASWMissileDamage, level);
			level.addFreshEntity(rocket);
			return CMConfig.WASWMissileCooldown;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;

	}

	public record ProjectileData(Projectile proj, float speed, float gravity, int cooldown) {

	}

}
