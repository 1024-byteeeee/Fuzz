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

package top.byteeeee.kkk.mixin.function.biomeColor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.biome.BiomeEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.helpers.HexValidator;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(BiomeEffects.class)
public abstract class BiomeEffectsMixin {
    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private int getSkyColor(int original) {
        if (!Objects.equals(KKKSettings.skyColor, "false")) {
            KKKSettings.skyColor = HexValidator.appendSharpIfNone(KKKSettings.skyColor);
        }
        if (!Objects.equals(KKKSettings.skyColor, "false") && HexValidator.isValidHexColor(KKKSettings.skyColor)) {
            return Integer.parseInt(KKKSettings.skyColor.substring(1), 16);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getFogColor", at = @At("RETURN"))
    private int getFogColor(int original) {
        if (!Objects.equals(KKKSettings.fogColor, "false")) {
            KKKSettings.fogColor = HexValidator.appendSharpIfNone(KKKSettings.fogColor);
        }
        if (!Objects.equals(KKKSettings.fogColor, "false") && HexValidator.isValidHexColor(KKKSettings.fogColor)) {
            return Integer.parseInt(KKKSettings.fogColor.substring(1), 16);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getWaterColor", at = @At("RETURN"))
    private int getWaterColor(int original) {
        if (!Objects.equals(KKKSettings.waterColor, "false")) {
            KKKSettings.waterColor = HexValidator.appendSharpIfNone(KKKSettings.waterColor);
        }
        if (!Objects.equals(KKKSettings.waterColor, "false") && HexValidator.isValidHexColor(KKKSettings.waterColor)) {
            return Integer.parseInt(KKKSettings.waterColor.substring(1), 16);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getWaterFogColor", at = @At("RETURN"))
    private int getWaterFogColor(int original) {
        if (!Objects.equals(KKKSettings.waterFogColor, "false")) {
            KKKSettings.waterFogColor = HexValidator.appendSharpIfNone(KKKSettings.waterFogColor);
        }
        if (!Objects.equals(KKKSettings.waterFogColor, "false") && HexValidator.isValidHexColor(KKKSettings.waterColor)) {
            return Integer.parseInt(KKKSettings.waterFogColor.substring(1), 16);
        } else {
            return original;
        }
    }
}
