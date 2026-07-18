package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin
{
	@Inject(method = "copyFrom", at = @At(value = "HEAD"))
	private void copyFromInject(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci)//玩家数据拷贝丢失修复
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
	private void teleportInject(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci)//甚至传送之后也忘记更新玩家数据
	{
		ServerPlayerEntity spe = (ServerPlayerEntity)(Object)this;
		spe.sendAbilitiesUpdate();//帮忙更新一下
	}
	
	//Server逻辑
	@Inject
	(
		method = "handleFall",
		at = @At
		(
			value = "INVOKE",
			shift = At.Shift.AFTER,
			target = "Lnet/minecraft/entity/player/PlayerEntity;fall(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V"
		)
	)//必须在返回之后，防止先取消状态再计算摔伤
	void handleFallInject(double xDifference, double yDifference, double zDifference, boolean onGround, CallbackInfo ci)
	{
		if(onGround)
		{
			//如果落地，那么取消上次飞行状态
			((PlayerEntityMixinExtension)(PlayerEntity)(Object)this).flycommand_1_20_1$SetLastFly(false);
		}
	}
}

