package dev.goetycharms;
import net.minecraft.gametest.framework.*;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder("goety_charms") @PrefixGameTestTemplate(false)
public final class CharmGameTests {
    @GameTest(template="empty") public static void nativeTeleportDestination(GameTestHelper h){CharmCases.teleport(h,TestPlayers.player(h),s->net.minecraft.world.item.ItemStack.parse(h.getLevel().registryAccess(),s.save(h.getLevel().registryAccess())).orElseThrow());}
    @GameTest(template="empty") public static void nativeLocationMap(GameTestHelper h){CharmCases.locationMap(h,TestPlayers.player(h));}
    @GameTest(template="empty") public static void nativeRecipeDerivation(GameTestHelper h){CharmCases.derivation(h);}
    @GameTest(template="empty") public static void sealedDataAndNativeNetwork(GameTestHelper h){CharmCases.sealing(h,TestPlayers.player(h),s->{var saved=net.minecraft.world.item.ItemStack.parse(h.getLevel().registryAccess(),s.save(h.getLevel().registryAccess())).orElseThrow();
        var buf=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());try{net.minecraft.world.item.ItemStack.STREAM_CODEC.encode(buf,saved);return net.minecraft.world.item.ItemStack.STREAM_CODEC.decode(buf);}finally{buf.release();}});}
    @GameTest(template="empty") public static void nativeSummonOwnership(GameTestHelper h){CharmCases.summon(h,TestPlayers.player(h));}
    @GameTest(template="empty") public static void nativeConversionTarget(GameTestHelper h){CharmCases.conversion(h,TestPlayers.player(h));}
    @GameTest(template="empty") public static void missingRecipeKeepsCharm(GameTestHelper h){CharmCases.missing(h,TestPlayers.player(h));}
    @GameTest(template="empty") public static void derivedRecipeNetwork(GameTestHelper h) {
        var original=CharmCases.recipe(h,"summon_zombie_thrall");
        var buf=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());
        try {dev.goetycharms.platform.CharmPlatform.serializer().streamCodec().encode(buf,original);
            var decoded=dev.goetycharms.platform.CharmPlatform.serializer().streamCodec().decode(buf);
            h.assertTrue(original.sourceId.equals(decoded.sourceId)&&decoded.getIngredients().size()==original.getIngredients().size()
                &&decoded.getSoulCost()==original.getSoulCost(),"Derived ritual and original native source survive client recipe synchronization");
        }finally{buf.release();}h.succeed();
    }
}
