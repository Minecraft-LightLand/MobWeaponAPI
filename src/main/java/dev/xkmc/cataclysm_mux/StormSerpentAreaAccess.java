package dev.xkmc.cataclysm_mux;

/**
 * Implemented by {@code StormSerpentAreaMixin} so a skill can widen one serpent's slam radius
 * without a mixin-internal call.
 * <p>
 * <b>必须放在 mixin 包之外</b>：Mixin 禁止外部直接引用 {@code cataclysm_mux.mixins.json} 声明的
 * mixin 包（{@code dev.xkmc.cataclysm_mux.mixin}）里的类，否则加载时抛
 * {@code IllegalClassLoadError: ... is in a defined mixin package ... and cannot be referenced directly}。
 */
public interface StormSerpentAreaAccess {

	void cataclysm_mux$setAreaScale(double scale);

}
