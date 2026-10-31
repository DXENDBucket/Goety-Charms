package dev.goetycharms;
import dev.goetycharms.platform.CharmPlatform;
import dev.goetycharms.platform.Settings;
import net.minecraftforge.fml.common.Mod;
@Mod("goety_charms")
public final class GoetyCharms {
    public GoetyCharms(){net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON,Settings.SPEC);CharmPlatform.register(net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus());}
}
