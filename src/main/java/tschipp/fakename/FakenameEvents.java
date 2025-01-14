package tschipp.fakename;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FakenameEvents {

    @SubscribeEvent
    public static void serverLoad(RegisterCommandsEvent event) {
        CommandFakeName.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void renderName(PlayerEvent.NameFormat event) {
        CompoundTag tag = event.getEntity().getPersistentData();
        if (tag.contains("fakename")) {
            event.setDisplayname(Component.literal(tag.getString("fakename")));
        } else {
            event.setDisplayname(event.getUsername());
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.getCommandSenderWorld().isClientSide) {
            if (player.getPersistentData().contains("fakename")) {
                FakeName.sendPacket(player, player.getPersistentData().getString("fakename"), 0);
            } else {
                String playerName = player.getGameProfile().getName();
                for (String mapping : Config.SERVER.autoNameMappings.get()) {
                    String[] parts = mapping.split("=", 2);
                    if (parts.length == 2 && parts[0].trim().equals(playerName)) {

                        String fakename = parts[1].trim().replace("&", "\u00a7") + "\u00a7r";
                        player.getPersistentData().putString("fakename", fakename);
                        FakeName.sendPacket(player, fakename, 0);
                        break;
                    }
                }
            }

            for (Player other : player.getServer().getPlayerList().getPlayers()) {
                if (other.getPersistentData().contains("fakename")) {
                    FakeName.network.send(
                            PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new FakeNamePacket(other.getPersistentData().getString("fakename"),
                                    other.getId(), 0)
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player) {
            Player targetPlayer = (Player) event.getTarget();
            if (targetPlayer.getPersistentData() != null &&
                    targetPlayer.getPersistentData().contains("fakename")) {
                ServerPlayer toReceive = (ServerPlayer) event.getEntity();
                FakeName.network.send(
                        PacketDistributor.PLAYER.with(() -> toReceive),
                        new FakeNamePacket(targetPlayer.getPersistentData().getString("fakename"),
                                targetPlayer.getId(), 0)
                );
            }
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        if (oldPlayer.getPersistentData().contains("fakename")) {
            String fakename = oldPlayer.getPersistentData().getString("fakename");
            newPlayer.getPersistentData().putString("fakename", fakename);
        }
    }
}