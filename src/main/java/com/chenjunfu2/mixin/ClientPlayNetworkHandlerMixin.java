package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Shadow @Final private MinecraftClient client;
	
	@Inject(method = "onPlayerAbilities", at = @At(value = "RETURN"))
	void onPlayerAbilitiesInject(PlayerAbilitiesS2CPacket packet, CallbackInfo ci)
	{
		if(client.player == null || client.interactionManager == null)
		{
			return;
		}
		
		GameMode clientGameMode = client.interactionManager.getCurrentGameMode();
		((PlayerEntityMixinExtension)client.player).flycommand_1_20_1$SetFlyCommandOn(clientGameMode.isSurvivalLike() && client.player.getAbilities().allowFlying);
		
		if(client.player.getAbilities().flying)
		{
			((PlayerEntityMixinExtension)client.player).flycommand_1_20_1$SetLastFly(true);
		}
	}
	
	@Inject(method = "onGameJoin", at = @At(value = "RETURN"))
	void onGameJoinInject(GameJoinS2CPacket packet, CallbackInfo ci)
	{
		if(client.player == null || client.interactionManager == null)
		{
			return;
		}
		
		GameMode clientGameMode = client.interactionManager.getCurrentGameMode();
		((PlayerEntityMixinExtension)client.player).flycommand_1_20_1$SetFlyCommandOn(clientGameMode.isSurvivalLike() && client.player.getAbilities().allowFlying);
		
		if(client.player.getAbilities().flying)
		{
			((PlayerEntityMixinExtension)client.player).flycommand_1_20_1$SetLastFly(true);
		}
		
	}
	
}
