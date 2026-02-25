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

package top.byteeeee.fuzz.mixin.rule.waterFogColor;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.byteeeee.fuzz.FuzzSettings;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow
    protected abstract FogType getFogType(Camera camera);

    @ModifyArg(
        method = "computeFogColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"
        ),
        index = 0
    )
    private float modifyFogRed(float originalRed, @Local(argsOnly = true) Camera camera) {
        return getCustomColorComponent(0, originalRed, camera);
    }

    @ModifyArg(
        method = "computeFogColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"
        ),
        index = 1
    )
    private float modifyFogGreen(float originalGreen, @Local(argsOnly = true) Camera camera) {
        return getCustomColorComponent(1, originalGreen, camera);
    }

    @ModifyArg(
        method = "computeFogColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"
        ),
        index = 2
    )
    private float modifyFogBlue(float originalBlue, @Local(argsOnly = true) Camera camera) {
        return getCustomColorComponent(2, originalBlue, camera);
    }

    @Unique
    private float getCustomColorComponent(int index, float originalValue, Camera camera) {
        FogType fogType = this.getFogType(camera);
        if (Objects.equals(FuzzSettings.waterFogColor, "false") || FuzzSettings.waterFogColor == null) {
            return originalValue;
        }

        try {
            String colorStr = FuzzSettings.waterFogColor.startsWith("#") ? FuzzSettings.waterFogColor.substring(1) : FuzzSettings.waterFogColor;
            int colorInt = Integer.parseInt(colorStr, 16);
            int colorComponent = switch (index) {
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
