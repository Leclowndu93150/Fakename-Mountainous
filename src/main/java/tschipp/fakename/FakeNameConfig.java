package tschipp.fakename;

import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class FakeNameConfig {
    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {
        public final ForgeConfigSpec.IntValue commandPermissionLevelSelf;
        public final ForgeConfigSpec.IntValue commandPermissionLevelAll;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> autoNameMappings;
        public final ForgeConfigSpec.BooleanValue showDimension;
        public final ForgeConfigSpec.ConfigValue<String> dimensionFormat;
        public final ForgeConfigSpec.EnumValue<ChatFormatting> overworldColor;
        public final ForgeConfigSpec.EnumValue<ChatFormatting> netherColor;
        public final ForgeConfigSpec.EnumValue<ChatFormatting> endColor;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionAliases;

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("settings");

            commandPermissionLevelAll = builder
                    .comment("Permission Level of the command. This is the level needed to be able to change other people's fakename")
                    .defineInRange("commandPermissionLevelAll", 2, 0, 10);

            commandPermissionLevelSelf = builder
                    .comment("Permission Level of the command. This is the level needed to be able to change your own fakename")
                    .defineInRange("commandPermissionLevelSelf", 0, 0, 10);

            autoNameMappings = builder
                    .comment("Automatically assign fake names to players based on their username. Format: username=fakename")
                    .defineListAllowEmpty(List.of("autoNameMappings"),
                            () -> Arrays.asList("Notch=Markus Persson"),
                            obj -> obj instanceof String && ((String)obj).contains("="));

            showDimension = builder
                    .comment("Show dimension next to player names")
                    .define("showDimension", true);

            dimensionFormat = builder
                    .comment("Format for dimension display. Use %n for name, %d for dimension")
                    .define("dimensionFormat", "%n §7[%d§7]");

            overworldColor = builder
                    .comment("Color for Overworld dimension text")
                    .defineEnum("overworldColor", ChatFormatting.GREEN);

            netherColor = builder
                    .comment("Color for Nether dimension text")
                    .defineEnum("netherColor", ChatFormatting.RED);

            endColor = builder
                    .comment("Color for End dimension text")
                    .defineEnum("endColor", ChatFormatting.LIGHT_PURPLE);

            dimensionAliases = builder
                    .comment("Aliases for dimension names. Format: dimension_id=alias")
                    .defineList("dimensionAliases",
                            () -> Arrays.asList(
                                    "minecraft:overworld=Overworld",
                                    "minecraft:the_nether=Nether",
                                    "minecraft:the_end=End"
                            ),
                            obj -> obj instanceof String && ((String)obj).contains("="));

            builder.pop();
        }
    }
}