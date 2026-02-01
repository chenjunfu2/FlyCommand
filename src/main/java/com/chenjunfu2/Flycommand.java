package com.chenjunfu2;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class Flycommand implements ModInitializer
{
	private enum OperationMode
	{
		TOGGLE,  // 切换模式（默认）
		ENABLE,  // 强制开启
		DISABLE  // 强制关闭
	}
	
	public void onInitialize()
	{
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
		{
			flyCMD(dispatcher);
		});
	}
	
	private void flyCMD(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		LiteralArgumentBuilder<ServerCommandSource> flyCommand = CommandManager.literal("fly")
				.requires(ServerCommandSource::isExecutedByPlayer)
				.executes(context -> this.toggleFly(context, OperationMode.TOGGLE))
					.then(CommandManager.literal("on").executes(context -> this.toggleFly(context, OperationMode.ENABLE)))
					.then(CommandManager.literal("off").executes(context -> this.toggleFly(context, OperationMode.DISABLE)));
		
		dispatcher.register(flyCommand);
	}
	
	private boolean isFly(ServerPlayerEntity player)
	{
		return player.getAbilities().allowFlying && ((PlayerEntityMixinExtension)player).flycommand_1_20_1$GetFlyCommandOn();
	}
	
	private boolean allowSet(ServerPlayerEntity player)
	{
		var gamemode = player.interactionManager.getGameMode();
		return gamemode != GameMode.CREATIVE && gamemode != GameMode.SPECTATOR;//旁观或创造不会真的去影响飞行状态，仅记录
	}
	
	private void onFly(ServerPlayerEntity player)
	{
		((PlayerEntityMixinExtension)player).flycommand_1_20_1$SetFlyCommandOn(true);
		if(allowSet(player))
		{
			player.getAbilities().allowFlying = true;
			player.getAbilities().flying = true;
			player.sendAbilitiesUpdate();
		}
	}
	
	private void offFly(ServerPlayerEntity player)
	{
		((PlayerEntityMixinExtension)player).flycommand_1_20_1$SetFlyCommandOn(false);
		if(allowSet(player))
		{
			player.getAbilities().allowFlying = false;
			player.getAbilities().flying = false;
			player.sendAbilitiesUpdate();
		}
	}
	
	
	private int toggleFly(CommandContext<ServerCommandSource> context, OperationMode mode)
	{
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();
		if (player == null)
		{
			return 0;
		}
		
		boolean isFlying;
		
		if(mode == OperationMode.ENABLE)
		{
			isFlying = true;
		}
		else if(mode == OperationMode.DISABLE)
		{
			isFlying = false;
		}
		else//(mode == OperationMode.TOGGLE) or OTHER
		{
			isFlying = !isFly(player);//取反切换状态
		}
		
		if(isFlying)
		{
			onFly(player);
		}
		else
		{
			offFly(player);
		}
		
		String message = isFlying ? "§aFlight enabled!" : "§cFlight disabled!";
		source.sendFeedback(() -> Text.of(message), false);
		
		return 1;
	}
}
