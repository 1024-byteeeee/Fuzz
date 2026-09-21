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

import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;

//#if MC>=260300
//$$ import org.joml.Vector3f;
//$$ import org.joml.Vector3fc;
//#endif

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;

@Environment(EnvType.CLIENT)
@Mixin(SkyRenderer.class)
public abstract class SkyRenderStateMixin {
    //#if MC>=260300
    //$$ @WrapOperation(
    //$$     method = "extractRenderState",
    //$$     at = @At(
    //$$         value = "FIELD",
    //$$         target = "Lnet/minecraft/client/renderer/state/level/SkyRenderState;skyColor:Lorg/joml/Vector3fc;",
    //$$         opcode = Opcodes.PUTFIELD
    //$$     )
    //$$ )
    //$$ private void modifySkyColor(
    //$$     SkyRenderState state,
    //$$     Vector3fc value,
    //$$     Operation<Void> original
    //$$ ) {
    //$$     if ("false".equals(FuzzSettings.skyColor)) {
    //$$         original.call(state, value);
    //$$         return;
    //$$     }
    //$$
    //$$     int rgb = Integer.parseInt(FuzzSettings.skyColor.substring(1), 16);
    //$$     Vector3fc customColor = new Vector3f(
    //$$         ((rgb >>> 16) & 0xFF) / 255.0F,
    //$$         ((rgb >>> 8) & 0xFF) / 255.0F,
    //$$         (rgb & 0xFF) / 255.0F
    //$$     );
    //$$     original.call(state, customColor);
    //$$ }
    //#else
    @WrapOperation(
        method = "extractRenderState",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/state/level/SkyRenderState;skyColor:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static void modifySkyColor(
        SkyRenderState state,
        int value,
        Operation<Void> original
    ) {
        if ("false".equals(FuzzSettings.skyColor)) {
            original.call(state, value);
            return;
        }

        int customColor = Integer.parseInt(FuzzSettings.skyColor.substring(1), 16);
        original.call(state, customColor);
    }
    //#endif
}
