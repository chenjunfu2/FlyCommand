package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin
{
	@Inject(method = "copyFrom", at = @At(value = "HEAD"))
	private void inj(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci)//玩家数据拷贝丢失修复
	{
		PlayerEntityAccessor newAccessor = (PlayerEntityAccessor)this;
		PlayerEntityAccessor oldAccessor = (PlayerEntityAccessor)oldPlayer;
		
		newAccessor.setAbilities(oldAccessor.getAbilities());//绝了，麻将居然这都能忘掉
		
		//细节拷贝mod数据
		PlayerEntityMixinExtension newModData = (PlayerEntityMixinExtension)this;
		PlayerEntityMixinExtension oldModData = (PlayerEntityMixinExtension)oldPlayer;
		
		newModData.flycommand_1_20_1$SetFlyCommandOn(oldModData.flycommand_1_20_1$GetFlyCommandOn());
		newModData.flycommand_1_20_1$SetLastFly(oldModData.flycommand_1_20_1$GetLastFly());
	}
	
	@Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At(value = "RETURN"))
	private void inj2(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci)//甚至传送之后也忘记更新玩家数据
	{
		ServerPlayerEntity spe = (ServerPlayerEntity)(Object)this;
		spe.sendAbilitiesUpdate();//帮忙更新一下
	}
}

