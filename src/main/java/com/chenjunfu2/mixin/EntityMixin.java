package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
abstract class EntityMixin
{
	@Inject(
		method = "fall",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onLanding()V", shift = At.Shift.BEFORE)
	)
	void failInject(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci)
	{
		if(!(((Object)this) instanceof PlayerEntity playerEntity))//必须为玩家
		{
			return;
		}
		
		//如果落地，那么取消上次飞行状态
		((PlayerEntityMixinExtension)playerEntity).flycommand_1_20_1$SetLastFly(false);//注入位置确保onGround恒为true
	}
}
