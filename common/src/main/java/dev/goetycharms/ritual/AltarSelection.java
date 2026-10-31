package dev.goetycharms.ritual;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Return true to stop altar activation; a normal click confirms the explicitly selected candidate. */
public final class AltarSelection {
    public static boolean intercept(DarkAltarBlockEntity altar,Player player,ItemStack held) {
        if(player.level().isClientSide||held.getItem()!=CharmPlatform.blank()||altar.getCurrentRitualRecipe()!=null)return false;
        var all=CharmPlatform.sealings(player.level());if(all.isEmpty())return false;
        var ritual=all.get(0).getRitual();var items=ritual.getItemsOnPedestals(player.level(),altar.getBlockPos());
        var matches=all.stream().filter(r->r.getRitual().matchesAdditionalIngredients(player,r.getIngredients(),items)).sorted(java.util.Comparator.comparing(r->r.sourceId.toString())).toList();
        if(matches.isEmpty())return false;
        var selected=CharmPlatform.selection(held);int index=-1;
        for(int i=0;i<matches.size();i++)if(matches.get(i).sourceId.equals(selected))index=i;
        if(player.isShiftKeyDown()) {
            var next=matches.get((index+1)%matches.size());CharmPlatform.select(held,next.sourceId);
            player.displayClientMessage(Component.translatable("message.goety_charms.selected",dev.goetycharms.item.Charms.create(next.sourceId,next.source,ItemStack.EMPTY).getHoverName()),true);return true;
        }
        if(index>=0)return false;
        if(matches.size()==1){CharmPlatform.select(held,matches.get(0).sourceId);return false;}
        player.displayClientMessage(Component.translatable("message.goety_charms.ambiguous",matches.size()),true);return true;
    }
    private AltarSelection() { }
}
