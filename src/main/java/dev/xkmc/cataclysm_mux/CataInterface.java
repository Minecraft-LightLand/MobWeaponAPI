package dev.xkmc.cataclysm_mux;

import java.util.function.Predicate;
import dev.xkmc.cataclysm_mux_0316.CataInterfaceImpl_0316;
import dev.xkmc.cataclysm_mux_0327.CataInterfaceImpl_0327;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

import java.util.List;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.Nullable;

public interface CataInterface {

	static CataInterface get() {
		try {
			ModFileInfo cataclysm = LoadingModList.get().getModFileById("cataclysm");
			if (cataclysm == null) return null;
			return cataclysm.getMods().stream().anyMatch(modInfo -> modInfo.getModId().equals("cataclysm")
					&& modInfo.getVersion().compareTo(new DefaultArtifactVersion("3.16")) > 0)
					? new CataInterfaceImpl_0327() : new CataInterfaceImpl_0316();
		} catch (Throwable e) {
			return null;
		}
	}

	record ProjectileData(Projectile proj, float speed, float gravity, int cooldown) {

	}

	// 傀儡
	void sandstormAttack(LivingEntity golem, LivingEntity target, int life);

	boolean isLaser(DamageSource source);

	boolean isMissile(DamageSource source);

	boolean isWitherMissile(DamageSource source);

	boolean isHowitzer(DamageSource source);

	boolean isSandstorm(DamageSource source);

	boolean isIgnisExplosive(Entity entity);

	boolean isSoul(Entity entity);

	boolean isAbyssFireball(Entity entity);

	boolean isIgnisStrike(Entity entity);

	int getSandCurseLevel(LivingEntity e);

	@Nullable
	Entity addLaserBeam(LivingEntity user, int dur);

	void addMissile(LivingEntity user, LivingEntity target, Vec3 pos);

	void addRune(LivingEntity user, LivingEntity target);

	void spawnBlastPortal(LivingEntity user, double x, double y, double z, float rotation, int delay);

	void golemStackBlazingBrandRaw(LivingEntity golem, LivingEntity target, float dmg, int min);

	void shootFireball(LivingEntity user, Vec3 shotAt, int timer, boolean abyss, boolean isBlue);

	void createBlast(LivingEntity user, Vec3 pos, int dur, int delay, float radius, float dmg, boolean soul);

	float monstrosityEarthquakeDamage();

	float maledictusEarthquakeDamage();

	void updateLaser(LivingEntity golem, Entity e);

	@Nullable
	LivingEntity getOwner(Entity entity);

	// 武器
	void stackBlazingBrand(LivingEntity user, LivingEntity target, float factor);

	void inflictStun(LivingEntity user, LivingEntity target, int time);

	int spawnHalberd(Vec3 pos, LivingEntity player, int delay);

	void spawnHalberdCircle(LivingEntity user, Vec3 pos, int count, float radius, int delay);

	void spawnWindmillHalberd(LivingEntity user, Vec3 pos, int branches, int perBranch, float initRadius, float radiusIncr, float curve, int delay);
	/**
	 * 向前方扇形区域召唤一圈幻影战戟，复刻灾变 Maledictus_Entity#radagonskill。
	 * yaw 为施法者朝向（度），distance 为该圈半径，spreadArc 为扇形张角（单位 π，本体用 0.7 = 126°）。
	 * 弧上按一个方块一点均匀铺开，并吸附到方块中心。
	 */
	void spawnHalberdFan(LivingEntity user, Vec3 pos, float yaw, int distance, float spreadArc, int delay);

	// 生物武器兼容
	@Nullable
	AbstractArrow createGhostArrow(Level level, LivingEntity player, LivingEntity target);

	@Nullable
	Entity createGhostStorm(LivingEntity user, Vec3 pos, Vec3 rot, LivingEntity target);

	void shootLaserGatling(LivingEntity user, Vec3 vec3);

	@Nullable
	ProjectileData shootVoid(LivingEntity player);

	int shootMissile(LivingEntity player, Vec3 dir);

	void launchTornado(LivingEntity user, Vec3 dir);

	int spawnVortex(LivingEntity user, Vec3 pos);

	int spawnVoidFangs(LivingEntity user, Vec3 dir);

	int infernalForge(LivingEntity user, LivingEntity target);

	int ceraunus(Level level, LivingEntity user, LivingEntity target);

	int astrape(Level level, LivingEntity user, LivingEntity target);

	int astrape(Level level, LivingEntity user, LivingEntity target, float lateral);

	@Nullable
	Projectile brontes(LivingEntity user, ItemStack stack, LivingEntity target);

