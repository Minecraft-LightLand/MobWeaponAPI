package dev.xkmc.cataclysm_mux;

import dev.xkmc.cataclysm_mux_0316.CataInterfaceImpl_0316;
import dev.xkmc.cataclysm_mux_0327.CataInterfaceImpl_0327;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

//@Mod(CataclysmMux.MODID)
public class CataclysmMux {

	public static final String MODID = "cataclysm_mux";

	public static final Logger LOGGER = LogManager.getLogger();

	private static CataInterface INS;

	static CataInterface get() {
		if (INS != null) return INS;
		INS = new CataInterfaceImpl_0316();
		try {
			ModFileInfo cataclysm = LoadingModList.get().getModFileById("cataclysm");
			if (cataclysm != null && cataclysm.getMods().stream()
					.anyMatch(modInfo -> modInfo.getModId().equals("cataclysm")
							&& modInfo.getVersion().compareTo(new DefaultArtifactVersion("3.16")) > 0))
				INS = new CataInterfaceImpl_0327();
		} catch (Throwable e) {
		}
		return INS;

	}

}
