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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

import org.lwjgl.opengl.GL11;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.validators.HexValidator;
import top.byteeeee.fuzz.helpers.rule.blockOutline.RainbowColorHelper;

import java.util.Objects;

@GameVersion(version = "Minecraft == 1.16.5")
@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
        )
    )
    private void renderBlockOutlineWrapper(
        WorldRenderer worldRenderer, MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity,
        double cameraX, double cameraY, double cameraZ,
        BlockPos pos, BlockState state, Operation<Void> original
    ) {
        if (!Objects.equals(FuzzSettings.blockOutlineColor, "false")) {
            String colorString = FuzzSettings.blockOutlineColor;
            float red, green, blue;
            float alpha = (float) (FuzzSettings.blockOutlineAlpha / 255.0D);
            float lineWidth = (float) (FuzzSettings.blockOutlineWidth != -1.0D ? FuzzSettings.blockOutlineWidth : 1.5D);
            double X = pos.getX() - cameraX;
            double Y = pos.getY() - cameraY;
            double Z = pos.getZ() - cameraZ;
            VoxelShape shape = state.getOutlineShape(((WorldRendererAccessor) this).getWorld(), pos, ShapeContext.of(entity));
            if (Objects.equals(FuzzSettings.blockOutlineColor, "rainbow")) {
                float[] rainbowRgb = RainbowColorHelper.getRainbowColorComponents();
                red = rainbowRgb[0];
                green = rainbowRgb[1];
                blue = rainbowRgb[2];
            } else {
                if (HexValidator.isValidHexColor(colorString)) {
                    red = Integer.parseInt(colorString.substring(1, 3), 16) / 255.0F;
                    green = Integer.parseInt(colorString.substring(3, 5), 16) / 255.0F;
                    blue = Integer.parseInt(colorString.substring(5, 7), 16) / 255.0F;
                } else {
                    original.call(worldRenderer, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state);
                    return;
                }
            }
            renderCustomBlockOutline(matrices, shape, X, Y, Z, red, green, blue, alpha, lineWidth);
        } else {
            original.call(worldRenderer, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state);
        }
    }

    @Unique
    private void renderCustomBlockOutline(
        MatrixStack matrices, VoxelShape shape, double x, double y, double z,
        float red, float green, float blue, float alpha, float lineWidth
    ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.SRC_ALPHA,
            GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SrcFactor.ONE,
            GlStateManager.DstFactor.ZERO
        );
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableCull();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.lineWidth(Math.max(lineWidth, 0.168F));
        RenderSystem.disableTexture();
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.0f, -1.0f);
        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

        MatrixStack.Entry entry = matrices.peek();

        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            double offset = 0.00008D;

            float startX = (float) (x1 + x + offset);
            float startY = (float) (y1 + y + offset);
            float startZ = (float) (z1 + z + offset);

            float endX = (float) (x2 + x + offset);
            float endY = (float) (y2 + y + offset);
            float endZ = (float) (z2 + z + offset);

            float dirX = endX - startX;
            float dirY = endY - startY;
            float dirZ = endZ - startZ;

            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);

            float normalX = length > 0.0F ? dirX / length : 0.0F;
            float normalY = length > 0.0F ? dirY / length : 1.0F;
            float normalZ = length > 0.0F ? dirZ / length : 0.0F;

            buffer.vertex(entry.getModel(), startX, startY, startZ).color(red, green, blue, alpha).normal(entry.getNormal(), normalX, normalY, normalZ).next();
            buffer.vertex(entry.getModel(), endX, endY, endZ).color(red, green, blue, alpha).normal(entry.getNormal(), normalX, normalY, normalZ).next();
        });

        tessellator.draw();

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.depthMask(false);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
