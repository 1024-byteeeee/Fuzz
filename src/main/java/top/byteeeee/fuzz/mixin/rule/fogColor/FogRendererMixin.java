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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.fog.FogRenderer;

import org.joml.Vector4f;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @ModifyReturnValue(method = "computeFogColor", at = @At("RETURN"))
    private Vector4f modifyFogColor(Vector4f original) {
        if (!Objects.equals(FuzzSettings.fogColor, "false")) {
            try {
                int colorInt = Integer.parseInt(FuzzSettings.fogColor.substring(1), 16);

                float r = ((colorInt >> 16) & 0xFF) / 255.0f;
                float g = ((colorInt >> 8) & 0xFF) / 255.0f;
                float b = (colorInt & 0xFF) / 255.0f;

                return (new Vector4f(r, g, b, 1.0f));
            } catch (Exception e) {
                return original;
            }
        } else {
            return original;
        }
    }
}
