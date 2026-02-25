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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyReturnValue(method = "isInWater", at = @At("RETURN"))
    private boolean isInWater(boolean original) {
        Entity entity = (Entity) (Object) this;
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer()) && !entity.isOnFire()) {
            return false;
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "isInShallowWater", at = @At("RETURN"))
    private boolean isInShallowWater(boolean original) {
        Entity entity = (Entity) (Object) this;
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer()) && !entity.isOnFire()) {
            return false;
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "isInLava", at = @At("RETURN"))
    private boolean isInLava(boolean original) {
        Entity entity = (Entity) (Object) this;
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer()) && !entity.isOnFire()) {
            return false;
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "updateFluidInteraction", at = @At("RETURN"))
    private boolean noUpdate(boolean original) {
        Entity entity = (Entity) (Object) this;
        if (FuzzSettings.letFluidInteractLikeAir && entity.equals(ClientUtil.getCurrentPlayer()) && !entity.isOnFire()) {
            return false;
        } else {
            return original;
        }
    }

    @WrapOperation(
        method = "updateFluidInteraction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;isPushedByFluid()Z"
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
            target = "Lnet/minecraft/world/entity/Entity;setSharedFlag(IZ)V"
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
