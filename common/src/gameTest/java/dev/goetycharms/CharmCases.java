package dev.goetycharms;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import dev.goetycharms.api.*;
import dev.goetycharms.item.*;
import dev.goetycharms.platform.CharmPlatform;
import dev.goetycharms.ritual.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import java.util.function.UnaryOperator;
public final class CharmCases {
    public static SealingRecipe recipe(GameTestHelper h,String suffix) {
        return CharmPlatform.sealings(h.getLevel()).stream().filter(r->r.sourceId.getPath().endsWith(suffix)).findFirst().orElseThrow();
    }
    public static ItemStack charm(SealingRecipe recipe) {var catalyst=recipe.source.getActivationItem().getItems()[0].copy();catalyst.setCount(1);return Charms.create(recipe.sourceId,recipe.source,catalyst);}
    public static void derivation(GameTestHelper h) {
        var list=CharmPlatform.sealings(h.getLevel());h.assertTrue(!list.isEmpty(),"Native summon and conversion recipes are derived during real recipe reload");
        for(var kind:CharmAdapter.Kind.values())h.assertTrue(list.stream().anyMatch(r->RitualCharms.definition(r.source).kind()==kind),"Derived family present: "+kind);
        for(var r:list) {
            h.assertTrue(r.getIngredients().size()==r.source.getIngredients().size()+1,"Original catalyst becomes an extra pedestal ingredient");
            h.assertTrue(r.getSoulCost()==r.source.getSoulCost()&&r.getDuration()==r.source.getDuration()&&r.getResearch().equals(r.source.getResearch()),"Native payment, duration and research preserved");
            h.assertTrue(!r.isSummoning()&&!r.isConversion()&&RitualCharms.adapter(SealingRecipe.id(r.sourceId),r)==null,"Sealing is not recursively derived and does not require a live target");
        }
        h.succeed();
    }
    public static void sealing(GameTestHelper h,ServerPlayer player,UnaryOperator<ItemStack> roundTrip) {
        var r=recipe(h,"summon_zombie_thrall");var pos=new BlockPos(2,1,2);h.setBlock(pos,ModBlocks.DARK_ALTAR.get().defaultBlockState());
        var altar=(DarkAltarBlockEntity)h.getBlockEntity(pos);altar.currentRitualRecipe=r;altar.currentRitualRecipeId=SealingRecipe.id(r.sourceId);
        for(var ingredient:r.getIngredients()){var s=ingredient.getItems()[0].copy();s.setCount(1);altar.consumedIngredients.add(s);}
        var blank=new ItemStack(CharmPlatform.blank());r.getRitual().finish(h.getLevel(),h.absolutePos(pos),altar,player,blank);
        var result=CharmPlatform.output(altar);var saved=roundTrip.apply(result);
        h.assertTrue(blank.isEmpty()&&saved.getItem()==CharmPlatform.charged(CharmAdapter.Kind.SUMMONING),"Native completion replaces one blank with a summoning charm");
        h.assertTrue(r.sourceId.equals(CharmPlatform.selection(saved))&&r.source.getActivationItem().test(CharmPlatform.catalyst(saved)),"Recipe ID and actual catalyst survive native save/network codecs");
        h.assertTrue(!CompletionScope.capturing(r)&&r.getEntityToSummon()==null,"Completion context never leaks after sealing");h.succeed();
    }
    public static void summon(GameTestHelper h,ServerPlayer player) {
        var r=recipe(h,"summon_zombie_thrall");var charm=charm(r);var pos=h.absolutePos(new BlockPos(5,1,5));
        h.assertTrue(CharmRelease.activate(charm,player,pos,null),"Native summoning can release from an unplaced completion context");
        h.assertTrue(charm.isEmpty(),"Successful survival release consumes one charm");
        var servants=h.getLevel().getEntitiesOfClass(Mob.class,new net.minecraft.world.phys.AABB(pos).inflate(3),m->m instanceof IOwned owned&&player.getUUID().equals(owned.getOwnerId()));
        h.assertTrue(!servants.isEmpty(),"Native owned servant initialization is preserved");servants.forEach(Entity::discard);h.succeed();
    }
    public static void conversion(GameTestHelper h,ServerPlayer player) {
        var r=recipe(h,"convert_blazes_to_wildfire");var charm=charm(r);var pos=h.absolutePos(new BlockPos(5,1,5));
        var wrong=EntityType.PIG.create(h.getLevel());wrong.moveTo(pos.getX(),pos.getY()+1,pos.getZ());h.getLevel().addFreshEntity(wrong);
        h.assertTrue(!CharmRelease.activate(charm,player,pos,wrong)&&charm.getCount()==1&&wrong.isAlive(),"Wrong conversion target does not consume the charm or creature");wrong.discard();
        var blaze=EntityType.BLAZE.create(h.getLevel());blaze.moveTo(pos.getX(),pos.getY()+1,pos.getZ());h.getLevel().addFreshEntity(blaze);
        h.assertTrue(CharmRelease.activate(charm,player,pos,blaze)&&charm.isEmpty(),"Native Blaze-to-Wildfire conversion succeeds");
        var results=h.getLevel().getEntitiesOfClass(Mob.class,new net.minecraft.world.phys.AABB(pos).inflate(3),m->m.getType()==r.source.getEntityToConvertInto());
        h.assertTrue(!results.isEmpty()&&results.get(0) instanceof IOwned owned&&player.getUUID().equals(owned.getOwnerId()),"Wildfire retains native owner initialization");results.forEach(Entity::discard);h.succeed();
    }
    public static void missing(GameTestHelper h,ServerPlayer player) {
        var charm=charm(recipe(h,"summon_zombie_thrall"));CharmPlatform.select(charm,net.minecraft.resources.ResourceLocation.tryParse("missing:ritual"));
        h.assertTrue(!CharmRelease.activate(charm,player,h.absolutePos(new BlockPos(5,1,5)),null)&&charm.getCount()==1,"Missing source recipe keeps the charm");h.succeed();
    }
    public static ItemStack boundWaystone(GameTestHelper h,ServerPlayer player,BlockPos pos) {
        var stone=recipe(h,"teleport_ritual").source.getActivationItem().getItems()[0].copy();stone.setCount(1);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stone);
        h.getLevel().setBlockAndUpdate(pos,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        var hit=new net.minecraft.world.phys.BlockHitResult(pos.getCenter().add(0,.5,0),net.minecraft.core.Direction.UP,pos,false);
        stone.useOn(new net.minecraft.world.item.context.UseOnContext(player,net.minecraft.world.InteractionHand.MAIN_HAND,hit));
        h.assertTrue(com.Polarice3.Goety.common.items.WaystoneItem.getPosition(stone)!=null,"Native item interaction binds the actual waystone");
        return stone;
    }
    public static void teleport(GameTestHelper h,ServerPlayer player,UnaryOperator<ItemStack> roundTrip) {
        var r=recipe(h,"teleport_ritual");var pos=h.absolutePos(new BlockPos(5,1,5));
        var unbound=charm(r);
        h.assertTrue(!CharmRelease.activate(unbound,player,player.blockPosition(),null)&&unbound.getCount()==1,"Unbound teleport cannot consume a charm");
        var stone=boundWaystone(h,player,pos);
        var charm=roundTrip.apply(Charms.create(r.sourceId,r.source,stone));
        h.assertTrue(com.Polarice3.Goety.common.items.WaystoneItem.getPosition(CharmPlatform.catalyst(charm)).pos().equals(pos),"Native destination survives item serialization");
        h.getLevel().setBlockAndUpdate(pos.above(),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        h.getLevel().setBlockAndUpdate(pos.above(2),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        h.assertTrue(CharmRelease.activate(charm,player,player.blockPosition(),null)&&charm.isEmpty(),"Native teleport completes and consumes one charm");
        h.assertTrue(player.position().distanceToSqr(new net.minecraft.world.phys.Vec3(pos.getX(),pos.getY()+1,pos.getZ()))<.000001,"Native waypoint coordinates are used");h.succeed();
    }
    public static void locationMap(GameTestHelper h,ServerPlayer player) {
        var r=recipe(h,"locate/ancient_city");var charm=charm(r);
        h.assertTrue(charm.getItem()==CharmPlatform.charged(CharmAdapter.Kind.LOCATION)
                &&r.source.getStructureName().equals(CharmPlatform.locationName(charm)),"Location charm retains native structure display metadata");
        var pos=h.absolutePos(new BlockPos(4,1,4));
        var context=new DarkAltarBlockEntity(pos,ModBlocks.DARK_ALTAR.get().defaultBlockState()) {
            @Override public com.Polarice3.Goety.common.crafting.RitualRecipe getCurrentRitualRecipe(){return r.source;}
        };
        context.setLevel(h.getLevel());context.castingPlayer=player;context.findStructurePos=pos;
        // Isolate native map completion from expensive world-generation searches.
        try(var scope=CompletionScope.release(context,null,false)) {
            h.assertTrue(r.source.getRitual().isValid(h.getLevel(),pos,context,player,CharmPlatform.catalyst(charm),java.util.List.of()),"Native validation accepts already-paid ingredients");
            RitualCharms.definition(r.source).finish(new CharmAdapter.Use(h.getLevel(),player,pos,null,r.source),context,CharmPlatform.catalyst(charm));
        }
        var result=CharmPlatform.output(context);
        h.assertTrue(result.is(net.minecraft.world.item.Items.FILLED_MAP)&&context.findStructurePos==null,"Native locate finish creates a real exploration map and clears its result");
        h.assertTrue(result.getHoverName().equals(net.minecraft.network.chat.Component.translatable(r.source.getStructureName())),"Native exploration map retains its localized structure name");
        h.assertTrue(!CompletionScope.paid(context),"Paid release scope is restored after native map completion");h.succeed();
    }
    private CharmCases(){ }
}
