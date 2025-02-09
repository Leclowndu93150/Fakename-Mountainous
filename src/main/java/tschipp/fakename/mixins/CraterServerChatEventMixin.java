package tschipp.fakename.mixins;

import com.hypherionmc.craterlib.api.events.server.CraterServerChatEvent;
import com.hypherionmc.craterlib.nojang.world.entity.player.BridgedPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraterServerChatEvent.class)
public class CraterServerChatEventMixin {
    @Mutable
    @Shadow @Final public String username;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectFakeName(BridgedPlayer player, String message, shadow.kyori.adventure.text.Component component, CallbackInfo ci) {
        ServerPlayer serverPlayer = player.toMojangServerPlayer();
        if (serverPlayer != null) {
            CompoundTag tag = serverPlayer.getPersistentData();
            if (tag != null && tag.contains("fakename")) {
                this.username = tag.getString("fakename");
            }
        }
    }
}
