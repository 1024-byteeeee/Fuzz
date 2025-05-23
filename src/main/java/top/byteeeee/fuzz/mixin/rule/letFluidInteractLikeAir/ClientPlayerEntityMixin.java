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

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class, priority = 1688)
public abstract class ClientPlayerEntityMixin {

    @Shadow
    private boolean lastSprinting;

    @WrapMethod(method = "updateWaterSubmersionState")
    private boolean preventSwimmingWhileSprinting(Operation<Boolean> original) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (FuzzSettings.letFluidInteractLikeAir && player.equals(ClientUtil.getCurrentPlayer()) && !player.isOnFire()) {
            return false;
        } else {
            return original.call();
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPackets(CallbackInfo ci) {
        if (FuzzSettings.letFluidInteractLikeAir) {
            final MinecraftClient client = ClientUtil.getCurrentClient();
            final ClientPlayerEntity player = ClientUtil.getCurrentPlayer();
            if (!client.isInSingleplayer() && client.getCurrentServerEntry() != null) {
                boolean isSprinting = player.isSprinting();
                if (isSprinting != this.lastSprinting) {
                    this.lastSprinting = isSprinting;
                    ci.cancel();
                }
            }
        }
    }
}
