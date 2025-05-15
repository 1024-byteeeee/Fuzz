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

package top.byteeeee.kkk.mixin.function.usingItemSlowDownDisabled;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.network.ClientPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC>=12104
//$$ import top.byteeeee.kkk.utils.ClientUtil;
//$$ import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//#endif

//#if MC>=12105
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//$$ import top.byteeeee.kkk.utils.ClientUtil;
//#endif

import top.byteeeee.kkk.KKKSettings;

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
        return KKKSettings.usingItemSlowDownDisabled ? false : original;
    }

    //#if MC>=12104
    //$$ @ModifyReturnValue(method = "shouldStopSprinting", at = @At("RETURN"))
    //$$ private boolean shouldStopSprinting(boolean original) {
    //$$     ClientPlayerEntity player = ClientUtil.getCurrentPlayer();
    //$$     if (KKKSettings.usingItemSlowDownDisabled && player != null && player.isSprinting() && player.isUsingItem()) {
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
    //$$     if (KKKSettings.usingItemSlowDownDisabled && player.equals(ClientUtil.getCurrentPlayer())) {
    //$$         return false;
    //$$     } else {
    //$$         return original.call(player);
    //$$     }
    //$$ }
    //#endif
}
