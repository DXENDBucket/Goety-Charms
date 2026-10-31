package dev.goetycharms.item;

import dev.goetycharms.api.CharmAdapter;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class CharmTooltips {
    public static void append(ItemStack stack, Player player, List<Component> lines, boolean advanced) {
        if (!(stack.getItem() instanceof CharmItem)) return;
        if (stack.getItem() == CharmPlatform.blank()) {
            lines.add(Component.translatable("tooltip.goety_charms.blank"));
        } else {
            boolean conversion = stack.getItem() == CharmPlatform.charged(CharmAdapter.Kind.CONVERSION);
            lines.add(Component.translatable(CharmPlatform.catalyst(stack).isEmpty()
                    ? "tooltip.goety_charms.preview"
                    : conversion ? "tooltip.goety_charms.use_conversion" : "tooltip.goety_charms.use_summoning"));
            if (conversion) {
                // Prefer current recipe data; the snapshot also works for playerless JEI previews.
                String target = CharmPlatform.conversionTarget(stack);
                var selected = CharmPlatform.selection(stack);
                if (player != null && selected != null) {
                    var recipe = CharmPlatform.source(player.level(), selected);
                    if (recipe != null) target = recipe.getEntityToConvertDisplayName();
                }
                if (target != null && !target.isBlank()) {
                    lines.add(Component.translatable("tooltip.goety_charms.conversion_target", Component.translatable(target)));
                }
            }
        }
        var selected = CharmPlatform.selection(stack);
        if (selected != null && advanced) lines.add(Component.literal(selected.toString()).withStyle(ChatFormatting.DARK_GRAY));
    }

    private CharmTooltips() { }
}
