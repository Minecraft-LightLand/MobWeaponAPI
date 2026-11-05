package dev.xkmc.mob_weapon_api.example.goal;

import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.api.goals.IRangedWeaponGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Optional;

public abstract class SmartRangedAttackGoal<E extends Mob> extends Goal implements IRangedWeaponGoal<E> {

	protected final E mob;
	protected final IWeaponHolder holder;
	protected final IMeleeGoal melee;
	protected final double speedModifier;
	protected final double radius;
	protected int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	private int meleeTime = 0;

	protected SmartRangedAttackGoal(E mob, IWeaponHolder holder, IMeleeGoal melee, double speedModifier, double r) {
		this.mob = mob;
		this.holder = holder;
		this.melee = melee;
		this.speedModifier = speedModifier;
		this.radius = r;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public boolean canUse() {
		if (mob.getTarget() == null || !mob.getTarget().isAlive()) return false;
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		// mayActivate 含冷却；只用它的话，开完一炮（长冷却的武器如糖果法杖 100 tick）goal 会直接停止，
		// 于是 strafing 不再跑——傀儡站着不动、视线也不转，看起来像"呆住几秒"。
		// 只要还有弹药就让 goal 保持运行，冷却由 tick 里的 attackTime 自己控制。
		// 必须确认这把武器<b>确实是远程</b>。
		// 只看 mayActivate / isAvailable 不够：注册表允许"近战武器"登记在 INSTANT / HOLD 里
		// （AC 的 melee() 就是这么干的，状态是 MELEE），于是主手换成剑之后远程 goal 依然激活，
		// 一直尝试"使用"那把剑——表现为切到副手后还在原地空放。
		if (!getWeaponStatus(stack).map(WeaponStatus::isRanged).orElse(false)) return false;
		return mayActivate(stack) || isAvailable(stack);
	}

	public boolean canContinueToUse() {
		// 武器已经不再是远程（例如切手后主手变成剑）时立刻停，
		// 不能靠 {@code !navigation.isDone()} 续命——那会让 goal 在"空放"状态下继续存活。
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		if (!getWeaponStatus(stack).map(WeaponStatus::isRanged).orElse(false)) return false;
		return canUse() || !mob.getNavigation().isDone();
	}

	public Optional<WeaponStatus> getWeaponStatus(ItemStack stack) {
		return Optional.empty();
	}

	public void start() {
		super.start();
		mob.setAggressive(true);
		var status = getWeaponStatus(mob.getItemInHand(holder.getWeaponHand()));
		holder.setInRangeAttack(status.isPresent() && status.get().isRanged());
	}

	public void stop() {
		super.stop();
		mob.setAggressive(false);
		holder.setInRangeAttack(false);
		// 这里<b>不能</b>留 {@code seeTime = 5}。
		// 那是"刚看到目标"的状态，但 stop() 之后 goal 随时可能被重新 addGoal（
		// {@code AbstractWeaponManager#reassessWeaponGoal} 每 100 tick 会重加一次），
		// 无目标时重新 start 后 seeTime=5 依然成立，{@code tick()} 里
		// {@code seeTime >= -60} 的 startUsingItem 分支就会跑起来 → 举枪 →
		// 下一 tick 因 {@code !withInRange} 又 stopUsingItem → 反复，表现为高频抖动。
		// 归零后无目标时 seeTime 会一路减到 -60 以下，彻底不再尝试使用。
		seeTime = 0;
		meleeTime = 0;
		mob.stopUsingItem();
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	protected void doMelee() {
		if (meleeTime > 0) {
			meleeTime--;
			return;
		}
		var target = mob.getTarget();
		if (target == null) return;
		if (melee.canReachTarget(target)) {
			onMelee();
			this.mob.doHurtTarget(target);
			meleeTime = melee.getMeleeInterval();
		}
	}

	protected void onMelee() {
		this.mob.swing(InteractionHand.MAIN_HAND);
	}

	protected boolean meleeFinished() {
		return meleeTime <= 0;
	}

	@Override
	public double range(ItemStack stack) {
		return radius;
	}

	public final double attackRadiusSqr() {
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		double range = range(stack);
		var ins = mob.getAttribute(Attributes.FOLLOW_RANGE);
		if (ins != null) {
			range = Math.min(ins.getValue(), range);
		}
		return range * range;
	}

	/**
	 * 是否要保持距离。返回 false 的武器（钩锁）不做侧移/后退，只转向并靠近。
	 * 默认 true，由持有 behavior 的子类覆写。
	 */
	protected boolean keepDistance(ItemStack stack) {
		return true;
	}

	protected void strafing() {
		var target = mob.getTarget();
		if (target == null) return;
		double dist = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
		boolean sight = mob.getSensing().hasLineOfSight(target);
		boolean oldSight = seeTime > 0;
		if (sight != oldSight) {
			seeTime = 0;
		}
		if (sight) {
			++seeTime;
		} else {
			--seeTime;
		}
		double sqr = attackRadiusSqr();
		if (!keepDistance(mob.getItemInHand(holder.getWeaponHand()))) {
			// 钩锁这类「必须贴近才有意义」的武器：不侧移、不后退，但<b>必须一直靠近</b>。
			// 不能走下面的"在射程内就 stop()"分支：钩锁射程 20 格，傀儡会在 20 格外站定，
			// 表现就是"用了钩锁就不动了"。
			mob.getNavigation().moveTo(target, speedModifier);
			strafingTime = -1;
			mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
			return;
		}
		if (dist <= sqr && seeTime >= 5) {
			mob.getNavigation().stop();
			++strafingTime;
		} else {
			mob.getNavigation().moveTo(target, speedModifier);
			strafingTime = -1;
		}
		if (sqr < 15) {
			strafingTime = 0;
			mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
			return;
		}
		if (strafingTime >= 20) {
			if ((double) mob.getRandom().nextFloat() < 0.3D) {
				strafingClockwise = !strafingClockwise;
			}
			if ((double) mob.getRandom().nextFloat() < 0.3D) {
				strafingBackwards = !strafingBackwards;
			}
			strafingTime = 0;
		}
		if (strafingTime > -1) {
			if (dist > sqr * 0.75) {
				strafingBackwards = false;
			} else if (dist < sqr * 0.5) {
				strafingBackwards = true;
			}
			mob.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
			mob.lookAt(target, 30.0F, 30.0F);
		} else {
			mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
		}
	}

}
