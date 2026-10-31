package dev.goetycharms;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
public final class TestPlayers {
    static ServerPlayer player(GameTestHelper h){
        var player=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"CharmStudent"));
        var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(connection);
        player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(h.getLevel().getServer(),connection,player){@Override public void send(net.minecraft.network.protocol.Packet<?> packet){} };
        player.setPos(h.absoluteVec(new net.minecraft.world.phys.Vec3(1,2,1)));player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);return player;
    }
}
