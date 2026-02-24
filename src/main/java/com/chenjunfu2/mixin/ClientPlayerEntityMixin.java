package com.chenjunfu2.mixin;

import com.chenjunfu2.api.ClientPlayerEntityMixinExtension;
import com.chenjunfu2.api.PlayerEntityMixinExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements ClientPlayerEntityMixinExtension
{
	@Unique
	private GameMode clientGamemode;
	
	@Unique
	@Override
	public GameMode flycommand_1_20_1$GetGameMode()
	{
		return clientGamemode;
	}
	
	@Unique
	@Override
	public void flycommand_1_20_1$SetGameMode(GameMode newGameMode)
	{
		clientGamemode = newGameMode;
	}
	
	@Inject(method = "onGameModeChanged", at= @At(value = "HEAD"))
	void onGameModeChangedInject(GameMode gameMode, CallbackInfo ci)
	{
		this.clientGamemode = gameMode;
	}
	
	@Inject(method = "sendAbilitiesUpdate", at = @At(value = "RETURN"))
	void sendAbilitiesUpdateInject(CallbackInfo ci)
	{
		if(((ClientPlayerEntity)(Object)this).getAbilities().flying)
		{
			((PlayerEntityMixinExtension)this).flycommand_1_20_1$SetLastFly(true);
		}
	}
}