	@Nullable
	Projectile coralSpear(LivingEntity user, Level level, ItemStack stack);

	// 远古遗魂 - 技能

	void spawnDesertStele(LivingEntity user, double x, double y, double z, int delay);

	void spawnEarthQuake(LivingEntity user);

	/**
	 * An earthquake wave that starts moving along {@code dir} and keeps steering toward {@code target}
	 * while it travels. Passing a null target makes it behave exactly like {@link #spawnEarthQuake}, so
	 * a caller that only has a few targets can still fire the rest of the ring as plain waves.
	 */
	void spawnEarthQuakeTracking(LivingEntity user, Vec3 dir, @Nullable LivingEntity target);

	boolean isEarthQuake(DamageSource source);

	void remnantRoarParticle(LivingEntity golem, float vec, float math, float y);

	void remnantChargeSound(LivingEntity user);

	void remnantChargeStep(LivingEntity user);

	void remnantTailSlamSound(LivingEntity user);

	// 魔训兽 - 技能

	void spawnFlameJet(LivingEntity user, double x, double y, double z, int delay);

	// 利维坦 - 技能

	@Nullable
	Entity addAbyssBlast(LivingEntity user, int dur);

	void spawnAbyssOrb(LivingEntity user, Vec3 dir, @Nullable LivingEntity target);

	void spawnAbyssMine(LivingEntity user, double x, double y, double z, int delay);

	void spawnDimensionalRift(LivingEntity user, double x, double y, double z);

	// 斯库拉 - 技能

	void lightningStorm(LivingEntity user, double x, double y, double z, float size);

	void summonStormSerpent(LivingEntity user, LivingEntity target);

	/**
	 * One phantom water serpent, spawned at the ground below the user and aimed at the target, with a
	 * lateral offset perpendicular to the user's facing (the way Scylla fans its serpents out in pairs).
	 * {@code lateral} is in blocks; {@code warmup} delays the strike.
	 */
	void summonStormSerpent(LivingEntity user, LivingEntity target, float lateral, int warmup);

	// 先驱者 - 技能

	void shootHowitzer(LivingEntity user, Vec3 dir);

	// 破块 - 通用（先驱者/魔训兽/远古遗魂冲锋破块）

	void fallingBlocks(LivingEntity user, double inflate);

	// 末影守卫 - 技能

	void shootEnderBullet(LivingEntity user, LivingEntity target);

	void gravityPull(LivingEntity user);

	/**
	 * Same pull, with the reach and strength scaled for a golem that has been upgraded.
	 * {@code radius} is in blocks, {@code strength} is the per-tick velocity added.
	 */
	void gravityPull(LivingEntity user, double radius, double strength);

	void massDestruction(LivingEntity user);

	void burst(LivingEntity user);

	void rocketPunch(LivingEntity user, LivingEntity target);

	void hugMe(LivingEntity user, LivingEntity target);

	// 魔训兽 - 技能

	void throwLavaBomb(LivingEntity user, LivingEntity target);

	void throwFlareBomb(LivingEntity user, LivingEntity target);

	void shoulderCheck(LivingEntity user, LivingEntity target);

	// 斯库拉 - 技能

	void shootSpark(LivingEntity user, float areaRadius);

	void spawnLightningArea(LivingEntity user, double x, double y, double z);

	void scyllaSummonWave(LivingEntity user, LivingEntity target);

	/**
	 * 同 {@link #scyllaSummonWave(LivingEntity, LivingEntity)}，但以施法瞬间锁定的位置与朝向为中心，
	 * 之后傀儡移动/转身都不会把海浪带走。
	 */
	void scyllaSummonWave(LivingEntity user, Vec3 center, float yaw);

	void scyllaSummonWave2(LivingEntity user, LivingEntity target);

	/**
	 * 同 {@link #scyllaSummonWave2(LivingEntity, LivingEntity)}，但以施法瞬间锁定的位置与朝向为中心。
	 */
	void scyllaSummonWave2(LivingEntity user, Vec3 center, float yaw);

