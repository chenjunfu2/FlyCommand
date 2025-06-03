package com.chenjunfu2;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.UUID;

public class Flycommand implements ModInitializer
{
	public static HashSet<UUID> flyPlayerList = new HashSet<>();
	
	public void onInitialize()
	{
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
		{
			flyCMD(dispatcher);
		});
	}
	
	private void flyCMD(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		LiteralArgumentBuilder<ServerCommandSource> flyCommand =
			CommandManager.literal("fly")
				.requires(ServerCommandSource::isExecutedByPlayer)
				.executes(this::toggleFly);
		
		dispatcher.register(flyCommand);
	}
	
	private int toggleFly(CommandContext<ServerCommandSource> context)
	{
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();
		if (player == null)
		{
			source.sendError(Text.literal("You must be a player to use this command!"));
			return 0;
		}
		
		boolean isFlyingEnabled = flyPlayerList.contains(player.getUuid());
		if(isFlyingEnabled)
		{
			flyPlayerList.remove(player.getUuid());
		}
		else
		{
			flyPlayerList.add(player.getUuid());
		}
		
		player.getAbilities().allowFlying = !isFlyingEnabled;
		player.getAbilities().flying = !isFlyingEnabled;
		player.sendAbilitiesUpdate();
		String message = isFlyingEnabled ? "Flight disabled!" : "Flight enabled!";
		source.sendFeedback(() -> Text.of(message), false);
		
		return 1;
	}
	
}
