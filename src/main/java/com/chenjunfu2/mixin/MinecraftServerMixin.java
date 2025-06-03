package com.chenjunfu2.mixin;


import com.chenjunfu2.FlyPlayerDataManager;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
	@Inject(method = "saveAll", at = @At(value = "HEAD"))
	public void inj(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir)
	{
		FlyPlayerDataManager.saveData();
	}
}
