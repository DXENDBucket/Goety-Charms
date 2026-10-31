package dev.goetycharms.item;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import dev.goetycharms.api.CharmAdapter;
import dev.goetycharms.api.RitualCharms;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
public final class Charms {
    public static ItemStack create(ResourceLocation id,RitualRecipe source,ItemStack catalyst) {
        var adapter=RitualCharms.definition(source);
        if(adapter==null)throw new IllegalArgumentException("Unsupported charm ritual "+id);
        var stack=new ItemStack(CharmPlatform.charged(adapter.kind()));
        var entity=adapter.kind()==CharmAdapter.Kind.SUMMONING?source.getEntityToSummon():source.getEntityToConvertInto();
        CharmPlatform.write(stack,id,BuiltInRegistries.ENTITY_TYPE.getKey(entity),catalyst);return stack;
    }
    public static EntityType<?> targetType(ItemStack stack){var id=CharmPlatform.entity(stack);return id==null?null:BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);}
    private Charms() { }
}
