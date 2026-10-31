package dev.goetycharms.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.WaystoneItem;
import dev.goetycharms.api.CharmAdapter;
import dev.goetycharms.api.RitualCharms;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Native non-entity results. Pending searches never take ownership of the player's item. */
public final class UtilityRelease {
    private static final Map<UUID, Pending> SEARCHES = new HashMap<>();
    private static final long SEARCH_TIMEOUT_TICKS = 20 * 120;

    public static boolean teleport(ItemStack stack, Player player, RitualRecipe source, CharmAdapter adapter, ItemStack catalyst) {
        var destination = WaystoneItem.getPosition(catalyst);
        if (!(catalyst.getItem() instanceof WaystoneItem) || destination == null)
            return CharmRelease.fail(player, "unbound_destination");
        var world = (ServerLevel) player.level();
        if (!destination.dimension().equals(world.dimension())) return CharmRelease.fail(player, "wrong_dimension");
        var landing = new Vec3(destination.pos().getX(), destination.pos().getY() + 1, destination.pos().getZ());
        var use = new CharmAdapter.Use(world, player, player.blockPosition(), null, source);
        if (!adapter.canRelease(use)) return false;
        var context = CharmRelease.context(world, use.origin(), player, source);
        try (var scope = CompletionScope.release(context, null, false)) {
            adapter.finish(use, context, catalyst.copy());
        }
        if (player.level() != world || player.position().distanceToSqr(landing) > 0.000001)
            return CharmRelease.fail(player, "blocked");
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return true;
    }

    /** True means the search was accepted; consumption happens only when its map is delivered. */
    public static boolean locate(ItemStack stack, Player player, RitualRecipe source, CharmAdapter adapter, ItemStack catalyst) {
        if (SEARCHES.containsKey(player.getUUID())) return CharmRelease.fail(player, "search_busy");
        if (!carried(player, stack)) return CharmRelease.fail(player, "invalid_charm");
        var world = (ServerLevel) player.level();
        var use = new CharmAdapter.Use(world, player, player.blockPosition(), null, source);
        if (!adapter.canRelease(use)) return false;
        var pending = new Pending(stack, stack.copy(), catalyst.copy(), use, adapter,
                CharmRelease.context(world, use.origin(), player, source), world.getGameTime());
        SEARCHES.put(player.getUUID(), pending);
        player.displayClientMessage(Component.translatable("message.goety_charms.search_started"), true);
        // isValid performs Goety's own asynchronous structure search; no duplicate search algorithm.
        advance(player.getUUID(), pending);
        return true;
    }

    public static void tick(MinecraftServer server) {
        if (SEARCHES.isEmpty() || server.getTickCount() % 5 != 0) return;
        for (var entry : List.copyOf(SEARCHES.entrySet())) advance(entry.getKey(), entry.getValue());
    }

    private static void advance(UUID id, Pending pending) {
        var use = pending.use;
        var player = use.player();
        var selected = CharmPlatform.selection(pending.stack);
        var current = selected == null ? null : CharmPlatform.source(use.world(), selected);
        if (!player.isAlive() || player.isRemoved() || player.level() != use.world()
                || !carried(player, pending.stack) || !CharmPlatform.sameCharm(pending.snapshot, pending.stack)
                || !CharmPlatform.enabled() || current != use.recipe()
                || RitualCharms.adapter(selected, current) == null) {
            cancel(id);
            return;
        }
        if (use.world().getGameTime() - pending.started > SEARCH_TIMEOUT_TICKS) {
            cancel(id);
            CharmRelease.fail(player, "search_timeout");
            return;
        }
        try {
            if (pending.context.pendingStructureSearch != null && !pending.context.pendingStructureSearch.isDone()) return;
            try (var scope = CompletionScope.release(pending.context, null, false)) {
                if (!use.recipe().getRitual().isValid(use.world(), use.origin(), pending.context, player, pending.catalyst, List.of())) {
                    cancel(id);
                    return;
                }
            }
            if (pending.context.findStructurePos == null) return;
            SEARCHES.remove(id);
            try (var scope = CompletionScope.release(pending.context, null, false)) {
                pending.adapter.finish(use, pending.context, pending.catalyst.copy());
            }
            var result = CharmPlatform.output(pending.context).copy();
            CharmPlatform.output(pending.context, ItemStack.EMPTY);
            if (result.isEmpty()) {
                CharmRelease.fail(player, "blocked");
                return;
            }
            if (!player.getAbilities().instabuild) pending.stack.shrink(1);
            if (!player.getInventory().add(result)) player.drop(result, false);
            player.getInventory().setChanged();
            player.displayClientMessage(Component.translatable("message.goety_charms.search_complete"), true);
        } catch (java.util.concurrent.CompletionException | java.util.concurrent.CancellationException exception) {
            cancel(id);
            CharmRelease.fail(player, "search_failed");
        }
    }

    private static boolean carried(Player player, ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
            if (player.getInventory().getItem(slot) == stack) return true;
        return false;
    }

    public static void cancel(UUID player) {
        var pending = SEARCHES.remove(player);
        if (pending != null && pending.context.pendingStructureSearch != null) {
            pending.context.pendingStructureSearch.cancel(true);
            pending.context.pendingStructureSearch = null;
        }
    }

    public static void clear() {
        for (var id : List.copyOf(SEARCHES.keySet())) cancel(id);
    }

    private record Pending(ItemStack stack, ItemStack snapshot, ItemStack catalyst, CharmAdapter.Use use,
                           CharmAdapter adapter, DarkAltarBlockEntity context, long started) { }

    private UtilityRelease() { }
}
