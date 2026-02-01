package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
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
abstract class ServerPlayerInteractionManagerMixin
{
	@Shadow @Final protected ServerPlayerEntity player;
	//设置游戏模式直接走自定义逻辑，飞行状态切换到生存继续保持，而不是改为false
	//这里因为麻将代码中切换游戏模式是由gamemode类的setGameMode设置的，而这个类既不包含playerEntity，
	//方法setGameMode也无法看见外部的对象，所以只能直接抄过来代码进行方法强制替换
	@Redirect(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;setAbilities(Lnet/minecraft/entity/player/PlayerAbilities;)V"))
	private void inj(GameMode instance, PlayerAbilities abilities)
	{
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
			((PlayerEntityMixinExtension)this.player).flycommand_1_20_1$SetLastFly(true);//因为旁观模式强制飞行，所以手动设置
		}
		else
		{
			if(!((PlayerEntityMixinExtension)this.player).flycommand_1_20_1$GetFlyCommandOn())
			{
				abilities.allowFlying = false;
				abilities.flying = false;
			}
			
			abilities.creativeMode = false;
			abilities.invulnerable = false;
		}
		
		abilities.allowModifyWorld = !instance.isBlockBreakingRestricted();
	}
}