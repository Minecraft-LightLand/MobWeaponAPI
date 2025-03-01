package dev.xkmc.mob_weapon_api.integration.create;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.potatoCannon.*;
import com.simibubi.create.content.equipment.zapper.ShootGadgetPacket;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public class CreateProxy {

	public static final Logger LOGGER = LogManager.getLogger();

	private static boolean error = false;

	public static int shootPotato(BowUseContext user, ItemStack stack, InteractionHand hand) {
		if (error) return 20;
		try {
			return Helper.shootPotato(user, stack, hand);
		} catch (Throwable e) {
			LOGGER.throwing(e);
			error = true;
		}
		return 20;
	}

	private static class Helper {

		private static int shootPotato(BowUseContext user, ItemStack stack, InteractionHand hand) {
			if (!(stack.getItem() instanceof PotatoCannonItem)) return 20;
			var e = user.user();
			var level = e.level();
			ItemStack ammo = user.getPreferredProjectile(stack);
			Vec3 src = getGunBarrelVec(e, hand == InteractionHand.MAIN_HAND, new Vec3(0.75, -0.15, 1.5));
			PotatoCannonProjectileType type = PotatoProjectileTypeManager.getTypeForStack(ammo).orElse(BuiltinPotatoProjectileTypes.FALLBACK);
			float soundPitch = type.getSoundPitch() + (e.getRandom().nextFloat() - 0.5F) / 4.0F;
			boolean spray = type.getSplit() > 1;
			Vec3 sprayBase = VecHelper.rotate(new Vec3(0.0, 0.1, 0.0), 360.0F * e.getRandom().nextFloat(), Direction.Axis.Z);
			float sprayChange = 360.0F / (float) type.getSplit();

			float gravity = 0.05f * type.getGravityMultiplier();
			float speed = 2f * type.getVelocityMultiplier();
			var cons = user.aim(src, speed, gravity, user.getInitialInaccuracy());
			Vec3 motion = null;


			ItemStack copy = stack.copy();
			if (type.getSplit() > 1) {
				var old = EnchantmentHelper.getEnchantments(copy);
				if (old.remove(AllEnchantments.POTATO_RECOVERY.get()) != null) {
					EnchantmentHelper.setEnchantments(old, copy);
				}
			}
			for (int i = 0; i < type.getSplit(); ++i) {
				PotatoProjectileEntity proj = AllEntityTypes.POTATO_PROJECTILE.create(level);
				if (proj == null) continue;
				proj.setItem(ammo);

				if (user.bypassAllConsumption() || i != 0) {
					proj.setEnchantmentEffectsFromCannon(copy);
				} else {
					proj.setEnchantmentEffectsFromCannon(stack);
				}
				if (motion == null) {
					cons.shoot(proj, 0);
					motion = proj.getDeltaMovement();
				}
				Vec3 splitMotion = motion;
				if (spray) {
					float imperfection = 40.0F * (e.getRandom().nextFloat() - 0.5F);
					Vec3 sprayOffset = VecHelper.rotate(sprayBase, (float) i * sprayChange + imperfection, Direction.Axis.Z);
					splitMotion = splitMotion.add(VecHelper.lookAt(sprayOffset, motion));
				}

				proj.setPos(src.x, src.y, src.z);
				proj.setDeltaMovement(splitMotion);
				proj.setOwner(e);
				level.addFreshEntity(proj);
			}

			if (!user.bypassAllConsumption()) {
				ammo.shrink(1);
				if (ammo.isEmpty() && e instanceof Player player) {
					player.getInventory().removeItem(ammo);
				}
			}
			if (!user.bypassAllConsumption() && !BacktankUtil.canAbsorbDamage(e, AllConfigs.server().equipment.maxPotatoCannonShots.get())) {
				stack.hurtAndBreak(1, e, (p) -> p.broadcastBreakEvent(hand));
			}
			if (motion != null) {
				var finMotion = motion.normalize();
				sendPackets(e, (b) -> new PotatoCannonPacket(src, finMotion, ammo, hand, soundPitch, b));
			}
			return type.getReloadTicks();
		}

		private static Vec3 getGunBarrelVec(LivingEntity player, boolean mainHand, Vec3 rel) {
			Vec3 eye = player.position().add(0.0, player.getEyeHeight(), 0.0);
			float yaw = (float) ((double) (player.getYRot() / -180.0F) * Math.PI);
			float pitch = (float) ((double) (player.getXRot() / -180.0F) * Math.PI);
			int flip = mainHand == (player.getMainArm() == HumanoidArm.RIGHT) ? -1 : 1;
			Vec3 relative = new Vec3((double) flip * rel.x, rel.y, rel.z);
			return eye.add(relative.xRot(pitch).yRot(yaw));
		}

		private static void sendPackets(LivingEntity player, Function<Boolean, ? extends ShootGadgetPacket> factory) {
			if (player instanceof ServerPlayer) {
				AllPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> player), factory.apply(false));
			}
		}

	}

}
