package dev.xkmc.mob_weapon_api.init;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import dev.xkmc.l2core.init.reg.simple.DCReg;
import dev.xkmc.l2core.init.reg.simple.DCVal;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.mob_weapon_api.example.vanilla.VanillaMobManager;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(MobWeaponAPI.MODID)
@EventBusSubscriber(modid = MobWeaponAPI.MODID)
public class MobWeaponAPI {

	public static final String MODID = "mob_weapon_api";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Registrate REGISTRATE = Registrate.create(MODID);
	public static final Reg REG = new Reg(MODID);
	public static final DCReg DC = DCReg.of(REG);
	public static final DCVal<Long> TIMESTAMP = DC.longVal("timestamp");

	public MobWeaponAPI() {
		//if (ModList.get().isLoaded(Cataclysm.MODID)) CataclysmIntegration.register();
		//if (ModList.get().isLoaded(TwilightForestMod.ID)) TFIntegration.register();
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			WeaponRegistry.init();
			//if (ModList.get().isLoaded(L2Complements.MODID)) L2ComplementsIntegration.init();
			//if (ModList.get().isLoaded(L2Archery.MODID)) L2ArcheryIntegration.init();
			//if (ModList.get().isLoaded(L2Weaponry.MODID)) L2WeaponryIntegration.init();
			//TODO if (ModList.get().isLoaded(TConstruct.MOD_ID)) TConstructIntegration.init();
			//if (ModList.get().isLoaded(Cataclysm.MODID)) CataclysmIntegration.init();
			//if (ModList.get().isLoaded(TwilightForestMod.ID)) TFIntegration.init();
			//if (ModList.get().isLoaded(Create.ID)) CreateIntegration.init();
			VanillaMobManager.init();
		});
	}

	public static Identifier loc(String id) {
		return Identifier.fromNamespaceAndPath(MODID, id);
	}

}
