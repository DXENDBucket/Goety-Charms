package dev.goetycharms.platform;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import dev.goetycharms.ritual.SealingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.google.gson.JsonObject;
public final class SealingSerializer implements RecipeSerializer<SealingRecipe> {
    @Override public SealingRecipe fromJson(ResourceLocation id,JsonObject json) {
        var sourceId=new ResourceLocation(json.get("source_id").getAsString());return new SealingRecipe(sourceId,RitualRecipe.SERIALIZER.fromJson(sourceId,json.getAsJsonObject("source")));
    }
    @Override public SealingRecipe fromNetwork(ResourceLocation id,FriendlyByteBuf buf) {
        var sourceId=buf.readResourceLocation();return new SealingRecipe(sourceId,RitualRecipe.SERIALIZER.fromNetwork(sourceId,buf));
    }
    @Override public void toNetwork(FriendlyByteBuf buf,SealingRecipe recipe) {buf.writeResourceLocation(recipe.sourceId);RitualRecipe.SERIALIZER.toNetwork(buf,recipe.source);}
}
