package gytixss.goat;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;

public class SingleplayerBoat implements ModInitializer {
	public static final String MOD_ID = "singleplayer-boat";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Holds whatever type /boat (with no argument) currently spawns
	private static EntityType<? extends AbstractBoat> defaultBoatType = EntityType.OAK_BOAT;

	// Maps command text like "cherry" or "oak_chest" to the actual boat entity type
	private static final Map<String, EntityType<? extends AbstractBoat>> BOAT_TYPES = Map.ofEntries(
			Map.entry("oak", EntityType.OAK_BOAT),
			Map.entry("spruce", EntityType.SPRUCE_BOAT),
			Map.entry("birch", EntityType.BIRCH_BOAT),
			Map.entry("jungle", EntityType.JUNGLE_BOAT),
			Map.entry("acacia", EntityType.ACACIA_BOAT),
			Map.entry("dark_oak", EntityType.DARK_OAK_BOAT),
			Map.entry("mangrove", EntityType.MANGROVE_BOAT),
			Map.entry("cherry", EntityType.CHERRY_BOAT),
			Map.entry("pale_oak", EntityType.PALE_OAK_BOAT),
			Map.entry("bamboo", EntityType.BAMBOO_RAFT),
			Map.entry("oak_chest", EntityType.OAK_CHEST_BOAT),
			Map.entry("spruce_chest", EntityType.SPRUCE_CHEST_BOAT),
			Map.entry("birch_chest", EntityType.BIRCH_CHEST_BOAT),
			Map.entry("jungle_chest", EntityType.JUNGLE_CHEST_BOAT),
			Map.entry("acacia_chest", EntityType.ACACIA_CHEST_BOAT),
			Map.entry("dark_oak_chest", EntityType.DARK_OAK_CHEST_BOAT),
			Map.entry("mangrove_chest", EntityType.MANGROVE_CHEST_BOAT),
			Map.entry("cherry_chest", EntityType.CHERRY_CHEST_BOAT),
			Map.entry("pale_oak_chest", EntityType.PALE_OAK_CHEST_BOAT),
			Map.entry("bamboo_chest", EntityType.BAMBOO_CHEST_RAFT)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// This is the part that actually registers the commands with Minecraft
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(buildBoatCommand("boat"));
			dispatcher.register(buildBoatCommand("b"));
		});
	}

	// Builds the whole /boat command tree (and its "default" and <type> branches),
	// reused for both "boat" and "b" so we don't write this twice
	private static LiteralArgumentBuilder<CommandSourceStack> buildBoatCommand(String name) {
		return Commands.literal(name)
				.executes(context -> spawnBoat(context, defaultBoatType))
				.then(Commands.literal("default")
						.then(Commands.argument("type", StringArgumentType.word())
								.suggests((context, builder) -> {
									BOAT_TYPES.keySet().forEach(builder::suggest);
									return builder.buildFuture();
								})
								.executes(context -> {
									String type = StringArgumentType.getString(context, "type").toLowerCase();
									EntityType<? extends AbstractBoat> boatType = BOAT_TYPES.get(type);
									if (boatType == null) {
										context.getSource().sendFailure(Component.literal("Unknown boat type: " + type));
										return 0;
									}
									defaultBoatType = boatType;
									context.getSource().sendSuccess(() -> Component.literal("Default boat type set to " + type), false);
									return 1;
								})))
				.then(Commands.argument("type", StringArgumentType.word())
						.suggests((context, builder) -> {
							BOAT_TYPES.keySet().forEach(builder::suggest);
							return builder.buildFuture();
						})
						.executes(context -> {
							String type = StringArgumentType.getString(context, "type").toLowerCase();
							EntityType<? extends AbstractBoat> boatType = BOAT_TYPES.getOrDefault(type, EntityType.OAK_BOAT);
							return spawnBoat(context, boatType);
						}));
	}

	// The actual "spawn a boat and put the player in it" logic, shared by every command path
	private static int spawnBoat(CommandContext<CommandSourceStack> context, EntityType<? extends AbstractBoat> boatType) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		ServerPlayer player = source.getPlayerOrException();
		ServerLevel world = source.getLevel();

		AbstractBoat boat = boatType.create(world, EntitySpawnReason.COMMAND);
		if (boat != null) {
			boat.setPos(player.getX(), player.getY(), player.getZ());
			boat.setYRot(player.getYRot());
			boat.yRotO = player.getYRot();
			world.addFreshEntity(boat);
			player.startRiding(boat);
			source.sendSuccess(() -> Component.literal("Boat spawned!"), false);
		}

		return 1;
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}