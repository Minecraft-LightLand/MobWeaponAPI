package dev.xkmc.cataclysm_mux.mixin;

import com.github.L_Ender.cataclysm.entity.projectile.Storm_Serpent_Entity;
import dev.xkmc.cataclysm_mux.StormSerpentAreaAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * 幻影水蛇落点伤害半径可调。
 * <p>
 * 本体 {@code Storm_Serpent_Entity} 把落点判定写死成一个固定半宽的 AABB，这里把
 * {@code getEntitiesOfClass} 收到的 AABB 按 {@link StormSerpentAreaAccess} 设定的倍率等比放大，
 * 于是「落点伤害半径」就能随胸甲加成一起变大，而不改动落点位置。
 * <p>
 * 只重定向那一次调用（{@code tick()} 里唯一的 {@code getEntitiesOfClass}），不改落点位置，
 * 所以水蛇仍然是「砸在前方 8 格」。
 * <p>
 * <b>必须只存在于 API 模块</b>：两个实现分支（0316 / 0327）的 jar 会被 jarjar 同时装进同一个
 * 游戏，若各写一份 {@code @Redirect}，第二份会因 {@code @Redirect conflict} 被跳过并按
 * {@code defaultRequire} 抛 {@code InjectionError} 直接崩服。目标类在两个灾变版本里同名同结构，
 * 一份即可。
 */
@Pseudo
@Mixin(Storm_Serpent_Entity.class)
public abstract class StormSerpentAreaMixin extends Entity implements StormSerpentAreaAccess {

	@Unique
	private double cataclysm_mux$areaScale = 1.0;

	public StormSerpentAreaMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Override
	public void cataclysm_mux$setAreaScale(double scale) {
		this.cataclysm_mux$areaScale = scale;
	}

	@Redirect(method = "tick", require = 0,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	private List<LivingEntity> cataclysm_mux$widenSlam(Level level, Class<LivingEntity> cls, AABB box) {
		double scale = this.cataclysm_mux$areaScale;
		if (scale == 1.0) return level.getEntitiesOfClass(cls, box);
		double cx = (box.minX + box.maxX) * 0.5;
		double cz = (box.minZ + box.maxZ) * 0.5;
		double hx = (box.maxX - box.minX) * 0.5 * scale;
		double hz = (box.maxZ - box.minZ) * 0.5 * scale;
		return level.getEntitiesOfClass(cls,
				new AABB(cx - hx, box.minY, cz - hz, cx + hx, box.maxY, cz + hz));
	}

}
