package tschipp.fakename;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class FakeNamePacket
{
    public String fakename;
    public int entityId;
    public int deleteFakename;

    public FakeNamePacket(FriendlyByteBuf buf)
    {
        this.fakename = buf.readUtf();
        this.entityId = buf.readInt();
        this.deleteFakename = buf.readInt();
    }

    public FakeNamePacket()
    {

    }

    public FakeNamePacket(String fakename, int entityID, int delete)
    {
        this.fakename = fakename;
        this.entityId = entityID;
        this.deleteFakename = delete;
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeUtf(fakename);
        buf.writeInt(entityId);
        buf.writeInt(deleteFakename);
    }

    public void handle(Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Player toSync = (Player) mc.level.getEntity(entityId);

            if (toSync != null) {
                ctx.get().setPacketHandled(true);
                FakeName.performFakenameOperation(toSync, fakename, deleteFakename);

                ClientPacketListener connection = mc.player.connection;
                PlayerInfo playerInfo = connection.getPlayerInfo(toSync.getGameProfile().getId());
                if (playerInfo != null) {
                    if (deleteFakename == 0) {
                        playerInfo.setTabListDisplayName(Component.literal(
                                FakeName.formatNameWithDimension(toSync, fakename)
                        ));
                    } else {
                        playerInfo.setTabListDisplayName(Component.literal(toSync.getGameProfile().getName()));
                    }
                }
            }
        });
    }

}
