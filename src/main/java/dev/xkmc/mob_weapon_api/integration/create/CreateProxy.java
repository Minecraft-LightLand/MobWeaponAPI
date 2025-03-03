package dev.xkmc.mob_weapon_api.integration.create;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonPacket;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.equipment.zapper.ShootGadgetPacket;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.Vec3;
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
			var typeOpt = PotatoCannonProjectileType.getTypeForItem(level.registryAccess(), ammo.getItem());
			if (typeOpt.isEmpty()) return 20;
			var type = typeOpt.get().value();
			float soundPitch = type.soundPitch() + (e.getRandom().nextFloat() - 0.5F) / 4.0F;
			boolean spray = type.split() > 1;
			Vec3 sprayBase = VecHelper.rotate(new Vec3(0.0, 0.1, 0.0), 360.0F * e.getRandom().nextFloat(), Direction.Axis.Z);
			float sprayChange = 360.0F / (float) type.split();

			float gravity = 0.05f * type.gravityMultiplier();
			float speed = 2f * type.velocityMultiplier();
			var cons = user.aim(src, speed, gravity, user.getInitialInaccuracy());
			Vec3 motion = null;


			ItemStack copy = stack.copy();
			if (type.split() > 1) {
				if (EnchHelper.getLv(copy, AllEnchantments.POTATO_RECOVERY) > 0) {
					var old = new ItemEnchantments.Mutable(copy.getEnchantments());
					old.removeIf(x -> x.is(AllEnchantments.POTATO_RECOVERY));
					EnchantmentHelper.setEnchantments(copy, old.toImmutable());
				}
			}
			for (int i = 0; i < type.split(); ++i) {
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
				stack.hurtAndBreak(1, e, LivingEntity.getSlotForHand(hand));
			}
			if (motion != null) {
				var finMotion = motion.normalize();
				sendPackets(e, (b) -> new PotatoCannonPacket(src, finMotion, ammo, hand, soundPitch, b));
			}
			return type.reloadTicks();
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
				CatnipServices.NETWORK.sendToClientsTrackingEntity(player, factory.apply(false));
			}
		}

	}

}
