package com.moondrip.mixin;

import com.moondrip.entity.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
	private void moondrip$tryUseSolarTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return;
		}

		LivingEntity self = (LivingEntity) (Object) this;
		ItemStack used = null;

		for (Hand hand : Hand.values()) {
			ItemStack stack = self.getStackInHand(hand);
			if (stack.isOf(ModItems.SOLAR_CREEPER_TOTEM)) {
				used = stack.copy();
				stack.decrement(1);
				break;
			}
		}

		if (used == null) {
			return;
		}

		if (self instanceof ServerPlayerEntity player) {
			player.incrementStat(Stats.USED.getOrCreateStat(ModItems.SOLAR_CREEPER_TOTEM));
			Criteria.USED_TOTEM.trigger(player, used);
		}

		self.setHealth(1.0F);
		self.clearStatusEffects();

		// Efectos del totem vanilla
		self.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
		self.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
		// Efectos propios (60 segundos = 1200 ticks)
		self.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 3)); // 8 corazones dorados
		self.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1200, 0));
		self.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0));

		self.getWorld().sendEntityStatus(self, (byte) 35);
		cir.setReturnValue(true);
	}
}
