package org.binaris.misted_atmosphere;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MistedAtmosphere.MODID)
public class MistedAtmosphere {

    public static final String MODID = "misted_atmosphere";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MistedAtmosphere(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Misted Atmosphere initialized");
    }
}
