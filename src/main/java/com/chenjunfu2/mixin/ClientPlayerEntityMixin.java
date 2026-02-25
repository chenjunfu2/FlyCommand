package com.chenjunfu2.mixin;

import com.chenjunfu2.api.PlayerEntityMixinExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin
{
	@Inject(method = "sendAbilitiesUpdate", at = @At(value = "RETURN"))
	void sendAbilitiesUpdateInject(CallbackInfo ci)
	{
		if(((ClientPlayerEntity)(Object)this).getAbilities().flying)//注意这里只会设置true，而不会清理值，值由落地伤害计算后清理
		{
			((PlayerEntityMixinExtension)this).flycommand_1_20_1$SetLastFly(true);
		}
	}
}
