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
        h.assertTrue(list.stream().anyMatch(r->RitualCharms.definition(r.source).kind()==CharmAdapter.Kind.CONVERSION)
                &&list.stream().anyMatch(r->RitualCharms.definition(r.source).kind()==CharmAdapter.Kind.SUMMONING),"Both families are present");
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
    private CharmCases(){ }
}
