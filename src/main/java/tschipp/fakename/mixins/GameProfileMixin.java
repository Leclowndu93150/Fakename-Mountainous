package tschipp.fakename.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameProfile.class)
public class GameProfileMixin {
    @Inject(method = "getName()Ljava/lang/String;", at = @At("RETURN"), cancellable = true, remap = false)
    private void injectFakeName(CallbackInfoReturnable<String> cir) {
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(cir.getReturnValue());
        if (player != null) {
            CompoundTag tag = player.getPersistentData();
            if (tag != null && tag.contains("fakename")) {
                cir.setReturnValue(tag.getString("fakename"));
            }
        }
    }
}