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

package top.byteeeee.fuzz.mixin.rule.skyColor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.state.SkyRenderState;
import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(SkyRendering.class)
public abstract class SkyRenderStateMixin {
    @WrapOperation(
        method = "updateRenderState",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/state/SkyRenderState;skyColor:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static void modifySkyColor(SkyRenderState state, int value, Operation<Void> original) {
        if (!Objects.equals(FuzzSettings.skyColor, "false")) {
            int customColor = Integer.parseInt(FuzzSettings.skyColor.substring(1), 16);
            original.call(state, customColor);
        } else {
            original.call(state, value);
        }
    }
}
