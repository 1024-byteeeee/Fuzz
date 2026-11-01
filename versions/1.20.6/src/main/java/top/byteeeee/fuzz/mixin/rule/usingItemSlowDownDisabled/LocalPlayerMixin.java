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

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.player.LocalPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.utils.ClientUtil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import top.byteeeee.fuzz.FuzzSettings;

@GameVersion(version = "Minecraft <= 1.20.6")
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @ModifyExpressionValue(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        )
    )
    private boolean usingItemNoSlowDown(boolean original) {
        return !FuzzSettings.usingItemSlowDownDisabled && original;
    }

    @WrapOperation(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        )
    )
    private boolean noApplySlowDown(LocalPlayer player, Operation<Boolean> original) {
        return !(FuzzSettings.usingItemSlowDownDisabled && ClientUtil.isLocalPlayerSelf(player)) && original.call(player);
    }
}
