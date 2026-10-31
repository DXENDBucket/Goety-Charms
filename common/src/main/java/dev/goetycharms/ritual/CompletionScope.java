package dev.goetycharms.ritual;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import net.minecraft.world.entity.Entity;
import java.util.ArrayList;
import java.util.List;
/** One thread-local completion boundary: native ritual hooks are paid/performed at sealing only. */
public final class CompletionScope implements AutoCloseable {
    private static final ThreadLocal<CompletionScope> CURRENT=new ThreadLocal<>();
    public final SealingRecipe sealing;
    public final DarkAltarBlockEntity releasing;
    private final CompletionScope previous;
    private final net.minecraft.world.entity.EntityType<?> expected;
    private final boolean livingVariants;
    private final List<Entity> candidates=new ArrayList<>();
    private CompletionScope(SealingRecipe sealing,DarkAltarBlockEntity releasing,net.minecraft.world.entity.EntityType<?> expected,boolean livingVariants) {
        this.expected=expected;this.livingVariants=livingVariants;
        this.sealing=sealing;this.releasing=releasing;previous=CURRENT.get();CURRENT.set(this);
    }
    public static CompletionScope capture(SealingRecipe recipe){return new CompletionScope(recipe,null,null,false);}
    public static CompletionScope release(DarkAltarBlockEntity tile,net.minecraft.world.entity.EntityType<?> expected,boolean variants){return new CompletionScope(null,tile,expected,variants);}
    public static boolean capturing(SealingRecipe recipe){var s=CURRENT.get();return s!=null&&s.sealing==recipe;}
    public static boolean paid(DarkAltarBlockEntity tile){var s=CURRENT.get();return s!=null&&s.releasing==tile;}
    public static boolean paidIngredients(net.minecraft.world.level.Level world,net.minecraft.core.BlockPos pos,net.minecraft.world.entity.player.Player player) {
        var s=CURRENT.get();return s!=null&&s.releasing!=null&&s.releasing.getLevel()==world
                &&s.releasing.getBlockPos().equals(pos)&&s.releasing.castingPlayer==player;
    }
    public static void observe(Entity entity){var s=CURRENT.get();if(s!=null&&s.releasing!=null&&s.releasing.getLevel()==entity.level()
            &&(entity.getType()==s.expected||s.livingVariants&&entity instanceof net.minecraft.world.entity.LivingEntity))s.candidates.add(entity);}
    public boolean spawned(){return candidates.stream().anyMatch(e->!e.isRemoved()&&e.level().getEntity(e.getId())==e);}
    @Override public void close(){if(previous==null)CURRENT.remove();else CURRENT.set(previous);}
}