	void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning);

	void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning, float lateral);

	void scyllaFallingBurst(LivingEntity user, int sparkAmount);

	void scyllaFallingBurstAt(LivingEntity user, double x, double y, double z, int sparkAmount);

	void scyllaLightningExplosion(LivingEntity user, double x, double y, double z);

	void scyllaBurstLightning(LivingEntity user);

	void scyllaWhip(LivingEntity user, LivingEntity target);

	void scyllaSpin(LivingEntity user);

	void guardianStompFangs(LivingEntity user, LivingEntity target);

	/**
	 * Only the ring of runes around the golem, without the rows toward a target.
	 */
	void guardianStompFangsRing(LivingEntity user);

	/**
	 * A single row of runes from the golem toward {@code target}, rotated by {@code angleOffset}
	 * radians. Skills that want several rows call this once per row so each row can pick its own
	 * direction; nothing is delayed, so all rows appear in the same tick.
	 */
	void guardianStompFangsRow(LivingEntity user, LivingEntity target, float angleOffset);

	void guardianUppercut(LivingEntity user, LivingEntity target);

	void guardianHugFX(LivingEntity user);

	void guardianGravityPunch(LivingEntity user, LivingEntity target);

	void guardianSlamLanding(LivingEntity user);

	void guardianExplosionSound(LivingEntity user);

	void scyllaSpinSound(LivingEntity user);

	void scyllaStormParticles(LivingEntity user);

	/** 一字闪电起手：傀儡脚下的青色冲击环（本体 attackState 18 tick25/53）+ 闪电爆裂粒子。 */
	void scyllaLightningSelfRing(LivingEntity user);


	void scyllaThunderCloud(LivingEntity user, LivingEntity target);

	void scyllaThunderCloudStorm(LivingEntity user, double x, double y, double z);

	/** 风暴之怒：雷云位置撒圈状闪电粒子（本体 CircleLighning，attackState 9 tick56-114 每 tick）。 */
	void scyllaThunderCloudLightning(LivingEntity user, double x, double y, double z);

	void scyllaThunderCloudSpear(LivingEntity user, LivingEntity target, double x, double y, double z);

	// 焰魔 - 震地

	void ignisEarthquakeFX(LivingEntity user, float shakeRadius);

	void ignisAreaPunch(LivingEntity user, float radius);

	/**
	 * 同 {@link #ignisAreaPunch}，但可指定扇形弧度（度）。360 即整圆。数值照本体 AreaAttack。
	 */
	void ignisAreaPunch(LivingEntity user, float radius, float arcDegrees);
	/**
	 * 焰魔反击（本体 STRIKE）：向前方扇形释放冲击波——扇形 AreaAttack
	 * (半径 5.25 / 120° / 1.25×攻击 + 0.1×最大生命) + 逐圈外扩的扇形震地波
	 * (ShieldSmashDamage(0.75, j, ...) 的 135° 扇形，grab=true)。
	 */
	void ignisCounterStrike(LivingEntity user, boolean blue);

	/**
	 * 烈焰反击的震地波：本体 ShieldSmashDamage(0.75, distance, ...) + distance+1，并播放该距离的震地音效。
	 */
	void ignisCounterWave(LivingEntity user, Vec3 center, float yaw, int distance, boolean blue);

	/**
	 * 同 {@link #ignisCounterWave(LivingEntity, Vec3, float, int, boolean)}，但可以指定击退方向。
	 * outward 为 true 时向远离傀儡的方向击退（远程模式用），false 时保持本体行为（把目标吸向波形内侧）。
	 */
	void ignisCounterWave(LivingEntity user, Vec3 center, float yaw, int distance, boolean blue, boolean outward);

	/**
	 * 焰魔咆哮（本体 BREAK_THE_SHIELD tick 55）：FLAME_BURST 音效 + 屏幕震动
	 * （半径 30 / 0.15 / 0 / 50）+ 一团咆哮粒子（魂形态青色，否则橙黄）。
	 * <p>
	 * 只做视听，范围眩晕由调用方自行施加（本体 tick 55 对 12 格内目标眩晕 60 tick）。
	 */
	void ignisRoarPulse(LivingEntity user, boolean blue);


	/**
	 * 焰魔砸地震起方块：照本体 ShieldSmashDamage 的落点环，沿弧线把地面方块震飞成
	 * {@code Cm_Falling_Block_Entity}（落地会碎），并生成地面冲击环。
	 *
	 * @param spreadArc 弧长比例（本体砸地用 2，覆盖整圈）
	 * @param distance  环半径
	 */
	void ignisSmashBlocks(LivingEntity user, float spreadArc, int distance, float mxy);


	void ignisQuakeWave(LivingEntity user, int distance, float mxy, float vec, boolean grab, float damage, float hpdamage, float airborne, boolean blue);

	/**
	 * Same wave, but collapsing onto an explicit point instead of the user. Skills that fire several waves
	 * after touching down want the rings to stay on the impact spot rather than follow the golem off across
	 * the map. The user is still the damage source and the ally filter.
	 */
	void ignisQuakeWaveAt(LivingEntity user, Vec3 center, int distance, float mxy, float vec, boolean grab, float damage, float hpdamage, float airborne, boolean blue);

	void ignisQuakePush(LivingEntity user, int distance, float mxy, float vec, float damage, float hpdamage, float airborne, boolean blue);

	// 焰魔 - 骨风暴

	void ignisBoneStorm(LivingEntity user, float radius);

	// 备用 - 8 Boss补全（傀儡可能用到，先代理备用）

	// 利维坦
	void spawnMiniAbyssBlast(LivingEntity user, Vec3 dir);

	void spawnPortalAbyssBlast(LivingEntity user, Vec3 dir);

	void spawnAbyssPortal(LivingEntity user, double x, double y, double z);

	void spawnAbyssMark(LivingEntity user, LivingEntity target);

	void spawnTidalTentacle(LivingEntity user, double x, double y, double z);

	void spawnTidalHook(LivingEntity user, Vec3 dir);

	// 焰魔
	void spawnAshenBreath(LivingEntity user, Vec3 dir);

	void spawnBoltStrike(LivingEntity user, double x, double y, double z);

	// 末影守卫
	void spawnVoidShard(LivingEntity user, Vec3 dir);

	void spawnVoidScatter(LivingEntity user, Vec3 dir);

	// 受难者/亡灵系
	void spawnAxeBlade(LivingEntity user, Vec3 dir);

	// 远古遗魂
	void spawnSandstormProjectile(LivingEntity user, Vec3 dir);

	void spawnSandstormOrbit(LivingEntity user, int life);

	void spawnSandstormOrbit(LivingEntity user, int life, float radius, float speed, float baseOffset);

	/**
	 * 在施法者身边生成 count 个环绕沙暴，并按半径档位分配环绕位置，档位 0 为最内侧。
	 * 返回生成的沙暴实体，供调用方后续下达追踪/归位指令。
	 */
	List<Entity> spawnSandstormGroup(LivingEntity user, int count, int life, float baseRadius, float speed);

	/**
	 * 取得该施法者当前拥有的全部环绕沙暴（含正在追踪目标的），按档位从内到外排序。
	 * 用于群组在区块卸载重载后恢复句柄。
	 */
	List<Entity> getSandstorms(LivingEntity user);

	/**
	 * 给单个沙暴下达指令。slot 是它归位后的半径档位；target 非空时朝目标移动，
	 * 为空时回到施法者身边继续按 slot 档位环绕。
	 */
	void setSandstormOrder(Entity storm, int slot, @Nullable LivingEntity target);

	// 斯库拉（轻量版）
	void spawnPlayerCeraunus(LivingEntity user, Vec3 dir);

	// 先驱者
	void spawnAccretion(LivingEntity user, Vec3 dir);

	void spawnWitherSmoke(LivingEntity user);

	// 通用演出
	void screenShake(LivingEntity user, float radius);

	void summonSoulRing(LivingEntity golem);

	void maledictusChargeSound(LivingEntity user);

	void maledictusShortRoar(LivingEntity user);

	void harbingerChargePrepare(LivingEntity user);

	void harbingerChargeSound(LivingEntity user);

	void maledictusAfterImage(LivingEntity user);

	void monstrosityEarthquakeFX(LivingEntity user);

	void scyllaAnchorSlamFX(LivingEntity user);

	/**
	 * 斯库拉招架火花：身前环绕一圈灾变自带的 SPARK 粒子（本体 parryParticle）。
	 */
	void scyllaParryParticle(LivingEntity user);


	/**
	 * 远古遗魂「裂地重踏」起手：一次环绕自身的碎块尘土爆发，外加重踏音效与屏幕震动。
	 */
	void ancientRemnantStompStart(LivingEntity user, Vec3 center, float yaw);

	/**
	 * 远古遗魂「裂地重踏」的单步：在 distance 与 distance+1 两处各掀起一道 0.4π 的擦地冲击，
	 * 造成碎块、伤害与击退，并播放冲击波音效。本体每 2 tick 推进一次，距离 2 收到 19。
	 * <p>
	 * filter 由调用方提供，用来复用傀儡自身的目标规则。
	 */
	void ancientRemnantStomp(LivingEntity user, Vec3 center, float yaw, int distance, Predicate<LivingEntity> filter);

	/**
	 * 同 {@link #ancientRemnantStomp(LivingEntity, Vec3, float, int, Predicate)}，但可以指定击退方向。
	 * outward 为 true 时向远离傀儡的方向击退（远程模式用），false 时保持本体行为。
	 */
	void ancientRemnantStomp(LivingEntity user, Vec3 center, float yaw, int distance, Predicate<LivingEntity> filter, boolean outward);
}
