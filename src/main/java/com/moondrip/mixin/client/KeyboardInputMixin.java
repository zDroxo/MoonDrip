package com.moondrip.mixin.client;

import com.moondrip.effect.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mientras el jugador tiene Hipnosis, se anulan sus teclas de movimiento y salto
@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

	@Inject(method = "tick", at = @At("TAIL"))
	private void moondrip$inmovilizar(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && player.hasStatusEffect(ModEffects.HIPNOSIS)) {
			this.pressingForward = false;
			this.pressingBack = false;
			this.pressingLeft = false;
			this.pressingRight = false;
			this.movementForward = 0.0F;
			this.movementSideways = 0.0F;
			this.jumping = false;
		}
	}
}
