package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin
{
	@Inject(method = "onUpdatePlayerAbilities" , at = @At(value = "RETURN"))
	private void onUpdatePlayerAbilitiesInject(UpdatePlayerAbilitiesC2SPacket packet, CallbackInfo ci)
	{
		ServerPlayerEntity currentPlayer = ((ServerPlayNetworkHandler)(Object)this).player;
		if(currentPlayer.getAbilities().flying)//注意这里只会设置true，而不会清理值，值由落地伤害计算后清理
		{
			((PlayerEntityMixinExtension)currentPlayer).flycommand_1_20_1$SetLastFly(true);
		}
	}
}
