package com.chenjunfu2.mixin;

import com.chenjunfu2.FlyPlayerDataManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.GameMode;
import net.minecraft.server.network.ServerPlayerInteractionManager;

import static net.minecraft.world.GameMode.CREATIVE;
import static net.minecraft.world.GameMode.SPECTATOR;


@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin
{
	@Shadow @Final protected ServerPlayerEntity player;
	//设置游戏模式直接走自定义逻辑，飞行状态切换到生存继续保持，而不是改为false
	@Redirect(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;setAbilities(Lnet/minecraft/entity/player/PlayerAbilities;)V"))
	private void inj(GameMode instance, PlayerAbilities abilities)
	{
		ServerPlayerEntity currentPlayer = this.player;
		if (instance == CREATIVE)
		{
			abilities.allowFlying = true;
			abilities.creativeMode = true;
			abilities.invulnerable = true;
		}
		else if (instance == SPECTATOR)
		{
			abilities.allowFlying = true;
			abilities.creativeMode = false;
			abilities.invulnerable = true;
			abilities.flying = true;
		}
		else
		{
			if(! FlyPlayerDataManager.containsPlayer(currentPlayer.getUuid()))
			{
				abilities.allowFlying = false;
				abilities.flying = false;
			}
			
			abilities.creativeMode = false;
			abilities.invulnerable = false;
		}
		
		abilities.allowModifyWorld = !instance.isBlockBreakingRestricted();
	}//以上代码都是麻将那边抄来的
}

//下面是曾经的尝试，直接注入指定位置，可惜无法判断玩家是不是列表里的，只能弃用
/*
@Mixin(GameMode.class)
public class ExampleMixin {
	@Redirect(
			method = "setAbilities",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z",
					ordinal = 2  // 定位第3个allowFlying赋值操作
			)
	)
	private void redirectAllowFlying(PlayerAbilities abilities, boolean value) {
		//GameMode self = (GameMode)(Object)this;
	}
	
	// 拦截flying字段赋值
	@Redirect(
			method = "setAbilities",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z",
					ordinal = 1
			)
	)
	private void redirectFlying(PlayerAbilities abilities, boolean value) {
		//GameMode self = (GameMode)(Object)this;
	}
}
*/