package dev.xkmc.cataclysm_mux_0327;

import com.github.L_Ender.cataclysm.entity.effect.Sandstorm_Entity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SandstormOrbitEntity_0327 extends Sandstorm_Entity {

	/**
	 * 距离增益：离目标每远一格提速 GAIN_PER_BLOCK 倍，最快 MAX_SPEED_GAIN 倍，
	 * 倍数为 1（即原始固定步长）时是最低速度。
	 */
	private static final float GAIN_PER_BLOCK = 0.15F;
	private static final float MAX_SPEED_GAIN = 8.0F;
	/**
	 * 归位插值比例，以及距离环位足够近时直接吸附，避免插值永远差一点点。
	 */
	private static final float RETURN_LERP = 0.25F;
	private static final double RETURN_SNAP_SQR = 0.01D;

	/**
	 * 归位后的半径档位，0 为最内侧。追踪目标期间保持在离开时的档位，回到施法者身边后按它归位。
	 */
	private int slot;
	private float baseRadius;
	private float speed;
	@Nullable
	private LivingEntity target;
	private float targetId = -1;

	public SandstormOrbitEntity_0327(Level worldIn, double x, double y, double z, int lifespan, float offset,
			LivingEntity casterIn, float radius, float speed) {
		super(worldIn, x, y, z, lifespan, offset, casterIn);
		this.baseRadius = radius;
		this.speed = speed;
	}

	public int getSlot() {
		return slot;
	}

	public void setBaseRadius(float baseRadius) {
		this.baseRadius = baseRadius;
	}

	public float getBaseRadius() {
		return baseRadius;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	@Nullable
	public LivingEntity getTarget() {
		if (target != null && target.isAlive() && target.isRemoved() == false) return target;
		return target = resolveTarget();
	}

	public void setTarget(@Nullable LivingEntity target) {
		this.target = target;
		this.targetId = target == null ? -1 : target.getId();
	}

	@Nullable
	private LivingEntity resolveTarget() {
		if (targetId < 0) return null;
		Entity found = level().getEntity((int) targetId);
		return found instanceof LivingEntity le ? le : null;
	}

	@Override
	protected void updateMotion() {
		LivingEntity owner = this.getCaster();
		if (owner == null) return;
		LivingEntity chasing = getTarget();
		if (chasing != null) {
			seek(chasing);
			return;
		}
		orbit(owner);
	}

	/**
	 * 朝目标直线移动，越远越快，最慢就是原本的固定步长。到达目标身边后仍会继续贴着目标移动，
	 * 交给上层在目标失效时收回。
	 */
	private void seek(LivingEntity chasing) {
		Vec3 to = chasing.position().subtract(this.position());
		double dist = to.length();
		if (dist < 1.0E-4D) return;
		float base = this.speed * 4.0F;
		float gain = Math.min(MAX_SPEED_GAIN, 1.0F + (float) dist * GAIN_PER_BLOCK);
		float step = (float) Math.min(dist, base * gain);
		Vec3 next = this.position().add(to.normalize().scale(step));
		this.setPos(next.x, chasing.getY(), next.z);
	}

	/**
	 * 三层同心环，每环三个，沙暴在环上的相位由构造时传入的 offset 决定。
	 * 半径按环号递增，和原本近战模式的 4/8/12 一致；slot 是展平的下标，环号 = slot / 3。
	 * 归位用插值而非瞬移，追击途中被收回时不会出现突兀的落点。
	 */
	private void orbit(LivingEntity owner) {
		Vec3 center = owner.position();
		int ring = slot / 3;
		float radius = baseRadius + ring * 4.0F;
		float angle = this.tickCount * this.speed + this.getOffset();
		Vec3 spot = new Vec3(center.x + Math.cos(angle) * radius, center.y, center.z + Math.sin(angle) * radius);
		Vec3 cur = this.position();
		this.setPos(cur.distanceToSqr(spot) < RETURN_SNAP_SQR ? spot : cur.lerp(spot, RETURN_LERP));
	}

}