package com.moondrip.mixin.client;

import com.moondrip.entity.ModItems;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Inject(method = "getActiveTotemOfUndying", at = @At("HEAD"), cancellable = true)
	private static void moondrip$showSolarTotem(PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
		for (Hand hand : Hand.values()) {
			ItemStack stack = player.getStackInHand(hand);
			if (stack.isOf(ModItems.SOLAR_CREEPER_TOTEM)) {
				cir.setReturnValue(stack.copy());
				return;
			}
		}
	}
}
