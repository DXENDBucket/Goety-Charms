package dev.goetycharms.mixin;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import dev.goetycharms.ritual.AltarSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=DarkAltarBlockEntity.class,remap=false)
public abstract class AltarSelectionMixin {
    @Inject(method="activate",at=@At("HEAD"),cancellable=true)
    private void charms$select(Level world,BlockPos pos,Player player,InteractionHand hand,Direction face,CallbackInfoReturnable<Boolean> ci) {
        if(AltarSelection.intercept((DarkAltarBlockEntity)(Object)this,player,player.getItemInHand(hand)))ci.setReturnValue(true);
    }
}
