package dev.goetycharms.api;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Register explicit custom-factory adapters during common setup on both sides. */
public interface CharmAdapter {
    enum Kind { SUMMONING, CONVERSION, TELEPORTATION, LOCATION }
    record Use(ServerLevel world, Player player, BlockPos origin, LivingEntity target, RitualRecipe recipe) { }
    Kind kind();
    default boolean supports(RitualRecipe recipe) {return true;}
    default boolean canSeal(Player player,ItemStack catalyst) {return true;}
    default boolean canRelease(Use use) {return true;}
    default void finish(Use use, DarkAltarBlockEntity context, ItemStack catalyst) {
        use.recipe().getRitual().finish(use.world(),use.origin(),context,use.player(),catalyst);
    }
}
