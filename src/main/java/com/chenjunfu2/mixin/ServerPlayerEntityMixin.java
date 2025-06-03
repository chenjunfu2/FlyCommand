package com.chenjunfu2.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin
{
	@Shadow public abstract void sendAbilitiesUpdate();
	
	@Inject(method = "copyFrom",at = @At(value = "HEAD"))
	public void inj(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci)
	{
		PlayerEntityAccessor accessor = (PlayerEntityAccessor)(PlayerEntity)(Object)this;
		PlayerEntityAccessor oldAccessor = (PlayerEntityAccessor)(PlayerEntity)oldPlayer;
		
		accessor.setAbilities(oldAccessor.getAbilities());
	}
	
	@Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V",at = @At(value = "TAIL"))
	public void inj2(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci)
	{
		ServerPlayerEntity spe = (ServerPlayerEntity)(Object)this;
		spe.sendAbilitiesUpdate();
	}
}

