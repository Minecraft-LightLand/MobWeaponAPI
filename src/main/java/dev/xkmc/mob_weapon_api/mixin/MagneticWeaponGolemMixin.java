package dev.xkmc.mob_weapon_api.mixin;

import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import dev.xkmc.mob_weapon_api.integration.alexscaves.GalenaGauntletBehavior;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 方铅护手的磁力武器：让非玩家 controller（傀儡）也能驱动环绕武器。
 * <p>
 * 原版 {@code MagneticWeaponEntity.tick()} 只有 {@code TeletorEntity} 和 {@code Player} 两个分支，
 * 傀儡是 {@code LivingEntity} 但两者都不是——既不会跟随、也不会攻击。
 * <p>
 * 这里补一个傀儡分支：
 * <ul>
 *   <li>护手不在傀儡手上（卸下 / 傀儡回收）→ 立即自毁，避免世界上留下孤儿实体；</li>
 *   <li>把 controller 的目标写回 {@code TARGET_ID}——原版 tick 开头靠它判断
 *       {@code strikeProgress} 是否该衰减，不写的话蓄力永远涨不上去；</li>
 *   <li>靠近目标时累加 {@code strikeProgress}，满了调 {@code hurtEntity} 结算伤害
 *       （原版只有那两个分支会调它，傀儡分支必须自己补）。</li>
 * </ul>
 */
@Pseudo
@Mixin(MagneticWeaponEntity.class)
public class MagneticWeaponGolemMixin {

	@Shadow(remap = false)
	@Final
	private static EntityDataAccessor<Integer> TARGET_ID;

	@Shadow(remap = false)
	@Final
	private static EntityDataAccessor<Integer> CONTROLLER_ID;

	@Shadow(remap = false)
	private float strikeProgress;

	@Shadow(remap = false)
	private int playerUseCooldown;

	@Shadow(remap = false)
	@Final
	private static EntityDataAccessor<Boolean> IDLING;

	@Shadow(remap = false)
	private void directMovementTowards(Vec3 moveTo, float speed) {
	}

	@Shadow(remap = false)
	private float returnProgress;

	@Shadow(remap = false)
	private boolean comingBack;

	/**
	 * "正在收回"的同步标志。
	 * <p>
	 * 渲染器的红蓝渐变读的是 {@code getReturnProgress()}，而 {@code returnProgress} 是普通字段、
	 * 不同步；客户端 {@code tick()} 里也没有任何地方推进它（原版只有 Player 分支会写，
	 * 而那个分支客户端也跑）。所以必须自己加一个同步标志，客户端据此推进本地进度。
	 */
	private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(
			MagneticWeaponEntity.class, EntityDataSerializers.BOOLEAN);

	@Inject(method = "defineSynchedData", at = @At("TAIL"), remap = true, require = 0)
	private void mobWeaponAPI$defineReturning(CallbackInfo ci) {
		((MagneticWeaponEntity) (Object) this).getEntityData().define(RETURNING, false);
	}

	private void setComingBack(boolean value) {
		this.comingBack = value;
	}


	@Shadow(remap = false)
	private void hurtEntity(LivingEntity holder, Entity target) {
	}

