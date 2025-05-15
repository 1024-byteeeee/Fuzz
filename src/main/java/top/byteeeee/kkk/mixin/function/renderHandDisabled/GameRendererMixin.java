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

package top.byteeeee.kkk.mixin.function.renderHandDisabled;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
//#if MC>=12006
//$$ import org.joml.Matrix4f;
//#else
import net.minecraft.client.util.math.MatrixStack;
//#endif

import org.spongepowered.asm.mixin.Mixin;

import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.helpers.Noop;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapMethod(method = "renderHand")
    private void worldRenderDisabled(
        //#if MC>=12006
        //$$ Camera camera, float tickDelta, Matrix4f matrix4f, Operation<Void> original
        //#else
        MatrixStack matrices, Camera camera, float tickDelta, Operation<Void> original
        //#endif
    ) {
        if (KKKSettings.renderHandDisabled) {
            Noop.noop();
        } else {
            //#if MC>=12006
            //$$ original.call(camera, tickDelta, matrix4f);
            //#else
            original.call(matrices, camera, tickDelta);
            //#endif
        }
    }
}
