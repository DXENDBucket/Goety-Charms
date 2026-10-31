package dev.goetycharms.platform;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import dev.goetycharms.ritual.SealingRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
public final class SealingSerializer implements RecipeSerializer<SealingRecipe> {
    private static final MapCodec<SealingRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(
            ResourceLocation.CODEC.fieldOf("source_id").forGetter((SealingRecipe r)->r.sourceId),
            RitualRecipe.SERIALIZER.codec().codec().fieldOf("source").forGetter((SealingRecipe r)->r.source)).apply(i,SealingRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf,SealingRecipe> STREAM=StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,r->r.sourceId,RitualRecipe.SERIALIZER.streamCodec(),r->r.source,SealingRecipe::new);
    @Override public MapCodec<SealingRecipe> codec(){return CODEC;}
    @Override public StreamCodec<RegistryFriendlyByteBuf,SealingRecipe> streamCodec(){return STREAM;}
}
