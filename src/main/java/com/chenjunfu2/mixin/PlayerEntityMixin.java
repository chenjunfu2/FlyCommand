package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin implements PlayerEntityMixinExtension
{
	//新增字段：玩家是否开启飞行命令
	@Unique private boolean flyCommandOn = false;
	//新增字段：玩家刚才是否在飞行
	@Unique private boolean lastFly = false;
	
	@Unique
	@Override
	public boolean flycommand_1_20_1$GetFlyCommandOn()
	{
		return flyCommandOn;
	}
	
	@Unique
	@Override
	public void flycommand_1_20_1$SetFlyCommandOn(boolean flyCommandOn)
	{
		this.flyCommandOn = flyCommandOn;
	}
	
	@Unique
	@Override
	public boolean flycommand_1_20_1$GetLastFly()
	{
		return lastFly;
	}
	
	@Unique
	@Override
	public void flycommand_1_20_1$SetLastFly(boolean lastFly)
	{
		this.lastFly = lastFly;
	}
	
	@Inject(method = "readCustomDataFromNbt", at = @At(value = "RETURN"))
	private void loadFlyCommandData(NbtCompound nbt, CallbackInfo ci)
	{
		NbtCompound modCompoundData = nbt.getCompound("FlyCommandModData");
		
		this.flyCommandOn = modCompoundData.getBoolean("flyCommandOn");
		this.lastFly = modCompoundData.getBoolean("lastFly");
	}
	
	@Inject(method = "writeCustomDataToNbt", at = @At(value = "RETURN"))
	private void saveFlyCommandData(NbtCompound nbt, CallbackInfo ci)
	{
		NbtCompound modCompoundData = new NbtCompound();
		modCompoundData.putBoolean("flyCommandOn", this.flyCommandOn);
		modCompoundData.putBoolean("lastFly", this.lastFly);
		
		nbt.put("FlyCommandModData", modCompoundData);
	}
	
	//这个方法只会在落地后调用，落地后先判断是否应该受到伤害，然后设置lastFly为false
	@WrapOperation(method = "handleFallDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z"))
	private boolean handleFallDamageWrapOperation(PlayerAbilities instance, Operation<Boolean> original)
	{
		//先保存，然后清空（因为此处调用代表玩家落地后计算伤害，那么必然设置为false，true则由玩家起飞过程设定）
		boolean canImmuneFallDamage = this.lastFly;
		this.lastFly = false;
		
		//获取原始值
		boolean allowFlying = original.call(instance);//this.abilities.allowFlying
		
		//如果玩家没有开启飞行命令，返回原始值
		if (!this.flyCommandOn)
		{
			return allowFlying;
		}
		
		//不是服务器，返回原始值
		if(!((PlayerEntity)(Object)this instanceof ServerPlayerEntity))
		{
			return allowFlying;
		}
		
		//获取游戏模式
		GameMode curGamemode = ((ServerPlayerEntity)(Object)this).interactionManager.getGameMode();
		
		//如果是创造、旁观，则跳过
		if (curGamemode == GameMode.CREATIVE || curGamemode == GameMode.SPECTATOR)
		{
			return allowFlying;
		}
		
		//否则是生存、冒险，
		//那么如果canImmuneFallDamage是true，刚退出飞行状态，则免疫一次，否则强制受到伤害，哪怕allowFlying是true
		return canImmuneFallDamage;
	}
	
	//玩家在飞行状态，那么避免空中惩罚
	//玩家不在飞行状态，但是最后一次飞行状态为true，也就是说明刚刚退出飞行状态，那么避免惩罚直到落地
	@WrapOperation(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z"))
	public boolean getBlockBreakingSpeedWrapOperation(PlayerEntity player, Operation<Boolean> original)
	{
		//注意这个函数的返回值被条件反转
		//也就是返回true跳过语句，避免惩罚
		//返回false则进入语句，计算惩罚
		if(this.flyCommandOn && this.lastFly)//this.lastFly 包含了 player.getAbilities().flying 情况
		{
			return true;
		}
		
		return original.call(player);
	}
}
