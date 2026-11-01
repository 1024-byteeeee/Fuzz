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

//#if MC>=260000
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//#endif

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

//#if MC>=260000
import net.minecraft.world.level.material.FluidState;
//#endif
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
//#if MC>=260000
import org.spongepowered.asm.mixin.injection.At;
//#endif

//#if MC>=260000
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;
//#endif

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    //#if MC>=260000
    @WrapOperation(
        method = "travel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;shouldTravelInFluid(Lnet/minecraft/world/level/material/FluidState;)Z"
        )
    )
    private boolean travel(LivingEntity entity, FluidState fluidState, Operation<Boolean> original) {
        return !(FuzzSettings.letFluidInteractLikeAir && ClientUtil.isLocalPlayerSelf(entity) && entity.isInLava()) && original.call(entity,fluidState);
    }
    //#endif
}
