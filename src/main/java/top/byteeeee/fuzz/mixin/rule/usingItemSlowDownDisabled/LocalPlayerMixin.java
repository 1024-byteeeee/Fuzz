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

package top.byteeeee.fuzz.mixin.rule.usingItemSlowDownDisabled;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.player.LocalPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.utils.ClientUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import top.byteeeee.fuzz.FuzzSettings;

@SuppressWarnings("SimplifiableConditionalExpression")
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @ModifyExpressionValue(
        method = "modifyInput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        )
    )
    private boolean usingItemNoSlowDown(boolean original) {
        return FuzzSettings.usingItemSlowDownDisabled ? false : original;
    }

    @ModifyReturnValue(method = "shouldStopRunSprinting", at = @At("RETURN"))
    private boolean shouldStopSprinting(boolean original) {
        LocalPlayer player = ClientUtil.getCurrentPlayer();
        if (FuzzSettings.usingItemSlowDownDisabled && player != null && player.isSprinting() && player.isUsingItem()) {
            return false;
        } else {
            return original;
        }
    }

    @WrapOperation(
        method = "modifyInput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        )
    )
    private boolean noApplySlowDown(LocalPlayer player, Operation<Boolean> original) {
        if (FuzzSettings.usingItemSlowDownDisabled && player.equals(ClientUtil.getCurrentPlayer())) {
            return false;
        } else {
            return original.call(player);
        }
    }
}
