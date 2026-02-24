package com.chenjunfu2.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
public interface ClientPlayerEntityMixinExtension
{
	@Unique
	GameMode flycommand_1_20_1$GetGameMode();
	
	@Unique
	void flycommand_1_20_1$SetGameMode(GameMode newGameMode);
}