	/**
	 * 让召唤出的武器触发它自身的命中特效。
	 * <p>
	 * 原版 {@code hurtEntity} 里这段写死了 {@code if (holder instanceof Player player)}：
	 * <pre>
	 * itemStack.hurtEnemy(living, player);
	 * </pre>
	 * 傀儡不是 {@code Player}，于是荒芜匕首召唤飞刀、原始大棒眩晕之类的原生效果全部不触发，
	 * 只剩纯伤害。
	 * <p>
	 * {@code Item#hurtEnemy(ItemStack, LivingEntity, LivingEntity)} 的第三个参数是 {@code LivingEntity}
	 * 而不是 {@code Player}（{@code ItemStack#hurtEnemy} 那个只接受 {@code Player} 的重载才是玩家专用，
	 * 内部只是转发并 awardStat）。所以直接调 Item 版本即可，无需复刻特效。
	 */
	@Inject(method = "hurtEntity", at = @At("TAIL"), remap = false, require = 0)
	private void mobWeaponAPI$golemHitEffects(LivingEntity holder, Entity target, CallbackInfo ci) {
		if (holder instanceof Player) return;
		if (!(target instanceof LivingEntity living)) return;
		ItemStack stack = ((MagneticWeaponEntity) (Object) this).getItemStack();
		if (stack.isEmpty()) return;
		stack.getItem().hurtEnemy(stack, living, holder);
		// 耐久回写：原版 {@code MagneticWeaponEntity#damageItem} 扣的是 {@code getItemStack()}，
		// 而玩家分支里那把武器已经被 {@code otherStack.setCount(0)} 从背包清空、实体持有副本，
		// 所以"扣副本"就是扣真实耐久（收回时 {@code plopItem} 把副本放回背包）。
		// 傀儡分支刻意跳过了那次清零（武器留在手上），副本与真身从此分离 → 攻击只扣副本、
		// 手上真武器永不磨损。这里把这一击的耐久消耗补到傀儡手上的真实武器上，
		// 语义与玩家一致：每次命中消耗 1 点（原版 {@code hurtEntity} 成功结算后调 {@code damageItem(1)}）。
		ItemStack held = GalenaGauntletBehavior.findWeapon(holder, gauntletInHand(holder));
		if (!held.isEmpty() && held.isDamageableItem() && held.getItem() == stack.getItem()) {
			held.hurtAndBreak(1, holder, e -> {
			});
		}
	}

