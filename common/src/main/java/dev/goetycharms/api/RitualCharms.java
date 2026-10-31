package dev.goetycharms.api;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import dev.goetycharms.ritual.SealingRecipe;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/** Factory-based support is independent of the recipe's namespace. No generic field-only fallback. */
public final class RitualCharms {
    private static final Map<ResourceLocation,CharmAdapter> ADAPTERS=new ConcurrentHashMap<>();
    static {
        for(String name:new String[]{"summon","summon_tamed","summon_tamed_no_variant"})register(ResourceLocation.tryParse("goety:"+name),nativeAdapter(CharmAdapter.Kind.SUMMONING));
        for(String name:new String[]{"convert","convert_tamed","convert_complete_tamed"})register(ResourceLocation.tryParse("goety:"+name),nativeAdapter(CharmAdapter.Kind.CONVERSION));
    }
    private static CharmAdapter nativeAdapter(CharmAdapter.Kind kind) {return new CharmAdapter(){public Kind kind(){return kind;}};}
    public static void register(ResourceLocation factory,CharmAdapter adapter) {ADAPTERS.put(java.util.Objects.requireNonNull(factory),java.util.Objects.requireNonNull(adapter));}
    public static CharmAdapter adapter(ResourceLocation id,RitualRecipe recipe) {
        if(recipe instanceof SealingRecipe||!CharmPlatform.allowed(id))return null;
        return definition(recipe);
    }
    public static CharmAdapter definition(RitualRecipe recipe) {
        var adapter=ADAPTERS.get(recipe.getRitualType());
        // A custom factory may still construct an unmodified native executor.
        if(adapter==null&&recipe.getRitual().getClass()==com.Polarice3.Goety.common.ritual.SummonRitual.class)adapter=nativeAdapter(CharmAdapter.Kind.SUMMONING);
        if(adapter==null&&recipe.getRitual().getClass()==com.Polarice3.Goety.common.ritual.ConvertRitual.class)adapter=nativeAdapter(CharmAdapter.Kind.CONVERSION);
        return adapter!=null&&adapter.supports(recipe)&&((adapter.kind()==CharmAdapter.Kind.SUMMONING&&recipe.isSummoning())
                ||(adapter.kind()==CharmAdapter.Kind.CONVERSION&&recipe.isConversion()))?adapter:null;
    }
    private RitualCharms() { }
}
