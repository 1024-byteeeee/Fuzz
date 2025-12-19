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

package top.byteeeee.fuzz.mixin.rule.blockOutlineColor_Width;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.rule.blockOutline.RainbowColorHelper;
import top.byteeeee.fuzz.validators.HexValidator;

import java.util.Objects;

@GameVersion(version = "Minecraft >= 1.21.11")
@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements WorldRendererAccessor {
    @WrapOperation(
        method = "renderTargetBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDLnet/minecraft/client/render/state/OutlineRenderState;IF)V"
        )
    )
    private void renderBlockOutlineWrapper(
        WorldRenderer worldRenderer, MatrixStack matrixStack, VertexConsumer vertexConsumer,
        double cameraX, double cameraY, double cameraZ, OutlineRenderState outlineRenderState, int color, float width,
        Operation<Void> original
    ) {
        if (!Objects.equals(FuzzSettings.blockOutlineColor, "false")) {
            String colorString = FuzzSettings.blockOutlineColor;
            int customColor;
            if (Objects.equals(FuzzSettings.blockOutlineColor, "rainbow")) {
                customColor = RainbowColorHelper.getRainbowColor();
            } else {
                if (HexValidator.isValidHexColor(colorString)) {
                    int red = Integer.parseInt(colorString.substring(1, 3), 16);
                    int green = Integer.parseInt(colorString.substring(3, 5), 16);
                    int blue = Integer.parseInt(colorString.substring(5, 7), 16);
                    double alpha = FuzzSettings.blockOutlineAlpha;
                    customColor = ColorHelper.getArgb((int) alpha, red, green, blue);
                } else {
                    original.call(worldRenderer, matrixStack, vertexConsumer, cameraX, cameraY, cameraZ, outlineRenderState, color, width);
                    return;
                }
            }
            original.call(worldRenderer, matrixStack, vertexConsumer, cameraX, cameraY, cameraZ, outlineRenderState, customColor, width);
        } else {
            original.call(worldRenderer, matrixStack, vertexConsumer, cameraX, cameraY, cameraZ, outlineRenderState, color, width);
        }
    }

    @WrapOperation(
        method = "renderTargetBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDLnet/minecraft/client/render/state/OutlineRenderState;IF)V"
        )
    )
    private void setBlockOutlineWidth(
        WorldRenderer worldRenderer, MatrixStack matrixStack, VertexConsumer vertexConsumer,
        double cameraX, double cameraY, double cameraZ, OutlineRenderState outlineRenderState, int color, float width,
        Operation<Void> original
    ) {
        if (FuzzSettings.blockOutlineWidth != -1.0D) {
            original.call(
                worldRenderer, matrixStack, vertexConsumer,
                cameraX, cameraY, cameraZ, outlineRenderState, color, (float) FuzzSettings.blockOutlineWidth
            );
        } else {
            original.call(worldRenderer, matrixStack, vertexConsumer, cameraX, cameraY, cameraZ, outlineRenderState, color, width);
        }
    }

    @WrapOperation(
        method = "renderTargetBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/state/OutlineRenderState;highContrast()Z"
        )
    )
    private boolean setBlockOutlineColor(OutlineRenderState outlineRenderState, Operation<Boolean> original) {
        if (!Objects.equals(FuzzSettings.blockOutlineColor, "false") || FuzzSettings.blockOutlineWidth != -1) {
            return false;
        } else {
            return original.call(outlineRenderState);
        }
    }
}
