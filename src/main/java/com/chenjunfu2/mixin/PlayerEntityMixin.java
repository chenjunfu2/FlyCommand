package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
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
	
	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	private void loadFlyCommandData(NbtCompound nbt, CallbackInfo ci)
	{
		NbtCompound modCompoundData = nbt.getCompound("FlyCommandModData");
		
		this.flyCommandOn = modCompoundData.getBoolean("flyCommandOn");
		this.lastFly = modCompoundData.getBoolean("lastFly");
	}
	
	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	private void saveFlyCommandData(NbtCompound nbt, CallbackInfo ci)
	{
		NbtCompound modCompoundData = new NbtCompound();
		modCompoundData.putBoolean("flyCommandOn", this.flyCommandOn);
		modCompoundData.putBoolean("lastFly", this.lastFly);
		
		nbt.put("FlyCommandModData", modCompoundData);
	}
}
