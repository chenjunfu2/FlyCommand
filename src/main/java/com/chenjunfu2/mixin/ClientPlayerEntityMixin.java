package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile)
	{
		super(world, profile);
	}
	
	@Inject(method = "sendAbilitiesUpdate", at = @At(value = "RETURN"))
	void sendAbilitiesUpdateInject(CallbackInfo ci)
	{
		ClientPlayerEntity currentPlayer = (ClientPlayerEntity)(Object)this;
		if(currentPlayer.getAbilities().flying)//注意这里只会设置true，而不会清理值，值由落地伤害计算后清理
		{
			((PlayerEntityMixinExtension)this).flycommand_1_20_1$SetLastFly(true);
		}
		else//例外情况：玩家在水里，并且当前退出了飞行状态，那么立刻设置为false
		{
			if(currentPlayer.isTouchingWater())
			{
				((PlayerEntityMixinExtension)currentPlayer).flycommand_1_20_1$SetLastFly(false);
			}
		}
	}
	
	//替mojang实现一下fall方法复写基类
	@Override
	@Intrinsic(displace = true)//如果没有，则注入替换，如果有，则重命名原方法并替代原方法
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition)
	{
		super.fall(heightDifference,onGround,state,landedPosition);
		if(onGround)
		{
			//如果落地，那么取消上次飞行状态
			((PlayerEntityMixinExtension)(PlayerEntity)(Object)this).flycommand_1_20_1$SetLastFly(false);
		}
	}
	
	//Client逻辑
	//@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "target"})
	//@Inject(method = "fall", at = @At(value = "RETURN"))//必须在返回之后，防止先取消状态再计算摔伤
	//void fallInject(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo info)
	//{
	//	if(onGround)
	//	{
	//		//如果落地，那么取消上次飞行状态
	//		((PlayerEntityMixinExtension)(PlayerEntity)(Object)this).flycommand_1_20_1$SetLastFly(false);
	//	}
	//}
}
