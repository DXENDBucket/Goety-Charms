package dev.goetycharms.mixin;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(RecipeManager.class)
public abstract class DerivedRecipesMixin {
    @Inject(method="apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",at=@At("RETURN"))
    private void charms$derive(CallbackInfo ci){CharmPlatform.derive((RecipeManager)(Object)this);}
}
