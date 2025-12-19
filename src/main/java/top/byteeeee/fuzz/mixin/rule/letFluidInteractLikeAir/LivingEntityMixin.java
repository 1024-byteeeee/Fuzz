/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 1024_byteeeee and contributors
 *
 * Fuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fuzz. If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.fuzz.mixin.rule.letFluidInteractLikeAir;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

//#if MC>=12111
//$$ import net.minecraft.fluid.FluidState;
//#endif
import net.minecraft.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(
        method = "travel",
        at = @At(
            value = "INVOKE",
            //#if MC>=12111
            //$$ target = "Lnet/minecraft/entity/LivingEntity;isTravellingInFluid(Lnet/minecraft/fluid/FluidState;)Z"
            //#else
            target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z"
            //#endif
        )
    )
    private boolean travel(
        LivingEntity entity,
        //#if MC>=12111
        //$$ FluidState fluidState,
        //#endif
        Operation<Boolean> original
    ) {
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer()) && entity.isInLava()) {
            return false;
        } else {
            return original.call(
                entity
                //#if MC>=12111
                //$$ ,fluidState
                //#endif
            );
        }
    }
}
