package dev.goetycharms.api;

import dev.goetycharms.platform.CharmPlatform;
import dev.goetycharms.ritual.CharmRelease;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Public optional-integration boundary. Never exposes loader-specific item storage. */
public final class CharmAccess {
    /** Null for blank charms or unrelated items. Does not validate the current source recipe. */
    public static CharmAdapter.Kind kind(ItemStack stack) {
        if (stack.isEmpty()) return null;
        for (var kind : CharmAdapter.Kind.values()) if (stack.getItem() == CharmPlatform.charged(kind)) return kind;
        return null;
    }

    /** Identifies sealed inventory candidates; release still validates current recipe and target. */
    public static boolean sealed(ItemStack stack) {
        return kind(stack) != null && CharmPlatform.selection(stack) != null && !CharmPlatform.catalyst(stack).isEmpty();
    }

    /** Release an actual inventory stack. Success consumes one in survival; failure retains it. */
    public static boolean release(ItemStack stack, Player player, BlockPos origin, LivingEntity target) {
        return CharmRelease.activate(stack, player, origin, target);
    }

    private CharmAccess() { }
}
