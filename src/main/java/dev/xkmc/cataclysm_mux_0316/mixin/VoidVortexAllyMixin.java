package dev.xkmc.cataclysm_mux_0316.mixin;

import com.github.L_Ender.cataclysm.entity.effect.Void_Vortex_Entity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 3.16 的虚空涡流只豁免施法者自己，会吸住友方；3.27 原版已补上队友豁免。
 * 这里给吸附加一层同盟判断，行为与 3.27 对齐。
 * 3.27 环境下该 wrap 同样能命中，但原版条件已先过滤，等价无害。
 */
@Mixin(Void_Vortex_Entity.class)
public abstract class VoidVortexAllyMixin extends Entity {

	public VoidVortexAllyMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
	private void cataclysm_mux_0316$allyExempt(LivingEntity entity, Vec3 vec, Operation<Void> op) {
		LivingEntity owner = ((Void_Vortex_Entity) (Object) this).getOwner();
		if (owner != null && (entity == owner || owner.isAlliedTo(entity))) return;
		op.call(entity, vec);
	}

}
