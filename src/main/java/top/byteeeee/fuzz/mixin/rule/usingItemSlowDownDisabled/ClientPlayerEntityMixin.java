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

import net.minecraft.client.network.ClientPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC>=12104
//$$ import top.byteeeee.fuzz.utils.ClientUtil;
//$$ import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//#endif

//#if MC>=12105
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//$$ import top.byteeeee.fuzz.utils.ClientUtil;
//#endif

import top.byteeeee.fuzz.FuzzSettings;

@SuppressWarnings("SimplifiableConditionalExpression")
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @ModifyExpressionValue(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
        )
    )
    private boolean usingItemNoSlowDown(boolean original) {
        return FuzzSettings.usingItemSlowDownDisabled ? false : original;
    }

    //#if MC>=12104
    //$$ @ModifyReturnValue(method = "shouldStopSprinting", at = @At("RETURN"))
    //$$ private boolean shouldStopSprinting(boolean original) {
    //$$     ClientPlayerEntity player = ClientUtil.getCurrentPlayer();
    //$$     if (FuzzSettings.usingItemSlowDownDisabled && player != null && player.isSprinting() && player.isUsingItem()) {
    //$$         return false;
    //$$     } else {
    //$$         return original;
    //$$     }
    //$$ }
    //#endif

    //#if MC>=12105
    //$$ @WrapOperation(
    //$$     method = "applyMovementSpeedFactors",
    //$$     at = @At(
    //$$         value = "INVOKE",
    //$$         target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
    //$$     )
    //$$ )
    //$$ private boolean noApplySlowDown(ClientPlayerEntity player, Operation<Boolean> original) {
    //$$     if (FuzzSettings.usingItemSlowDownDisabled && player.equals(ClientUtil.getCurrentPlayer())) {
    //$$         return false;
    //$$     } else {
    //$$         return original.call(player);
    //$$     }
    //$$ }
    //#endif
}
