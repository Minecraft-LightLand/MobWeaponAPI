package dev.xkmc.mob_weapon_api.integration.create;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllPackets;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonPacket;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.equipment.zapper.ShootGadgetPacket;
import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Function;

public class CreateProxy {

	public static int shootPotato(BowUseContext user, ItemStack stack, InteractionHand hand) {
		return Helper.shootPotato(user, stack, hand);
	}

	private static class Helper {

		/**
		 * Create's own fallback, which is registered as {@code .damage(0).build()} with every other field left
		 * at the builder default. Reading it out of the registry instead would need catnip's config classes,
		 * which only exist inside Create's own runtime.
		 */
		private static final PotatoCannonProjectileType DEFAULT_TYPE = new PotatoCannonProjectileType(
				HolderSet.direct(), 10, 0, 1, 1.0F, 0.99F, 1.0F, 1.0F, 1.0F, false, ItemStack.EMPTY,
				AllPotatoProjectileRenderModes.Billboard.INSTANCE, Optional.empty(), Optional.empty(),
				Optional.empty());

		private static int shootPotato(BowUseContext user, ItemStack stack, InteractionHand hand) {
			if (!(stack.getItem() instanceof PotatoCannonItem)) return 20;
			var e = user.user();
			var level = e.level();
			ItemStack ammo = user.getPreferredProjectile(stack);
			PotatoCannonProjectileType type = PotatoCannonProjectileType
					.getTypeForItem(level.registryAccess(), ammo.getItem())
					.map(holder -> holder.value())
					.orElse(DEFAULT_TYPE);

			// mirrors Create's own aim: gun barrel minus eye, added to the look vector
			Vec3 barrel = getGunBarrelVec(e, hand == InteractionHand.MAIN_HAND, new Vec3(0.75, -0.15, 1.5));
			Vec3 correction = getGunBarrelVec(e, hand == InteractionHand.MAIN_HAND, new Vec3(-0.05, 0, 0))
					.subtract(e.position().add(0, e.getEyeHeight(), 0));
			Vec3 look = e.getLookAngle();
			Vec3 motion = look.add(correction).normalize().scale(2).scale(type.velocityMultiplier());
			float soundPitch = type.soundPitch() + (e.getRandom().nextFloat() - 0.5F) / 4.0F;

			boolean spray = type.split() > 1;
			Vec3 sprayBase = rotateZ(new Vec3(0, 0.1, 0), 360.0F * e.getRandom().nextFloat());
			float sprayChange = 360.0F / type.split();

			// Create strips the recovery enchantment from the copy it hands to extra projectiles
			ItemStack copy = stack.copy();
			if (type.split() > 1) {
				var enchants = EnchantmentHelper.getEnchantments(copy);
				if (enchants.remove(AllEnchantments.POTATO_RECOVERY.get()) != null) {
					EnchantmentHelper.setEnchantments(enchants, copy);
				}
			}

			boolean infinite = user.bypassAllConsumption();
			ItemStack projectileItem = ammo.copy();
			for (int i = 0; i < type.split(); i++) {
				PotatoProjectileEntity proj = AllEntityTypes.POTATO_PROJECTILE.create(level);
				if (proj == null) continue;
				proj.setItem(projectileItem);
				proj.setEnchantmentEffectsFromCannon(infinite || i != 0 ? copy : stack);
				Vec3 splitMotion = motion;
				if (spray) {
					float imperfection = 40.0F * (e.getRandom().nextFloat() - 0.5F);
					Vec3 sprayOffset = rotateZ(sprayBase, i * sprayChange + imperfection);
					splitMotion = splitMotion.add(lookAt(sprayOffset, motion));
				}
				proj.setPos(barrel.x, barrel.y, barrel.z);
				proj.setDeltaMovement(splitMotion);
				proj.setOwner(e);
				level.addFreshEntity(proj);
			}

			if (!infinite) {
				ammo.shrink(1);
				if (ammo.isEmpty() && e instanceof Player player) {
					player.getInventory().removeItem(ammo);
				}
			}
			// The tank absorption check needs catnip's config classes, which only Create's own runtime has, so the
			// cannon simply wears down
			stack.hurtAndBreak(1, e, (p) -> p.broadcastBreakEvent(hand));
			sendPackets(e, (b) -> new PotatoCannonPacket(barrel, look.normalize(), ammo, hand, soundPitch, b));
			return type.reloadTicks();
		}

		private static Vec3 getGunBarrelVec(LivingEntity player, boolean mainHand, Vec3 rel) {
			Vec3 eye = player.position().add(0.0, player.getEyeHeight(), 0.0);
			float yaw = (float) ((double) (player.getYRot() / -180.0F) * Math.PI);
			float pitch = (float) ((double) (player.getXRot() / -180.0F) * Math.PI);
			int flip = mainHand == (player.getMainArm() == HumanoidArm.RIGHT) ? -1 : 1;
			Vec3 relative = new Vec3(flip * rel.x, rel.y, rel.z);
			return eye.add(relative.xRot(pitch).yRot(yaw));
		}

		/**
		 * Rotates around the Z axis, matching catnip's VecHelper.rotate(vec, deg, Axis.Z). Create reaches these
		 * through catnip, which ships inside Ponder and is not on our compile classpath, so it is inlined here.
		 */
		private static Vec3 rotateZ(Vec3 vec, float degrees) {
			if (degrees == 0 || vec == Vec3.ZERO) return vec;
			float angle = (float) (degrees / 180f * Math.PI);
			double sin = Mth.sin(angle);
			double cos = Mth.cos(angle);
			double x = vec.x;
			double y = vec.y;
			return new Vec3(x * cos - y * sin, y * cos + x * sin, vec.z);
		}

		/** Catnip's {@code lookAt}: rebases {@code vec} onto the frame aligned with {@code fwd}. */
		private static Vec3 lookAt(Vec3 vec, Vec3 fwd) {
			fwd = fwd.normalize();
			Vec3 up = new Vec3(0, 1, 0);
			double dot = fwd.dot(up);
			if (Math.abs(dot) > 1 - 1.0E-3) {
				up = new Vec3(0, 0, dot > 0 ? 1 : -1);
			}
			Vec3 right = fwd.cross(up).normalize();
			up = right.cross(fwd).normalize();
			double x = vec.x * right.x + vec.y * up.x + vec.z * fwd.x;
			double y = vec.x * right.y + vec.y * up.y + vec.z * fwd.y;
			double z = vec.x * right.z + vec.y * up.z + vec.z * fwd.z;
			return new Vec3(x, y, z);
		}

		/**
		 * The launch sound and the muzzle particles both ride on the gadget packet, so this has to reach every
		 * client tracking the shooter. Create's own helper only handles a ServerPlayer firing itself; a mob
		 * firing this needs the tracking distributor instead, which accepts any entity.
		 */
		private static void sendPackets(LivingEntity player, Function<Boolean, ? extends ShootGadgetPacket> factory) {
			AllPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> player), factory.apply(false));
		}

	}

}