package dev.goetycharms.mixin;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import dev.goetycharms.ritual.CompletionScope;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=Ritual.class,remap=false)
public abstract class NativeCompletionMixin {
    @Inject(method="finish",at=@At("HEAD"),cancellable=true)
    private void charms$paid(Level world,BlockPos pos,DarkAltarBlockEntity tile,Player player,ItemStack catalyst,CallbackInfo ci) {
        if(CompletionScope.paid(tile))ci.cancel();
    }
    @Inject(method="areAdditionalIngredientsFulfilled",at=@At("HEAD"),cancellable=true)
    private void charms$paidIngredients(Level world,BlockPos pos,Player player,java.util.List<net.minecraft.world.item.crafting.Ingredient> ingredients,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        if(ingredients.isEmpty()&&CompletionScope.paidIngredients(world,pos,player))cir.setReturnValue(true);
    }
}
