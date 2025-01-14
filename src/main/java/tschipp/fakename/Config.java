package tschipp.fakename;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class Config {
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

			builder.pop();
		}

		private static boolean validateNameMapping(Object obj) {
			if (!(obj instanceof String mapping)) {
				return false;
			}
			String[] parts = mapping.split("=", 2);
			return parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty();
		}
	}
}