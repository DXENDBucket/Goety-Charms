package dev.goetycharms.command;
import com.mojang.brigadier.CommandDispatcher;
import dev.goetycharms.api.RitualCharms;
import dev.goetycharms.item.Charms;
import dev.goetycharms.platform.CharmPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
public final class CharmCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("goety_charms").requires(s->s.hasPermission(2)).then(Commands.literal("give")
                .then(Commands.argument("player",EntityArgument.player()).then(Commands.argument("ritual",ResourceLocationArgument.id())
                .suggests((ctx,builder)->{for(var recipe:CharmPlatform.sealings(ctx.getSource().getLevel()))builder.suggest(recipe.sourceId.toString());return builder.buildFuture();})
                .executes(ctx->{var player=EntityArgument.getPlayer(ctx,"player");var id=ResourceLocationArgument.getId(ctx,"ritual");
                    var source=CharmPlatform.source(player.level(),id);
                    if(source==null||RitualCharms.adapter(id,source)==null){ctx.getSource().sendFailure(Component.translatable("message.goety_charms.missing_recipe"));return 0;}
                    var examples=source.getActivationItem().getItems();
                    if(examples.length==0){ctx.getSource().sendFailure(Component.translatable("message.goety_charms.invalid_charm"));return 0;}
                    var catalyst=examples[0].copy();catalyst.setCount(1);var charm=Charms.create(id,source,catalyst);
                    if(!player.addItem(charm))player.drop(charm,false);return 1;})))));
    }
    private CharmCommands(){ }
}
