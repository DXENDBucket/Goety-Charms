package dev.goetycharms.platform;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.ritual.ModRitualFactory;
import dev.goetycharms.api.CharmAdapter;
import dev.goetycharms.api.RitualCharms;
import dev.goetycharms.item.CharmItem;
import dev.goetycharms.ritual.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import java.util.*;
public final class CharmPlatform {
    private static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,"goety_charms");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,"goety_charms");
    private static final DeferredRegister<ModRitualFactory> FACTORIES=DeferredRegister.create(new ResourceLocation("goety:ritual_factory"),"goety_charms");
    private static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,"goety_charms");
    private static final java.util.function.Supplier<CharmItem> BLANK=ITEMS.register("blank_charm",()->new CharmItem(new Item.Properties().stacksTo(64),true));
    private static final java.util.function.Supplier<CharmItem> SUMMON=ITEMS.register("summoning_charm",()->new CharmItem(new Item.Properties().stacksTo(16),false));
    private static final java.util.function.Supplier<CharmItem> CONVERT=ITEMS.register("conversion_charm",()->new CharmItem(new Item.Properties().stacksTo(16),false));
    private static final java.util.function.Supplier<CharmItem> TELEPORT=ITEMS.register("teleportation_charm",()->new CharmItem(new Item.Properties().stacksTo(16),false));
    private static final java.util.function.Supplier<CharmItem> LOCATE=ITEMS.register("location_charm",()->new CharmItem(new Item.Properties().stacksTo(16),false));
    private static final java.util.function.Supplier<RecipeSerializer<SealingRecipe>> SERIALIZER=SERIALIZERS.register("sealing",SealingSerializer::new);
    static {FACTORIES.register("seal",()->new ModRitualFactory(SealingRitual::new));
        TABS.register("charms",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.goety_charms")).icon(()->new ItemStack(blank()))
                .displayItems((parameters,output)->{output.accept(blank());for(var kind:CharmAdapter.Kind.values())output.accept(charged(kind));}).build());}
    public static Item blank(){return BLANK.get();}
    public static Item charged(CharmAdapter.Kind kind){return switch(kind){case SUMMONING -> SUMMON.get();case CONVERSION -> CONVERT.get();case TELEPORTATION -> TELEPORT.get();case LOCATION -> LOCATE.get();};}
    public static RecipeSerializer<SealingRecipe> serializer(){return SERIALIZER.get();}
    public static boolean enabled(){return Settings.enabled();}
    public static int useTicks(){return Settings.useTicks();}
    public static boolean allowed(ResourceLocation id){return Settings.allowed(id);}
    public static ResourceLocation selection(ItemStack stack){var value=stack.hasTag()?stack.getTag().getString("goety_charms:source"):"";return value.isEmpty()?null:ResourceLocation.tryParse(value);}
    public static ResourceLocation entity(ItemStack stack){var value=stack.hasTag()?stack.getTag().getString("goety_charms:entity"):"";return value.isEmpty()?null:ResourceLocation.tryParse(value);}
    public static String conversionTarget(ItemStack stack){return stack.hasTag()?stack.getTag().getString("goety_charms:convert_target"):"";}
    public static void conversionTarget(ItemStack stack,String name){if(name!=null&&!name.isBlank())stack.getOrCreateTag().putString("goety_charms:convert_target",name);else if(stack.hasTag())stack.getTag().remove("goety_charms:convert_target");}
    public static String locationName(ItemStack stack){return stack.hasTag()?stack.getTag().getString("goety_charms:location_name"):"";}
    public static void locationName(ItemStack stack,String name){if(name!=null&&!name.isBlank())stack.getOrCreateTag().putString("goety_charms:location_name",name);}
    public static void select(ItemStack stack,ResourceLocation id){stack.getOrCreateTag().putString("goety_charms:source",id.toString());}
    public static void write(ItemStack stack,ResourceLocation id,ResourceLocation entity,ItemStack catalyst){select(stack,id);if(entity!=null)stack.getOrCreateTag().putString("goety_charms:entity",entity.toString());if(!catalyst.isEmpty())stack.getOrCreateTag().put("goety_charms:catalyst",catalyst.save(new net.minecraft.nbt.CompoundTag()));}
    public static boolean sameCharm(ItemStack a,ItemStack b){return ItemStack.isSameItemSameTags(a,b);}
    public static ItemStack catalyst(ItemStack stack){return stack.hasTag()?ItemStack.of(stack.getTag().getCompound("goety_charms:catalyst")):ItemStack.EMPTY;}
    public static RitualRecipe source(Level level,ResourceLocation id){return level.getRecipeManager().byKey(id).filter(r->r instanceof RitualRecipe).map(r->(RitualRecipe)r).orElse(null);}
    public static List<SealingRecipe> sealings(Level level){return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get()).stream().filter(r->r instanceof SealingRecipe).map(r->(SealingRecipe)r).toList();}
    public static void derive(RecipeManager manager){
        var all=new ArrayList<Recipe<?>>(manager.getRecipes());var ids=new HashSet<ResourceLocation>();all.forEach(r->ids.add(r.getId()));
        for(var recipe:List.copyOf(all))if(recipe instanceof RitualRecipe ritual&&RitualCharms.adapter(recipe.getId(),ritual)!=null) {
            var id=SealingRecipe.id(recipe.getId());if(ids.add(id))all.add(new SealingRecipe(recipe.getId(),ritual));
        }
        manager.replaceRecipes(all);
    }
    public static ItemStack output(com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity altar){return altar.itemStackHandler.map(handler->handler.getStackInSlot(0)).orElse(ItemStack.EMPTY);}
    public static void output(com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity altar,ItemStack result){altar.itemStackHandler.ifPresent(handler->handler.setStackInSlot(0,result));}
    public static void register(IEventBus bus){ITEMS.register(bus);SERIALIZERS.register(bus);FACTORIES.register(bus);TABS.register(bus);
        MinecraftForge.EVENT_BUS.addListener(net.minecraftforge.eventbus.api.EventPriority.NORMAL,true,(net.minecraftforge.event.entity.EntityJoinLevelEvent event)->CompletionScope.observe(event.getEntity()));
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.TickEvent.ServerTickEvent event)->{if(event.phase==net.minecraftforge.event.TickEvent.Phase.END)UtilityRelease.tick(event.getServer());});
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event)->UtilityRelease.cancel(event.getEntity().getUUID()));
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.server.ServerStoppedEvent event)->UtilityRelease.clear());
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.entity.player.ItemTooltipEvent event)->{
            dev.goetycharms.item.CharmTooltips.append(event.getItemStack(),event.getEntity(),event.getToolTip(),event.getFlags().isAdvanced());
        });
        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.RegisterCommandsEvent event)->dev.goetycharms.command.CharmCommands.register(event.getDispatcher()));
    }
    private CharmPlatform() { }
}
