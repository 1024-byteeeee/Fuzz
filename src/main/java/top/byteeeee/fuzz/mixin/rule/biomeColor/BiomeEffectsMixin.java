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

package top.byteeeee.fuzz.mixin.rule.biomeColor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.biome.BiomeEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.validators.HexValidator;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(BiomeEffects.class)
public abstract class BiomeEffectsMixin {
    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private int getSkyColor(int original) {
        if (!Objects.equals(FuzzSettings.skyColor, "false")) {
            FuzzSettings.skyColor = HexValidator.appendSharpIfNone(FuzzSettings.skyColor);
        }
        if (!Objects.equals(FuzzSettings.skyColor, "false") && HexValidator.isValidHexColor(FuzzSettings.skyColor)) {
            return Integer.parseInt(FuzzSettings.skyColor.substring(1), 16);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getFogColor", at = @At("RETURN"))
    private int getFogColor(int original) {
        if (!Objects.equals(FuzzSettings.fogColor, "false")) {
            FuzzSettings.fogColor = HexValidator.appendSharpIfNone(FuzzSettings.fogColor);
        }
        if (!Objects.equals(FuzzSettings.fogColor, "false") && HexValidator.isValidHexColor(FuzzSettings.fogColor)) {
            return Integer.parseInt(FuzzSettings.fogColor.substring(1), 16);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getWaterColor", at = @At("RETURN"))
    private int getWaterColor(int original) {
        if (!Objects.equals(FuzzSettings.waterColor, "false")) {
            FuzzSettings.waterColor = HexValidator.appendSharpIfNone(FuzzSettings.waterColor);
        }
        if (!Objects.equals(FuzzSettings.waterColor, "false") && HexValidator.isValidHexColor(FuzzSettings.waterColor)) {
            return Integer.parseInt(FuzzSettings.waterColor.substring(1), 16);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getWaterFogColor", at = @At("RETURN"))
    private int getWaterFogColor(int original) {
        if (!Objects.equals(FuzzSettings.waterFogColor, "false")) {
            FuzzSettings.waterFogColor = HexValidator.appendSharpIfNone(FuzzSettings.waterFogColor);
        }
        if (!Objects.equals(FuzzSettings.waterFogColor, "false") && HexValidator.isValidHexColor(FuzzSettings.waterColor)) {
            return Integer.parseInt(FuzzSettings.waterFogColor.substring(1), 16);
        } else {
            return original;
        }
    }
}
