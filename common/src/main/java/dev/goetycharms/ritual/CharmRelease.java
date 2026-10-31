package dev.goetycharms.ritual;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.SEHelper;
import dev.goetycharms.api.CharmAdapter;
import dev.goetycharms.api.RitualCharms;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

/** Reusable paid-ritual release entry. Item use supplies real hit geometry; future spell integration may do the same. */
public final class CharmRelease {
    public static boolean activate(ItemStack stack,Player player,BlockPos origin,LivingEntity target) {
        if(!(player.level() instanceof ServerLevel world)||!CharmPlatform.enabled())return false;
        var id=CharmPlatform.selection(stack);var source=id==null?null:CharmPlatform.source(world,id);
        var adapter=source==null?null:RitualCharms.adapter(id,source);
        if(adapter==null||stack.getItem()!=CharmPlatform.charged(adapter.kind()))return fail(player,"missing_recipe");
        var catalyst=CharmPlatform.catalyst(stack);
        if(catalyst.isEmpty()||!source.getActivationItem().test(catalyst))return fail(player,"invalid_charm");
        if(adapter.kind()==CharmAdapter.Kind.TELEPORTATION)return UtilityRelease.teleport(stack,player,source,adapter,catalyst);
        if(adapter.kind()==CharmAdapter.Kind.LOCATION)return UtilityRelease.locate(stack,player,source,adapter,catalyst);
        if(!world.hasChunkAt(origin)||!world.mayInteract(player,origin)||!world.getWorldBorder().isWithinBounds(origin)
                ||origin.getY()<world.getMinBuildHeight()||origin.getY()+2>=world.getMaxBuildHeight())return fail(player,"unsafe");
        if(source.getEntityToSummon()==ModEntityType.SUMMON_APOSTLE.get()&&world.getDifficulty()==Difficulty.PEACEFUL)return fail(player,"peaceful");
        boolean conversion=adapter.kind()==CharmAdapter.Kind.CONVERSION;
        if(conversion&&(target==null||!(target instanceof Mob)||!target.isAlive()||target.isRemoved()
                ||target.level()!=world||target==player||!target.getType().is(source.getEntityToConvert())))return fail(player,"wrong_target");
        var type=conversion?source.getEntityToConvertInto():source.getEntityToSummon();
        if(!RitualRequirements.canSummon(world,player,type))return false;
        if(!conversion) {
            var probe=type.create(world);
            if(probe==null)return fail(player,"unsafe");
            var pos=origin.getCenter().add(0,probe instanceof LivingEntity?.5:0,0);probe.setPos(pos.x,pos.y,pos.z);
            if(!world.hasChunksAt(BlockPos.containing(probe.getBoundingBox().minX,probe.getBoundingBox().minY,probe.getBoundingBox().minZ),
                    BlockPos.containing(probe.getBoundingBox().maxX,probe.getBoundingBox().maxY,probe.getBoundingBox().maxZ))
                    ||!world.noCollision(probe,probe.getBoundingBox())||!world.getWorldBorder().isWithinBounds(probe.getBoundingBox()))return fail(player,"unsafe");
        }
        var use=new CharmAdapter.Use(world,player,origin,target,source);
        if(!adapter.canRelease(use))return false;
        if(source.getEntityToSummon()==ModEntityType.SUMMON_APOSTLE.get()&&world.dimension()==Level.NETHER) {
            if(!SEHelper.apostleWarned(player)) {
                SEHelper.setApostleWarned(player,true);player.displayClientMessage(Component.translatable("info.goety.ritual.apostleWarn"),true);return false;
            }
            SEHelper.setApostleWarned(player,false);
        }
        // A non-ticking, unplaced context supplies the native finish method with its normal
        // target and recipe. It is never inserted into the world or used as an inventory device.
        var context=context(world,origin,player,source);context.getConvertEntity=conversion?(Mob)target:null;
        boolean success;
        try(var scope=CompletionScope.release(context,type,!conversion)) {
            adapter.finish(use,context,catalyst.copy());
            success=scope.spawned();
        }
        if(!success)return fail(player,"blocked");
        if(!player.getAbilities().instabuild)stack.shrink(1);
        return true;
    }
    public static void fromHand(ItemStack stack,Player player) {
        var from=player.getEyePosition();var end=from.add(player.getLookAngle().scale(6));
        var block=player.level().clip(new ClipContext(from,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));
        var limit=block.getType()==HitResult.Type.MISS?end:block.getLocation();
        var hit=ProjectileUtil.getEntityHitResult(player.level(),player,from,limit,new AABB(from,limit).inflate(1),e->e instanceof LivingEntity&&e.isAlive()&&!e.isSpectator(),0);
        LivingEntity target=hit!=null&&hit.getEntity() instanceof LivingEntity living?living:null;
        var origin=block.getType()!=HitResult.Type.MISS?block.getBlockPos().relative(block.getDirection())
                :BlockPos.containing(player.position().add(player.getLookAngle().x*2,0,player.getLookAngle().z*2));
        if(target!=null)origin=target.blockPosition();
        activate(stack,player,origin,target);
    }
    static DarkAltarBlockEntity context(ServerLevel world,BlockPos pos,Player player,RitualRecipe source) {
        var context=new DarkAltarBlockEntity(pos,com.Polarice3.Goety.common.blocks.ModBlocks.DARK_ALTAR.get().defaultBlockState()) {
            @Override public RitualRecipe getCurrentRitualRecipe(){return source;}
        };
        context.setLevel(world);context.castingPlayer=player;return context;
    }
    static boolean fail(Player player,String key){player.displayClientMessage(Component.translatable("message.goety_charms."+key),true);return false;}
    private CharmRelease() { }
}
