package com.chenjunfu2.mixin;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor {
	@Accessor("abilities")
	PlayerAbilities getAbilities();
	
	@Mutable
	@Accessor("abilities")
	void setAbilities(PlayerAbilities abilities);
}
