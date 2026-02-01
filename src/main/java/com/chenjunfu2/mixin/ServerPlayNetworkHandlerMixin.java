package com.chenjunfu2.mixin;

import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin
{
	@Inject(method = "onUpdatePlayerAbilities" , at=@At("RETURN"))
	private void onUpdatePlayerAbilitiesInject(UpdatePlayerAbilitiesC2SPacket packet, CallbackInfo ci)
	{
	
	
	
	}



}
