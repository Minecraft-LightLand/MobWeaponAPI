package dev.xkmc.cataclysm_mux;

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

	// 先驱者 - 技能

	void shootHowitzer(LivingEntity user, Vec3 dir);

	// 破块 - 通用（先驱者/魔训兽/远古遗魂冲锋破块）

	void fallingBlocks(LivingEntity user, double inflate);

	// 末影守卫 - 技能

	void shootEnderBullet(LivingEntity user, LivingEntity target);

	void gravityPull(LivingEntity user);

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

	void scyllaSummonWave2(LivingEntity user, LivingEntity target);

	void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning);

	void scyllaSpearThrow(LivingEntity user, LivingEntity target, boolean lightning, float lateral);

	void scyllaFallingBurst(LivingEntity user, int sparkAmount);

	void scyllaFallingBurstAt(LivingEntity user, double x, double y, double z, int sparkAmount);

	void scyllaLightningExplosion(LivingEntity user, double x, double y, double z);

	void scyllaBurstLightning(LivingEntity user);

	void scyllaWhip(LivingEntity user, LivingEntity target);

	void scyllaSpin(LivingEntity user);

	void guardianStompFangs(LivingEntity user, LivingEntity target);

	void guardianUppercut(LivingEntity user, LivingEntity target);

	void guardianHugFX(LivingEntity user);

	void guardianGravityPunch(LivingEntity user, LivingEntity target);

	void guardianSlamLanding(LivingEntity user);

	void guardianExplosionSound(LivingEntity user);

	void scyllaSpinSound(LivingEntity user);

	void scyllaStormParticles(LivingEntity user);

	void scyllaThunderCloud(LivingEntity user, LivingEntity target);

	void scyllaThunderCloudStorm(LivingEntity user, double x, double y, double z);

	void scyllaThunderCloudSpear(LivingEntity user, LivingEntity target, double x, double y, double z);

	// 焰魔 - 震地

	void ignisEarthquakeFX(LivingEntity user, float shakeRadius);

	void ignisAreaPunch(LivingEntity user, float radius);

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

}
