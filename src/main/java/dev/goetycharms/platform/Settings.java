package dev.goetycharms.platform;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
public final class Settings {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue ENABLED;
    private static final ModConfigSpec.IntValue USE_TICKS;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED;
    static {var b=new ModConfigSpec.Builder();ENABLED=b.comment("Generate sealing rituals for supported loaded summon/conversion recipes. Datapack reload required after recipe policy changes.").define("enabled",true);
        USE_TICKS=b.comment("Hold-use duration before a charm releases; default one second.").defineInRange("use_ticks",20,1,1200);
        EXCLUDED=b.comment("Source ritual recipe IDs excluded from both new sealing and release.").defineListAllowEmpty("excluded_recipes",List::of,()->"goety:summon_apostle",v->v instanceof String s&&ResourceLocation.tryParse(s)!=null);SPEC=b.build();}
    public static boolean enabled(){return SPEC.isLoaded()?ENABLED.get():ENABLED.getDefault();}
    public static int useTicks(){return SPEC.isLoaded()?USE_TICKS.get():USE_TICKS.getDefault();}
    public static boolean allowed(ResourceLocation id){return enabled()&&!(SPEC.isLoaded()?EXCLUDED.get():EXCLUDED.getDefault()).contains(id.toString());}
    private Settings(){ }
}
