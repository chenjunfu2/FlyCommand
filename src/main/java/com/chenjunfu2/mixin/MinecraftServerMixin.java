package com.chenjunfu2.mixin;

import com.chenjunfu2.FlyPlayerDataManager;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin
{
	//服务器保存事件（注意关闭不会调用这个saveAll，是另一个事件）
	@Inject(method = "saveAll", at = @At(value = "HEAD"))
	private void inj(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir)
	{
		FlyPlayerDataManager.saveData();
	}
	
	//服务器关闭事件->已由fabric注册完成
	//@Inject(method = "shutdown", at = @At(value = "HEAD"))
	//private void inj2(CallbackInfo ci)
	//{
	//	FlyPlayerDataManager.saveData();
	//}
}
