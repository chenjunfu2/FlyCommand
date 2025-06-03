package com.chenjunfu2;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.UUID;

public class Flycommand implements ModInitializer
{
	public static HashSet<UUID> flyPlayerList = new HashSet<>();
	public static MinecraftServer server;
	
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
		
		//注册玩家加入事件
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) ->
		{
			playerJoin(entity, world);
		});
		
		//注册玩家离开事件
		//ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) ->
		//{
		//	playerLeave(entity,world);
		//});
		
		//注册服务器实例事件
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> Flycommand.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> Flycommand.server = null);
	}
	
	//玩家加入后根据游戏模式判断是否开启飞行
	private void playerJoin(Entity entity, ServerWorld world)
	{
		if(!entity.isPlayer())
		{
			return;
		}
		
		ServerPlayerEntity player = (ServerPlayerEntity)entity;
		GameMode gamemode = player.interactionManager.getGameMode();
		if(gamemode != GameMode.CREATIVE && gamemode != GameMode.SPECTATOR)
		{
			if(player.getAbilities().allowFlying)
			{
				flyPlayerList.add(player.getUuid());
			}
			else
			{
				flyPlayerList.remove(player.getUuid());
			}
		}
	}
	
	private void playerLeave(Entity entity, ServerWorld world)
	{
		return;//不注册离开事件，方便玩家重进后仍能保留飞行状态
	}
	
	
	
	private void flyCMD(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		LiteralArgumentBuilder<ServerCommandSource> flyCommand =
			CommandManager.literal("fly")
				.requires(ServerCommandSource::isExecutedByPlayer)
				.executes(context -> this.toggleFly(context, OperationMode.TOGGLE))
					.then(CommandManager.literal("on")
					.executes(context -> this.toggleFly(context, OperationMode.ENABLE)))
					.then(CommandManager.literal("off"))
					.executes(context -> this.toggleFly(context, OperationMode.DISABLE));
		
		dispatcher.register(flyCommand);
	}
	
	private boolean isFly(ServerPlayerEntity player)
	{
		return player.getAbilities().allowFlying && flyPlayerList.contains(player.getUuid());
	}
	
	private void onFly(ServerPlayerEntity player)
	{
		flyPlayerList.add(player.getUuid());
		player.getAbilities().allowFlying = true;
		player.getAbilities().flying = true;
		player.sendAbilitiesUpdate();
	}
	
	private void offFly(ServerPlayerEntity player)
	{
		flyPlayerList.remove(player.getUuid());
		player.getAbilities().allowFlying = false;
		player.getAbilities().flying = false;
		player.sendAbilitiesUpdate();
	}
	
	
	private int toggleFly(CommandContext<ServerCommandSource> context, OperationMode mode)
	{
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();
		if (player == null)
		{
			source.sendError(Text.literal("You must be a player to use this command!"));
			return 0;
		}
		
		boolean isFlyingEnabled;
		
		if(mode == OperationMode.ENABLE)
		{
			isFlyingEnabled = true;
		}
		else if(mode == OperationMode.DISABLE)
		{
			isFlyingEnabled = false;
		}
		else//(mode == OperationMode.TOGGLE) or OTHER
		{
			isFlyingEnabled = !isFly(player);//取反切换
		}
		
		if(isFlyingEnabled)
		{
			offFly(player);
		}
		else
		{
			onFly(player);
		}
		
		String message = isFlyingEnabled ? "Flight disabled!" : "Flight enabled!";
		source.sendFeedback(() -> Text.of(message), false);
		
		return 1;
	}
}
