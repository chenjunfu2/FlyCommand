package com.chenjunfu2;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
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

public class Flycommand implements ModInitializer
{
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
		//ServerEntityEvents.ENTITY_LOAD.register((entity, world) ->
		//{
		//	playerJoin(entity, world);
		//});
		
		//注册玩家离开事件
		//ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) ->
		//{
		//	playerLeave(entity,world);
		//});
		
		//注册服务器实例事件
		//ServerLifecycleEvents.SERVER_STARTED.register(server ->
		//{
		//	Flycommand.server = server;
		//});
		//
		//ServerLifecycleEvents.SERVER_STOPPING.register(server ->
		//{
		//	Flycommand.server = null;
		//});
	}
	
	//玩家加入后根据游戏模式判断是否开启飞行
	//private void playerJoin(Entity entity, ServerWorld world)
	//{
	//	if(!entity.isPlayer())
	//	{
	//		return;
	//	}
	//
	//	ServerPlayerEntity player = (ServerPlayerEntity)entity;
	//	GameMode gamemode = player.interactionManager.getGameMode();
	//	//虽然有数据文件保存，但是这样还是可以一定程度缓解意料之外的数据不同步情况
	//	if(gamemode != GameMode.CREATIVE && gamemode != GameMode.SPECTATOR)
	//	{
	//		if(FlyPlayerDataManager.containsPlayer(player.getUuid()))//先看看在不在里面
	//		{
	//			if(!player.getAbilities().allowFlying)//在里面但是状态不对，改一下
	//			{
	//				onFly(player);
	//			}
	//		}
	//		else if(player.getAbilities().allowFlying)//不在里面，但是为允许飞行状态，加进去
	//		{
	//			FlyPlayerDataManager.addPlayer(player.getUuid());
	//		}
	//		//else ()//不在里面，也不是允许飞行状态，不处理
	//	}
	//}
	
	//private void playerLeave(Entity entity, ServerWorld world)
	//{
	//	return;//不注册离开事件，方便玩家重进后仍能保留飞行状态
	//}
	
	
	
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
