/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 1024_byteeeee and contributors
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

package top.byteeeee.fuzz.mixin.rule.slimeBlockSlowDownDisabled;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.SlimeBlock;
import net.minecraft.block.TransparentBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(SlimeBlock.class)
public abstract class SlimeBlockMixin extends TransparentBlock {
    protected SlimeBlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyExpressionValue(
    method = "onSteppedOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;bypassesSteppingEffects()Z"
        )
    )
    private boolean skipOnSteppedOnLogic(boolean original) {
        return (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) || original;
    }

    @ModifyExpressionValue(
        method = "onLandedUpon",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;bypassesLandingEffects()Z"
        )
    )
    private boolean skipOnLandedUponLogic(boolean original) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) {
            return true;
        } else {
            return original;
        }
    }

    @ModifyExpressionValue(
        method = "onEntityLand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;bypassesLandingEffects()Z"
        )
    )
    private boolean skipOnEntityLandLogic(boolean original) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) {
            return true;
        } else {
            return original;
        }
    }
}
