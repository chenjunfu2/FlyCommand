package com.chenjunfu2.mixin;


import com.chenjunfu2.api.ClientPlayerEntityMixinExtension;
import com.chenjunfu2.api.PlayerEntityMixinExtension;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Inject(method = "onPlayerAbilities",at = @At(value = "RETURN"))
	void onPlayerAbilitiesInject(PlayerAbilitiesS2CPacket packet, CallbackInfo ci, @Local(index = 2) PlayerEntity playerEntity)
	{
		if(!(playerEntity instanceof ClientPlayerEntity))
		{
			return;
		}
		
		GameMode clientGameMode = ((ClientPlayerEntityMixinExtension)playerEntity).flycommand_1_20_1$GetGameMode();
		((PlayerEntityMixinExtension)playerEntity).flycommand_1_20_1$SetFlyCommandOn(clientGameMode.isSurvivalLike() && playerEntity.getAbilities().allowFlying);
	}
}
