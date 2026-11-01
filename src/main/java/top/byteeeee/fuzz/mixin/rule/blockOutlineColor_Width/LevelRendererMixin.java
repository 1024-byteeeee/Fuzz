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

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import top.byteeeee.fuzz.renderer.rule.blockOutline.WorldOutlineRenderer;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.rule.blockOutline.RainbowColorHelper;
import top.byteeeee.fuzz.validators.HexValidator;

import java.util.Objects;

@GameVersion(version = "Minecraft >= 1.21.11 && Minecraft < 26.2")
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements LevelRendererAccessor {
    @WrapOperation(
        method = "renderBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;DDDLnet/minecraft/client/renderer/state/BlockOutlineRenderState;IF)V"
        )
    )
    private void renderBlockOutlineWrapper(
        LevelRenderer levelRenderer, PoseStack poseStack, VertexConsumer vertexConsumer,
        double camX, double camY, double camZ,
        BlockOutlineRenderState state,
        int color, float width, Operation<Void> original
    ) {
        String configuredColor = FuzzSettings.blockOutlineColor;

        if (Objects.equals(configuredColor, "rainbow")) {
            color = RainbowColorHelper.getRainbowColor();
        } else if (HexValidator.isValidHexColor(configuredColor)) {
            color = (FuzzSettings.blockOutlineAlpha << 24) | Integer.parseInt(configuredColor.substring(1), 16);
        }

        double configuredWidth = FuzzSettings.blockOutlineWidth;

        if (configuredWidth >= 0.0D) {
            if (configuredWidth == 0.0D) {
                return;
            }
            RenderType quads = RenderTypes.debugQuads();
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            WorldOutlineRenderer.render(poseStack.last(), buffers.getBuffer(quads), state.shape(),
                state.pos().getX() - camX, state.pos().getY() - camY, state.pos().getZ() - camZ,
                0, 0, 0, configuredWidth, color);
            buffers.endBatch(quads);
        } else {
            original.call(levelRenderer, poseStack, vertexConsumer, camX, camY, camZ, state, color, width);
        }
    }

    @WrapOperation(
        method = "renderBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/state/BlockOutlineRenderState;highContrast()Z"
        )
    )
    private boolean setBlockOutlineColor(BlockOutlineRenderState outlineRenderState, Operation<Boolean> original) {
        return !(!Objects.equals(FuzzSettings.blockOutlineColor, "false") || FuzzSettings.blockOutlineWidth != -1) && original.call(outlineRenderState);
    }
}
