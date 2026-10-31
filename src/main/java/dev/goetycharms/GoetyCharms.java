package dev.goetycharms;
import dev.goetycharms.platform.CharmPlatform;
import dev.goetycharms.platform.Settings;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.IEventBus;
@Mod("goety_charms")
public final class GoetyCharms {
    public GoetyCharms(IEventBus bus,ModContainer container){container.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON,Settings.SPEC);CharmPlatform.register(bus);}
}
