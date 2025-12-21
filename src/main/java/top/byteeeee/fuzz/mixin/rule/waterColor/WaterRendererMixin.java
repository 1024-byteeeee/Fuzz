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

package top.byteeeee.fuzz.mixin.rule.waterColor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.level.biome.BiomeSpecialEffects;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(BiomeSpecialEffects.class)
public abstract class WaterRendererMixin {
    @WrapOperation(
        method = "waterColor",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/biome/BiomeSpecialEffects;waterColor:I",
            opcode = Opcodes.GETFIELD
        )
    )
    private int modifyWaterColor(BiomeSpecialEffects biomeSpecialEffects, Operation<Integer> original) {
        return
            Objects.equals(FuzzSettings.waterColor, "false") ?
            original.call(biomeSpecialEffects) :
            Integer.parseInt(FuzzSettings.waterColor.substring(1), 16);
    }
}
