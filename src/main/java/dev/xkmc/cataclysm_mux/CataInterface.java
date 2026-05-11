package dev.xkmc.cataclysm_mux;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface CataInterface {

	static CataInterface get() {
		return null;
	}

	record ProjectileData(Projectile proj, float speed, float gravity, int cooldown) {

	}

	// 傀儡
	void sandstormAttack(LivingEntity golem, LivingEntity target, int life);

	boolean isLaser(DamageSource source);

	boolean isMissile(DamageSource source);

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

	@Nullable
	Projectile coralSpear(LivingEntity user, Level level, ItemStack stack);

}
