package tschipp.fakename.mixins;

import com.hypherionmc.craterlib.api.events.server.CraterPlayerEvent;
import com.hypherionmc.craterlib.api.events.server.CraterServerChatEvent;
import com.hypherionmc.craterlib.nojang.authlib.BridgedGameProfile;
import com.hypherionmc.craterlib.nojang.world.entity.player.BridgedPlayer;
import com.hypherionmc.sdlink.server.ServerEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import shadow.kyori.adventure.text.Component;

@Mixin(ServerEvents.class)
public class SDLinkServerEventsMixin {
    @ModifyVariable(
            method = "onServerChatEvent(Lcom/hypherionmc/craterlib/api/events/server/CraterServerChatEvent;)V",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private CraterServerChatEvent modifyEventWithFakeName(CraterServerChatEvent event) {
        BridgedPlayer player = event.getPlayer();
        ServerPlayer serverPlayer = player.toMojangServerPlayer();

        if (serverPlayer != null) {
            CompoundTag tag = serverPlayer.getPersistentData();
            if (tag != null && tag.contains("fakename")) {
                return new CraterServerChatEvent(
                        player,
                        event.getMessage(),
                        Component.text(tag.getString("fakename"))
                );
            }
        }

        return event;
    }

    @ModifyVariable(
            method = "playerJoinEvent(Lcom/hypherionmc/craterlib/api/events/server/CraterPlayerEvent$PlayerLoggedIn;)V",
            at = @At("HEAD"),
            ordinal = 1,
            remap = false
    )
    private String modifyJoinPlayerName(String playerName, CraterPlayerEvent.PlayerLoggedIn event) {
        ServerPlayer player = event.getPlayer().toMojangServerPlayer();
        if (player != null) {
            CompoundTag tag = player.getPersistentData();
            if (tag != null && tag.contains("fakename")) {
                return tag.getString("fakename");
            }
        }
        return playerName;
    }

    @ModifyVariable(
            method = "playerLeaveEvent(Lcom/hypherionmc/craterlib/api/events/server/CraterPlayerEvent$PlayerLoggedOut;)V",
            at = @At("HEAD"),
            ordinal = 1,
            remap = false
    )
    private String modifyLeavePlayerName(String playerName, CraterPlayerEvent.PlayerLoggedOut event) {
        ServerPlayer player = event.getPlayer().toMojangServerPlayer();
        if (player != null) {
            CompoundTag tag = player.getPersistentData();
            if (tag != null && tag.contains("fakename")) {
                return tag.getString("fakename");
            }
        }
        return playerName;
    }
}