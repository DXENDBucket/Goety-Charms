package dev.goetycharms;
import net.minecraft.gametest.framework.*;
import net.minecraftforge.gametest.*;
@GameTestHolder("goety_charms") @PrefixGameTestTemplate(false)
public final class CharmGameTests {
    @GameTest(template="empty") public static void nativeRecipeDerivation(GameTestHelper h){CharmCases.derivation(h);}
    @GameTest(template="empty") public static void sealedDataAndNativeNetwork(GameTestHelper h){CharmCases.sealing(h,TestPlayers.player(h),s->{var saved=net.minecraft.world.item.ItemStack.of(s.save(new net.minecraft.nbt.CompoundTag()));var buf=new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());try{buf.writeItem(saved);return buf.readItem();}finally{buf.release();}});}
    @GameTest(template="empty") public static void nativeSummonOwnership(GameTestHelper h){CharmCases.summon(h,TestPlayers.player(h));}
    @GameTest(template="empty") public static void nativeConversionTarget(GameTestHelper h){CharmCases.conversion(h,TestPlayers.player(h));}
    @GameTest(template="empty") public static void missingRecipeKeepsCharm(GameTestHelper h){CharmCases.missing(h,TestPlayers.player(h));}
    @GameTest(template="empty") public static void derivedRecipeNetwork(GameTestHelper h) {
        var original=CharmCases.recipe(h,"summon_zombie_thrall");var buf=new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        try {dev.goetycharms.platform.CharmPlatform.serializer().toNetwork(buf,original);
            var decoded=dev.goetycharms.platform.CharmPlatform.serializer().fromNetwork(original.getId(),buf);
            h.assertTrue(original.sourceId.equals(decoded.sourceId)&&decoded.getIngredients().size()==original.getIngredients().size()
                &&decoded.getSoulCost()==original.getSoulCost(),"Derived ritual and original native source survive client recipe synchronization");
        }finally{buf.release();}h.succeed();
    }
}
