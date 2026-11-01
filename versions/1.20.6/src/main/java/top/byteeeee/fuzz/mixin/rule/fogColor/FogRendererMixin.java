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

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.validators.HexValidator;

import net.minecraft.world.level.biome.BiomeSpecialEffects;

@GameVersion(version = "Minecraft <= 1.20.6")
@Environment(EnvType.CLIENT)
@Mixin(BiomeSpecialEffects.class)
public abstract class FogRendererMixin {
    @ModifyReturnValue(method = "getFogColor", at = @At("RETURN"))
    private int modifyColor(int original) {
        String color = FuzzSettings.fogColor;
        return HexValidator.isValidHexColor(color) ? Integer.parseInt(color.substring(1), 16) : original;
    }
}
