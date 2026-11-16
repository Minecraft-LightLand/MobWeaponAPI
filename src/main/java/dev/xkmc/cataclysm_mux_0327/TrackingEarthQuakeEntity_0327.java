package dev.xkmc.cataclysm_mux_0327;

import com.github.L_Ender.cataclysm.entity.projectile.EarthQuake_Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * An earthquake wave that keeps steering toward a chosen target while it travels.
 * <p>
 * The base wave only knows a straight delta movement, so a skill that fires many of them has no way to
 * give each wave its own quarry. Steering happens by rewriting the delta movement every tick, which
 * keeps the base class' collision, damage and lifetime behaviour untouched.
 */
public class TrackingEarthQuakeEntity_0327 extends EarthQuake_Entity {

	private static final double SPEED = 0.55D;
	/**
	 * How much of the current heading is kept each tick. High values turn slowly, which keeps the
	 * wave reading as a ground shock rather than a homing missile.
	 */
	private static final double STEER = 0.6D;

	/** Height above the ground surface the wave travels at, matching how it is spawned. */
	private static final double RIDE_HEIGHT = 0.4D;

	private int targetId = -1;

	public TrackingEarthQuakeEntity_0327(Level level, LivingEntity owner, Vec3 dir) {
		super(level, owner);
		this.setDeltaMovement(dir.x * SPEED, 0.0D, dir.z * SPEED);
	}

	public void setTrackedTarget(@Nullable LivingEntity target) {
		this.targetId = target == null ? -1 : target.getId();
	}

	/**
	 * The quarry this wave is homing on, or null when there is none.
	 * <p>
	 * Only grounded targets count: the wave is a ground shock, so an airborne target is not worth
	 * chasing. Returning null simply skips the steering for that tick and the wave keeps travelling
	 * along its current heading, so a jump makes it break off instead of circling underneath.
	 */
	@Nullable
	private LivingEntity resolveTarget() {
		if (targetId < 0) return null;
		Entity found = level().getEntity(targetId);
		if (!(found instanceof LivingEntity le)) return null;
		if (!le.isAlive() || le.isRemoved() || !le.onGround()) return null;
		return le;
	}

	@Override
	public void tick() {
		LivingEntity target = resolveTarget();
		if (target != null) {
			Vec3 cur = this.getDeltaMovement();
			Vec3 want = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
			if (want.lengthSqr() > 1.0E-6D) {
				Vec3 heading = new Vec3(cur.x, 0.0D, cur.z);
				if (heading.lengthSqr() < 1.0E-6D) heading = want.normalize();
				Vec3 next = heading.normalize().scale(1.0D - STEER)
						.add(want.normalize().scale(STEER))
						.normalize()
						.scale(SPEED);
				this.setDeltaMovement(next.x, cur.y, next.z);
			}
		}
		super.tick();
		// Stop homing once the wave lands its hit on the quarry. Uses the same condition the base wave
		// uses before dealing damage (every fifth tick, target grounded, hitbox overlap) rather than a
		// health drop: a target with enough resistance takes no health damage but is still struck, and
		// it must not keep the wave chasing forever.
		if (target != null && connected(target)) {
			this.targetId = -1;
		}
		snapToGround();
	}

	/** Mirrors {@code EarthQuake_Entity}'s pre-damage gate for a single target. */
	private boolean connected(LivingEntity target) {
		if (this.tickCount % 5 != 0) return false;
		if (!target.isAlive() || !target.onGround()) return false;
		return this.getBoundingBox().inflate(0.5D).intersects(target.getBoundingBox());
	}

	/**
	 * Keeps the wave glued to the floor. The base {@code EarthQuake_Entity} has {@code maxUpStep} 2.0,
	 * so it climbs whole blocks and can be lifted off the ground when its quarry is airborne. Every
	 * tick any vertical speed is cleared and, if the wave has risen above the floor beneath it, it is
	 * pulled back down.
	 */
	private void snapToGround() {
		Vec3 dm = this.getDeltaMovement();
		if (dm.y != 0.0D) this.setDeltaMovement(dm.x, 0.0D, dm.z);
		double groundY = groundYBelow();
		if (Double.isNaN(groundY)) return;
		if (this.getY() > groundY + RIDE_HEIGHT + 0.2D) {
			this.setPos(this.getX(), groundY + RIDE_HEIGHT, this.getZ());
		}
	}

	/**
	 * Top surface of the first standable block under the wave, or {@code NaN} when there is none.
	 */
	private double groundYBelow() {
		var pos = new BlockPos.MutableBlockPos(
				Mth.floor(this.getX()), Mth.floor(this.getY() - 0.05D), Mth.floor(this.getZ()));
		for (int i = 0; i < 8; i++) {
			BlockPos below = pos.below();
			if (this.level().getBlockState(below).isFaceSturdy(this.level(), below, Direction.UP)) {
				double dy = 0.0D;
				if (!this.level().isEmptyBlock(pos)) {
					var shape = this.level().getBlockState(pos).getCollisionShape(this.level(), pos);
					if (!shape.isEmpty()) dy = shape.max(Direction.Axis.Y);
				}
				return pos.getY() + dy;
			}
			pos.move(Direction.DOWN);
			if (pos.getY() <= this.level().getMinBuildHeight()) break;
		}
		return Double.NaN;
	}

}
