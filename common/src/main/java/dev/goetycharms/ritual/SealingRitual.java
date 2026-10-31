package dev.goetycharms.ritual;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import dev.goetycharms.item.Charms;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
public final class SealingRitual extends Ritual {
    public SealingRitual(RitualRecipe recipe){super(recipe);}
    @Override public boolean identify(Level level,BlockPos pos,Player player,ItemStack activation) {
        if(!(recipe instanceof SealingRecipe sealed))return false;
        var selected=CharmPlatform.selection(activation);
        return (selected==null||selected.equals(sealed.sourceId))&&super.identify(level,pos,player,activation)
                &&validCatalyst(level,pos,player,sealed,java.util.List.of());
    }
    @Override public boolean isValid(Level level,BlockPos pos,DarkAltarBlockEntity altar,Player player,ItemStack activation,java.util.List<net.minecraft.world.item.crafting.Ingredient> remaining) {
        return super.isValid(level,pos,altar,player,activation,remaining)
                &&validCatalyst(level,pos,player,(SealingRecipe)recipe,altar.consumedIngredients);
    }
    private boolean validCatalyst(Level level,BlockPos pos,Player player,SealingRecipe sealed,java.util.List<ItemStack> consumed) {
        var adapter=dev.goetycharms.api.RitualCharms.definition(sealed.source);
        if(adapter==null)return false;
        var catalyst=consumed.isEmpty()?getItemsOnPedestals(level,pos).stream().filter(sealed.source.getActivationItem()::test).findFirst().orElse(ItemStack.EMPTY):consumed.get(0);
        return !catalyst.isEmpty()&&adapter.canSeal(player,catalyst);
    }
    @Override public void finish(Level level,BlockPos pos,DarkAltarBlockEntity altar,Player player,ItemStack activation) {
        var sealed=(SealingRecipe)recipe;
        if(altar.consumedIngredients.isEmpty())throw new IllegalStateException("Sealing finished without original catalyst");
        var catalyst=altar.consumedIngredients.get(0).copy();catalyst.setCount(1);
        if(!sealed.source.getActivationItem().test(catalyst))throw new IllegalStateException("Consumed catalyst no longer matches sealing source");
        try(var scope=CompletionScope.capture(sealed)){super.finish(level,pos,altar,player,activation);}
        activation.shrink(1);CharmPlatform.output(altar,Charms.create(sealed.sourceId,sealed.source,catalyst));
    }
}
