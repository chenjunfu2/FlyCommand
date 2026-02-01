package com.chenjunfu2.api;

import org.spongepowered.asm.mixin.Unique;

public interface PlayerEntityMixinExtension
{
	@Unique
	boolean flycommand_1_20_1$GetFlyCommandOn();
	
	@Unique
	void flycommand_1_20_1$SetFlyCommandOn(boolean flyCommandOn);
	
	@Unique
	boolean flycommand_1_20_1$GetLastFly();
	
	@Unique
	void flycommand_1_20_1$SetLastFly(boolean lastFly);
}
