package dev.xkmc.cataclysm_mux;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import static dev.xkmc.cataclysm_mux.CataclysmMux.LOGGER;

public class LWCataProxy {

	public static void stackBlazingBrand(LivingEntity user, LivingEntity target, float factor) {
		try {
			CataInterface.get().stackBlazingBrand(user, target, factor);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static void inflictStun(LivingEntity user, LivingEntity target, int time) {
		try {
			CataInterface.get().inflictStun(user, target, time);
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	public static int spawnHalberd(Vec3 pos, LivingEntity player, int delay) {
		try {
			return CataInterface.get().spawnHalberd(pos, player, delay);
		} catch (Throwable e) {
			LOGGER.throwing(e);
			return 20;
		}
	}

}
