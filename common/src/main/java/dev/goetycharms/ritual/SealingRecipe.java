package dev.goetycharms.ritual;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import dev.goetycharms.item.Charms;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
/** A real Goety ritual recipe. Native altar payment, remainders, persistence and JEI remain in use. */
public final class SealingRecipe extends RitualRecipe {
    public final ResourceLocation sourceId;
    public final RitualRecipe source;
    public static ResourceLocation id(ResourceLocation source){return ResourceLocation.tryParse("goety_charms:sealing/"+source.getNamespace()+"/"+source.getPath());}
    private static NonNullList<Ingredient> inputs(RitualRecipe source) {
        var inputs=NonNullList.<Ingredient>create();inputs.add(source.getActivationItem());inputs.addAll(source.getIngredients());return inputs;
    }
    public SealingRecipe(ResourceLocation sourceId,RitualRecipe source) {
        super(id(sourceId),source.getGroup(),source.getCraftType(),ResourceLocation.tryParse("goety_charms:seal"),
                Charms.create(sourceId,source,net.minecraft.world.item.ItemStack.EMPTY),null,null,Ingredient.of(CharmPlatform.blank()),inputs(source),
                source.getDuration(),source.getSummonLife(),source.getSoulCost(),source.getEntityToSacrifice(),source.getEntityToSacrificeDisplayName(),
                null,source.getEntityToConvertDisplayName(),source.getStructureTag(),source.getStructureName(),source.getEnchantment(),source.getXPLevelCost(),source.getResearch());
        this.sourceId=sourceId;this.source=source;
    }
    @Override public boolean isSummoning(){return false;}
    @Override public boolean isConversion(){return false;}
    // Native completion callbacks see the original entity kind, but altar activation does not
    // require an on-site conversion target, summon capacity or Apostle warning during sealing.
    @Override public EntityType<?> getEntityToSummon(){return CompletionScope.capturing(this)?source.getEntityToSummon():null;}
    @Override public EntityType<?> getEntityToConvertInto(){return CompletionScope.capturing(this)?source.getEntityToConvertInto():null;}
    @Override public RecipeSerializer<?> getSerializer(){return CharmPlatform.serializer();}
}