	@Inject(method = "tick", at = @At("TAIL"), require = 0)
	private void mobWeaponAPI$golemController(CallbackInfo ci) {
		MagneticWeaponEntity self = (MagneticWeaponEntity) (Object) this;
		// 客户端只负责推进渲染用的 returnProgress / comingBack，其余逻辑全在服务端。
		// 原版这两项在客户端也更新（Player 分支两侧都跑），漏掉的话特效永远不变色。
		if (self.level().isClientSide) {
			boolean returning = self.getEntityData().get(RETURNING);
			setComingBack(returning);
			float step = returning ? 0.2F : -0.2F;
			returnProgress = Mth.clamp(returnProgress + step, 0F, 1F);
			return;
		}
		Entity controller = self.getController();
		if (controller == null || controller instanceof Player) return;
		if (!(controller instanceof LivingEntity holder)) return;
		// 冷却必须每 tick 递减。原版这段在 tick() 的公共区域（第 261 行），
		// 不在 Player 分支里——漏掉它 playerUseCooldown 永远是 0 以上，
		// {@code retreat} 恒为 true，武器会一直停在待命点不扑击。
		if (playerUseCooldown > 0) {
			playerUseCooldown--;
		}
		// 客户端 getController() 读的是 CONTROLLER_ID，原版只在 Teletor / Player 分支里写，
		// 傀儡不写的话客户端拿不到 controller，特效渲染器会直接跳过这个实体。
		self.getEntityData().set(CONTROLLER_ID, holder.getId());

		// 护手还在不在傀儡手上？不在就自毁
		boolean holding = isGauntlet(holder.getUseItem())
				|| isGauntlet(holder.getItemInHand(InteractionHand.MAIN_HAND))
				|| isGauntlet(holder.getItemInHand(InteractionHand.OFF_HAND));
		if (!holding) {
			self.discard();
			return;
		}
		// 另一只手的磁力武器被拿走（换手 / 消耗）后，环绕武器不该继续存在并攻击
		ItemStack weapon = GalenaGauntletBehavior.findWeapon(holder, gauntletInHand(holder));
		if (weapon.isEmpty()) {
			self.discard();
			return;
		}
		// 换成另一把武器时，环绕的那把也要跟着换
		if (!ItemStack.matches(self.getItemStack(), weapon)) {
			self.setItemStack(weapon.copy());
		}

		// 目标必须以 controller（傀儡）当前的锁定目标为准。
		// 反过来先读 self.getTarget() 会踩坑：我们每 tick 把目标写进 TARGET_ID，
		// 于是 self.getTarget() 永远返回上一轮写进去的旧目标，傀儡换目标后武器还在打旧的。
		Entity target = holder instanceof Mob mob ? mob.getTarget() : null;
		if (target != null && !target.isAlive()) target = null;
		if (target != null) {
			self.getEntityData().set(TARGET_ID, target.getId());
		}

		// 没有目标 = 原版 Player 分支里"护手不在手上"的那条 else：收回武器。
		// 少了这条，傀儡脱战后召唤出去的武器会一直飘着不回来。
		if (target == null) {
			// comingBack 必须置位：原版 tick 开头用它决定 noPhysics，渲染器也靠它驱动
			// returnProgress 由蓝转红的渐变。只推 returnProgress 不够。
			setComingBack(true);
			self.getEntityData().set(RETURNING, true);
			if (returnProgress < 1F) {
				returnProgress = Math.min(1F, returnProgress + 0.2F);
			}
			Vec3 home = holder.position().add(0, 1, 0);
			directMovementTowards(home, 0.1F);
			self.hurtMarked = true;
			// 回到身边就直接消散。
			// 注意不要像原版那样把物品塞回背包：傀儡这条路径从未把武器从背包里拿走
			// （{@code GalenaGauntletGolemMixin} 跳过了原版的 {@code setCount(0)}），
			// 再塞回去等于凭空复制一份。
			if (self.distanceTo(holder) < 1.4F && !self.isRemoved()) {
				self.discard();
			}
			return;
		}

		// 攻击冷却期间飞回傀儡前方待命——原版 Player 分支正是这么做的，
		// 所以玩家用的时候武器会在目标和身边之间来回摆动，而不是钉死在目标上。
		// 待命点是"眼睛 + 视线方向 10 格"（maxDist - 20），不是飞回脚边：
		// 用脚边当待命点会让武器在目标和傀儡之间跑一大段，摆动幅度过大。
		// 有目标时解除回程状态，特效才会转回蓝色
		setComingBack(false);
		self.getEntityData().set(RETURNING, false);
		boolean retreat = playerUseCooldown > 0;
		Vec3 goal = retreat
				? holder.getEyePosition().add(holder.getViewVector(1.0F).scale(10.0F))
				: target.position().add(0, target.getBbHeight() * 0.5F, 0);
		Vec3 want = goal.subtract(self.position());
		if (want.length() > 1.0E-4) {
			// 用原版的 directMovementTowards 而不是自己设 deltaMovement：它同时负责朝向（旋转）。
			// 直接设速度的话武器只会平移，看起来像一块滑动的板子。
			self.getEntityData().set(IDLING, false);
			directMovementTowards(goal, retreat ? 0.1F : 0.2F);
			self.hurtMarked = true;
		}


		// 命中结算：原版只有 Teletor / Player 分支会调 hurtEntity。
		// 冷却期间（playerUseCooldown > 0）不能继续蓄力：原版这时武器在待命点、
		// tick 开头会按 {@code playerUseCooldown > 0} 把 strikeProgress 每 tick 衰减 0.1。
		// 少了这个门闩，武器飞回待命点的路上仍在蓄力，冷却一结束就立刻满蓄力扑上去，
		// 表现为"摆动幅度比玩家大、没有挥砍动作"。
		if (retreat) {
			strikeProgress = Math.max(0F, strikeProgress - 0.1F);
		} else if (target != null && self.distanceTo(target) < target.getBbWidth() + 1.5F) {
			if (strikeProgress < 1F) {
				strikeProgress = Math.min(1F, strikeProgress + 0.35F);
			} else {
				hurtEntity(holder, target);
				// 与原版一致：命中后<b>不清零</b> strikeProgress，交给上面的衰减分支
				// 在冷却期间自然降到 0。清零会让下一次扑击的蓄力"凭空开始"，
				// 渲染器的挥砍进度（getLerpedStrikeProgress）因此没有连续的起落。
				playerUseCooldown = 5 + self.level().getRandom().nextInt(5);
			}
		}
	}

	private static boolean isGauntlet(net.minecraft.world.item.ItemStack stack) {
		return stack.getItem() instanceof GalenaGauntletItem;
	}

	/** 找到傀儡手上那把护手（主手或副手）。 */
	private static ItemStack gauntletInHand(LivingEntity holder) {
		ItemStack main = holder.getItemInHand(InteractionHand.MAIN_HAND);
		if (isGauntlet(main)) return main;
		return holder.getItemInHand(InteractionHand.OFF_HAND);
	}

}
