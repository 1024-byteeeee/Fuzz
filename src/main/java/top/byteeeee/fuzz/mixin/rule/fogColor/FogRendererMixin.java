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

package top.byteeeee.fuzz.mixin.rule.fogColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.fog.FogRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.ModifyArg;

import top.byteeeee.fuzz.FuzzSettings;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @ModifyArg(
        method = "computeFogColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"
        ),
        index = 0
    )
    private float modifyFogRed(float originalRed) {
        return getCustomColorComponent(0, originalRed);
    }

    @ModifyArg(
        method = "computeFogColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"
        ),
        index = 1
    )
    private float modifyFogGreen(float originalGreen) {
        return getCustomColorComponent(1, originalGreen);
    }

    @ModifyArg(
        method = "computeFogColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"
        ),
        index = 2
    )
    private float modifyFogBlue(float originalBlue) {
        return getCustomColorComponent(2, originalBlue);
    }

    @Unique
    private float getCustomColorComponent(int channelIndex, float originalValue) {
        if (Objects.equals(FuzzSettings.fogColor, "false") || FuzzSettings.fogColor == null) {
            return originalValue;
        }

        try {
            String colorStr = FuzzSettings.fogColor.startsWith("#") ? FuzzSettings.fogColor.substring(1) : FuzzSettings.fogColor;
            int colorInt = Integer.parseInt(colorStr, 16);
            int colorComponent = switch (channelIndex) {
                case 0 -> (colorInt >> 16) & 0xFF; // r
                case 1 -> (colorInt >> 8) & 0xFF; // g
                case 2 -> colorInt & 0xFF; // b
                default -> 0;
            };
            return colorComponent / 255.0f;
        } catch (Exception e) {
            return originalValue;
        }
    }
}
