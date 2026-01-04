package dev.xkmc.mob_weapon_api.integration.cataclysm;

import com.github.L_Ender.cataclysm.config.CMConfig;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Void_Vortex_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.*;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
			Laser_Beam_Entity laser = new Laser_Beam_Entity(user, vec3, level, (float) CMConfig.Laserdamage);
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
					player, x, player.getEyeY(), z, dir, (float) CMConfig.WASWMissileDamage, level);
			level.addFreshEntity(rocket);
			return CMConfig.WASWMissileCooldown;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;

	}

	public static int spawnHalberd(LivingEntity player) {
		try {
			if (player.level() instanceof ServerLevel sl)
				Helper.strikeWindmillHalberd(sl, player, 7, 5, 1.0, 1.0, 0.2, 1);
			return CMConfig.SoulRenderCooldown;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}

	public static void launchTornado(LivingEntity user, Vec3 dir) {
		try {
			Level worldIn = user.level();
			Vec3 offset = dir.multiply(1, 0, 1);
			double x = user.getX() + offset.x;
			double z = user.getZ() + offset.z;
			Sandstorm_Projectile b = new Sandstorm_Projectile(user, dir.x, dir.y, dir.z, user.level(), 6.0F);
			b.setState(1);
			b.setPos(x, user.getEyeY() - 0.5, z);
			worldIn.addFreshEntity(b);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
	}

	public static int spawnVortex(LivingEntity user, Vec3 pos) {
		try {
			int minY = Mth.floor(user.getY()) - 10;
			float yrot = (float) Math.toRadians(90.0F + user.getYRot());
			if (Helper.spawnVortex(pos.x, pos.y, pos.z, minY, yrot, user.level(), user))
				return CMConfig.GauntletOfMaelstromCooldown;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}

	public static int spawnVoidFangs(LivingEntity user, Vec3 dir) {
		try {
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
					Helper.spawnFangs(user.getX() + (double) Mth.cos(f) * d2, headY, user.getZ() + (double) Mth.sin(f) * d2, standingOnY, f, i, level, user);
				}
			}
			return CMConfig.VoidForgeCooldown;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}


	public static int infernalForge(LivingEntity user, LivingEntity target) {
		try {
			return Helper.infernal(user, target, 4);
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return 20;
	}


	public record ProjectileData(Projectile proj, float speed, float gravity, int cooldown) {

	}

	public static class Helper {

		private static void strikeWindmillHalberd(ServerLevel level, LivingEntity player, int numberOfBranches, int particlesPerBranch, double initialRadius, double radiusIncrement, double curveFactor, int delay) {
			float angleIncrement = (float) (6.283185307179586 / (double) numberOfBranches);

			for (int branch = 0; branch < numberOfBranches; ++branch) {
				float baseAngle = angleIncrement * (float) branch;

				for (int i = 0; i < particlesPerBranch; ++i) {
					double currentRadius = initialRadius + (double) i * radiusIncrement;
					float currentAngle = (float) ((double) baseAngle + (double) ((float) i * angleIncrement) / initialRadius + (double) ((float) ((double) i * curveFactor)));
					double xOffset = currentRadius * Math.cos(currentAngle);
					double zOffset = currentRadius * Math.sin(currentAngle);
					double spawnX = player.getX() + xOffset;
					double spawnY = player.getY() + 0.3;
					double spawnZ = player.getZ() + zOffset;
					int d3 = delay * (i + 1);

					level.sendParticles(ModParticle.PHANTOM_WING_FLAME.get(), spawnX, spawnY, spawnZ,
							1, 0, 0, 0, 0.007);

					spawnHalberd(spawnX, spawnZ, player.getY() - 5.0, player.getY() + 3.0, currentAngle, d3, level, player);
				}
			}

		}

		private static void spawnHalberd(double x, double z, double minY, double maxY, float rotation, int delay, Level world, LivingEntity player) {
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
				world.addFreshEntity(new Phantom_Halberd_Entity(world, x, (double) blockpos.getY() + d0, z, rotation, delay, player, (float) CMConfig.PhantomHalberddamage));
			}

		}

		private static boolean spawnVortex(double x, double y, double z, int lowestYCheck, float rotation, Level world, LivingEntity player) {
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

		private static boolean spawnFangs(double x, double y, double z, int lowestYCheck, float yRot, int warmupDelayTicks, Level world, LivingEntity player) {
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
				world.addFreshEntity(new Void_Rune_Entity(world, x, (double) blockpos.getY() + d0, z, yRot, warmupDelayTicks, (float) CMConfig.Voidrunedamage, player));
				return true;
			} else {
				return false;
			}
		}

		private static int infernal(LivingEntity user, LivingEntity target, double radius) {
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
			return CMConfig.InfernalForgeCooldown;
		}

		private static void infernalForgeParticles(ServerLevel level, LivingEntity entity, double radius) {
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

	public static int ceraunus(Level level, LivingEntity user, LivingEntity target) {
		try {
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
				Wave_Entity WaveEntity = new Wave_Entity(level, user, 60, (float) CMConfig.CeraunusWaveDamage);
				WaveEntity.setPos(spawnX, spawnY, spawnZ);
				WaveEntity.setState(1);
				WaveEntity.setYRot(-((float) (Mth.atan2(dx, dz) * (180D / Math.PI))));
				level.addFreshEntity(WaveEntity);
			}
			return CMConfig.CeraunusCooldown;
		} catch (Throwable ignored) {
		}
		return 20;
	}

	public static int astrape(Level level, LivingEntity user, LivingEntity target) {
		try {
			level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.EMP_ACTIVATED.get(), SoundSource.PLAYERS, 1.0F, 0.8F);
			Vec3 vec3 = target.getEyePosition().subtract(user.getEyePosition()).normalize();
			double x = user.getX() + vec3.x;
			double y = user.getEyeY();
			double Z = user.getZ() + vec3.z;
			float yRot = (float) (Mth.atan2(vec3.z, vec3.x) * (180D / Math.PI)) + 90.0F;
			float xRot = (float) (-(Mth.atan2(vec3.y, Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z)) * (180D / Math.PI)));
			Lightning_Spear_Entity lightning = new Lightning_Spear_Entity(user, vec3.normalize(), level, (float) CMConfig.AstrapeDamage);
			lightning.accelerationPower = 0.15;
			lightning.setYRot(yRot);
			lightning.setXRot(xRot);
			lightning.setPosRaw(x, y, Z);
			lightning.setAreaDamage((float) CMConfig.AstrapeAreaDamage);
			lightning.setAreaRadius(1.0F);
			level.addFreshEntity(lightning);
			return CMConfig.AstrapeCooldown;
		} catch (Throwable ignored) {

		}
		return 20;
	}

	@Nullable
	public static Projectile coralSpear(LivingEntity user, Level level, ItemStack stack) {
		try {
			ThrownCoral_Spear_Entity throwntrident = new ThrownCoral_Spear_Entity(level, user, stack);
			throwntrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			throwntrident.setPos(user.getEyePosition().add(user.getForward()));
			return throwntrident;
		} catch (Throwable ignored) {

		}
		return null;
	}

}
