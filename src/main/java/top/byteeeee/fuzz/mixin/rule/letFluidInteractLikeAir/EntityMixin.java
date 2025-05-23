/*
 * This file is part of the KKK project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 1024_byteeeee and contributors
 *
 * KKK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KKK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KKK. If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.fuzz.mixin.rule.letFluidInteractLikeAir;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyReturnValue(method = "updateMovementInFluid", at = @At("RETURN"))
    private boolean noUpdate(boolean original) {
        Entity entity = (Entity) (Object) this;
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer()) && !entity.isOnFire()) {
            return false;
        } else {
            return original;
        }
    }

    @WrapOperation(
        method = "updateMovementInFluid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;isPushedByFluids()Z"
        )
    )
    private boolean noPush(Entity entity, Operation<Boolean> original) {
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer()) && !entity.isOnFire()) {
            return false;
        } else {
            return original.call(entity);
        }
    }

    @WrapWithCondition(
        method = "setSwimming",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;setFlag(IZ)V"
        )
    )
    private boolean setSwimming(Entity entity, int index, boolean value) {
        return !FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer());
    }

    @ModifyReturnValue(method = "getFluidHeight", at = @At("RETURN"))
    private double getFluidHeight(double original) {
        Entity entity = (Entity) (Object) this;
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer())) {
            return 0.114514D;
        } else {
            return original;
        }
    }
}
