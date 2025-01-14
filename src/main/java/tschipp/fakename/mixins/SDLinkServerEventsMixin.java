package tschipp.fakename.mixins;

import com.hypherionmc.craterlib.api.events.server.CraterServerChatEvent;
import com.hypherionmc.craterlib.nojang.authlib.BridgedGameProfile;
import com.hypherionmc.craterlib.nojang.server.BridgedMinecraftServer;
import com.hypherionmc.craterlib.nojang.world.entity.player.BridgedPlayer;
import com.hypherionmc.sdlink.server.ServerEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import shadow.kyori.adventure.text.Component;
import snownee.jade.impl.EntityAccessorImpl;

@Mixin(ServerEvents.class)
public class SDLinkServerEventsMixin {

    @ModifyVariable(
            method = "onServerChatEvent(Lcom/hypherionmc/craterlib/api/events/server/CraterServerChatEvent;)V",
            at = @At(value = "HEAD"),
            argsOnly = true,
            remap = false
    )
    private CraterServerChatEvent modifyEventDisplayName(CraterServerChatEvent event) {
        BridgedPlayer bridgedPlayer = event.getPlayer();
        ServerPlayer player = bridgedPlayer.toMojangServerPlayer();

        if (player != null) {
            CompoundTag tag = player.getPersistentData();
            if (tag != null && tag.contains("fakename")) {

                String fakeName = tag.getString("fakename");
                return new CraterServerChatEvent(
                        bridgedPlayer,
                        event.getMessage(),
                        Component.text(fakeName)
                );
            }
        }
        return event;
    }

    @ModifyVariable(
            method = "onServerChatEvent(Lshadow/kyori/adventure/text/Component;Lshadow/kyori/adventure/text/Component;Ljava/lang/String;Lcom/hypherionmc/craterlib/nojang/authlib/BridgedGameProfile;Z)V",
            at = @At("HEAD"),
            ordinal = 1,
            remap = false
    )
    private Component modifyProcessedUsername(Component username, Component message, Component originalUsername,
                                                     String uuid, BridgedGameProfile profile, boolean fromServer) {
        if (fromServer) return username;

        ServerEvents events = ServerEvents.getInstance();
        BridgedMinecraftServer server = events.getMinecraftServer();

        for (BridgedPlayer bridgedPlayer : server.getPlayers()) {
            if (bridgedPlayer.getGameProfile().getId().equals(profile.getId())) {
                ServerPlayer player = bridgedPlayer.toMojangServerPlayer();
                if (player != null) {
                    CompoundTag tag = player.getPersistentData();
                    if (tag != null && tag.contains("fakename")) {
                        return Component.text(tag.getString("fakename"));
                    }
                }
                break;
            }
        }
        return username;
    }
}
