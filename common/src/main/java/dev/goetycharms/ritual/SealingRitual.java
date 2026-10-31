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
        return (selected==null||selected.equals(sealed.sourceId))&&super.identify(level,pos,player,activation);
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
