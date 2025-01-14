package tschipp.fakename.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import snownee.jade.addon.vanilla.AnimalOwnerProvider;
import snownee.jade.api.EntityAccessor;

@Mixin(AnimalOwnerProvider.class)
public class AnimalOwnerProviderMixin {

    @ModifyVariable(method = "appendServerData", at = @At(value = "STORE", ordinal = 0), ordinal = 0,remap = false)
    private String modifyOwnerName(String name, CompoundTag data, EntityAccessor accessor) {
        if (name == null) return null;

        for (Player player : accessor.getLevel().players()) {
            if (player.getGameProfile().getName().equals(name)) {
                CompoundTag tag = player.getPersistentData();
                if (tag != null && tag.contains("fakename")) {
                    return tag.getString("fakename");
                }
                break;
            }
        }
        return name;
    }
}