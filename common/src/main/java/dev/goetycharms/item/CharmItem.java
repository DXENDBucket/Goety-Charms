package dev.goetycharms.item;
import dev.goetycharms.platform.CharmPlatform;
import dev.goetycharms.ritual.CharmRelease;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
public final class CharmItem extends Item {
    private final boolean blank;
    public CharmItem(Properties properties,boolean blank){super(properties);this.blank=blank;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        if(blank)return InteractionResultHolder.pass(player.getItemInHand(hand));
        player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    @Override public InteractionResult interactLivingEntity(ItemStack stack,Player player,LivingEntity target,InteractionHand hand) {
        if(blank)return InteractionResult.PASS;player.startUsingItem(hand);return InteractionResult.CONSUME;
    }
    @Override public InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        var player=context.getPlayer();
        if(blank&&player!=null&&player.isShiftKeyDown()&&context.getLevel().getBlockEntity(context.getClickedPos())
                instanceof com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity altar) {
            if(context.getLevel().isClientSide)return InteractionResult.SUCCESS;
            if(dev.goetycharms.ritual.AltarSelection.intercept(altar,player,context.getItemInHand()))return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity actor) {
        if(!level.isClientSide&&actor instanceof Player player)CharmRelease.fromHand(stack,player);
        return stack;
    }
    public int getUseDuration(ItemStack stack){return CharmPlatform.useTicks();}
    public int getUseDuration(ItemStack stack,LivingEntity actor){return CharmPlatform.useTicks();}
    @Override public UseAnim getUseAnimation(ItemStack stack){return blank?UseAnim.NONE:UseAnim.BOW;}
    @Override public boolean doesSneakBypassUse(ItemStack stack,net.minecraft.world.level.LevelReader level,net.minecraft.core.BlockPos pos,Player player) {
        return blank&&level.getBlockEntity(pos) instanceof com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
    }
    @Override public Component getName(ItemStack stack) {
        if(stack.getItem()==CharmPlatform.charged(dev.goetycharms.api.CharmAdapter.Kind.LOCATION)) {
            var name=CharmPlatform.locationName(stack);
            if(!name.isBlank())return Component.translatable(getDescriptionId()+".named",Component.translatable(name));
        }
        var type=Charms.targetType(stack);
        return blank||type==null?super.getName(stack):Component.translatable(getDescriptionId()+".named",type.getDescription());
    }
}
