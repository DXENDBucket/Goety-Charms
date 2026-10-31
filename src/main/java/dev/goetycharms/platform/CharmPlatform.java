package dev.goetycharms.platform;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.ritual.ModRitualFactory;
import dev.goetycharms.api.CharmAdapter;
import dev.goetycharms.api.RitualCharms;
import dev.goetycharms.item.CharmItem;
import dev.goetycharms.ritual.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.*;
public final class CharmPlatform {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("goety_charms");
    private static final DeferredRegister<DataComponentType<?>> DATA=DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,"goety_charms");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"goety_charms");
    private static final DeferredRegister<ModRitualFactory> FACTORIES=DeferredRegister.create(ResourceLocation.tryParse("goety:ritual_factory"),"goety_charms");
    private static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,"goety_charms");
    private static final java.util.function.Supplier<CharmItem> BLANK=ITEMS.registerItem("blank_charm",p->new CharmItem(p,true),new Item.Properties().stacksTo(64));
    private static final java.util.function.Supplier<CharmItem> SUMMON=ITEMS.registerItem("summoning_charm",p->new CharmItem(p,false),new Item.Properties().stacksTo(16));
    private static final java.util.function.Supplier<CharmItem> CONVERT=ITEMS.registerItem("conversion_charm",p->new CharmItem(p,false),new Item.Properties().stacksTo(16));
    private static final java.util.function.Supplier<DataComponentType<ItemContainerContents>> CATALYST=DATA.register("catalyst",()->DataComponentType.<ItemContainerContents>builder().persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).build());
    private static final java.util.function.Supplier<CharmItem> TELEPORT=ITEMS.registerItem("teleportation_charm",p->new CharmItem(p,false),new Item.Properties().stacksTo(16));
    private static final java.util.function.Supplier<CharmItem> LOCATE=ITEMS.registerItem("location_charm",p->new CharmItem(p,false),new Item.Properties().stacksTo(16));
    private static final java.util.function.Supplier<RecipeSerializer<SealingRecipe>> SERIALIZER=SERIALIZERS.register("sealing",SealingSerializer::new);
    static {FACTORIES.register("seal",()->new ModRitualFactory(SealingRitual::new));
        TABS.register("charms",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.goety_charms")).icon(()->new ItemStack(blank()))
                .displayItems((parameters,output)->{output.accept(blank());for(var kind:CharmAdapter.Kind.values())output.accept(charged(kind));}).build());}
    private static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    public static Item blank(){return BLANK.get();}
    public static Item charged(CharmAdapter.Kind kind){return switch(kind){case SUMMONING -> SUMMON.get();case CONVERSION -> CONVERT.get();case TELEPORTATION -> TELEPORT.get();case LOCATION -> LOCATE.get();};}
    public static RecipeSerializer<SealingRecipe> serializer(){return SERIALIZER.get();}
    public static boolean enabled(){return Settings.enabled();}
    public static int useTicks(){return Settings.useTicks();}
    public static boolean allowed(ResourceLocation id){return Settings.allowed(id);}
    public static ResourceLocation selection(ItemStack stack){var value=data(stack).getString("goety_charms:source");return value.isEmpty()?null:ResourceLocation.tryParse(value);}
    public static ResourceLocation entity(ItemStack stack){var value=data(stack).getString("goety_charms:entity");return value.isEmpty()?null:ResourceLocation.tryParse(value);}
    public static String conversionTarget(ItemStack stack){return data(stack).getString("goety_charms:convert_target");}
    public static void conversionTarget(ItemStack stack,String name){var tag=data(stack);if(name!=null&&!name.isBlank())tag.putString("goety_charms:convert_target",name);else tag.remove("goety_charms:convert_target");stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    public static String locationName(ItemStack stack){return data(stack).getString("goety_charms:location_name");}
    public static void locationName(ItemStack stack,String name){if(name!=null&&!name.isBlank()){var tag=data(stack);tag.putString("goety_charms:location_name",name);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}}
    public static void select(ItemStack stack,ResourceLocation id){var tag=data(stack);tag.putString("goety_charms:source",id.toString());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    public static void write(ItemStack stack,ResourceLocation id,ResourceLocation entity,ItemStack catalyst){select(stack,id);var tag=data(stack);if(entity!=null)tag.putString("goety_charms:entity",entity.toString());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));if(!catalyst.isEmpty())stack.set(CATALYST.get(),ItemContainerContents.fromItems(List.of(catalyst)));}
    public static boolean sameCharm(ItemStack a,ItemStack b){return ItemStack.isSameItemSameComponents(a,b);}
    public static ItemStack catalyst(ItemStack stack){return stack.getOrDefault(CATALYST.get(),ItemContainerContents.EMPTY).copyOne();}
    public static RitualRecipe source(Level level,ResourceLocation id){return level.getRecipeManager().byKey(id).map(RecipeHolder::value).filter(r->r instanceof RitualRecipe).map(r->(RitualRecipe)r).orElse(null);}
    public static List<SealingRecipe> sealings(Level level){return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get()).stream().map(RecipeHolder::value).filter(r->r instanceof SealingRecipe).map(r->(SealingRecipe)r).toList();}
    public static void derive(RecipeManager manager){
        var all=new ArrayList<RecipeHolder<?>>(manager.getRecipes());var ids=new HashSet<ResourceLocation>();all.forEach(r->ids.add(r.id()));
        for(var holder:List.copyOf(all))if(holder.value() instanceof RitualRecipe recipe&&RitualCharms.adapter(holder.id(),recipe)!=null) {
            var id=SealingRecipe.id(holder.id());if(ids.add(id))all.add(new RecipeHolder<>(id,new SealingRecipe(holder.id(),recipe)));
        }
        manager.replaceRecipes(all);
    }
    public static ItemStack output(com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity altar){return altar.itemStackHandler.getStackInSlot(0);}
    public static void output(com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity altar,ItemStack result){altar.itemStackHandler.setStackInSlot(0,result);}
    public static void register(IEventBus bus){ITEMS.register(bus);DATA.register(bus);SERIALIZERS.register(bus);FACTORIES.register(bus);TABS.register(bus);
        NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.NORMAL,true,(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event)->CompletionScope.observe(event.getEntity()));
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.tick.ServerTickEvent.Post event)->UtilityRelease.tick(event.getServer()));
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event)->UtilityRelease.cancel(event.getEntity().getUUID()));
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppedEvent event)->UtilityRelease.clear());
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.ItemTooltipEvent event)->{
            dev.goetycharms.item.CharmTooltips.append(event.getItemStack(),event.getEntity(),event.getToolTip(),event.getFlags().isAdvanced());
        });
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent event)->dev.goetycharms.command.CharmCommands.register(event.getDispatcher()));
    }
    private CharmPlatform() { }
}
