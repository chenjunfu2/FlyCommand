package com.chenjunfu2.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin
{
	@Inject(method = "respawnPlayer", at = @At(value = "RETURN"))
	private void respawnPlayerInject(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir, @Local(ordinal = 1) ServerPlayerEntity serverPlayerEntity)
	{
		serverPlayerEntity.sendAbilitiesUpdate();//重新生成玩家的时候刷新状态以同步数据
	}
}
