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

package top.byteeeee.kkk.mixin.function.blockOutlineColor_Width;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.helpers.HexValidator;

import java.nio.FloatBuffer;
import java.util.Objects;

@GameVersion(version = "Minecraft == 1.16.5")
@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRenderMixin implements WorldRendererAccessor {
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
        if (!Objects.equals(KKKSettings.blockOutlineColor, "false")) {
            String colorString = KKKSettings.blockOutlineColor;
            colorString = HexValidator.appendSharpIfNone(colorString);
            if (HexValidator.isValidHexColor(colorString)) {
                float red = Integer.parseInt(colorString.substring(1, 3), 16) / 255.0F;
                float green = Integer.parseInt(colorString.substring(3, 5), 16) / 255.0F;
                float blue = Integer.parseInt(colorString.substring(5, 7), 16) / 255.0F;
                float alpha = (float) (KKKSettings.blockOutlineAlpha / 255.0D);
                float lineWidth = (float) (KKKSettings.blockOutlineWidth != -1.0D ? KKKSettings.blockOutlineWidth : 1.5D);
                double X = pos.getX() - cameraX;
                double Y = pos.getY() - cameraY;
                double Z = pos.getZ() - cameraZ;
                VoxelShape shape = state.getOutlineShape(this.getWorld(), pos, ShapeContext.of(entity));
                renderCustomBlockOutline(matrices, shape, X, Y, Z, red, green, blue, alpha, lineWidth);
            }
        } else {
            original.call(worldRenderer, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state);
        }
    }

    @Unique
    private void renderCustomBlockOutline(
        MatrixStack matrices, VoxelShape shape, double x, double y, double z,
        float red, float green, float blue, float alpha, float lineWidth
    ) {
        Matrix4f matrix = matrices.peek().getModel();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(168);
        matrix.writeRowFirst(matrixBuffer);
        matrixBuffer.rewind();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadMatrixf(matrixBuffer);
        GL11.glBegin(GL11.GL_LINES);
        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            GL11.glColor4f(red, green, blue, alpha);
            double offset = 0.001;
            GL11.glVertex3d((x1 + x) + offset, (y1 + y) + offset, (z1 + z) + offset);
            GL11.glVertex3d((x2 + x) + offset, (y2 + y) + offset, (z2 + z) + offset);
        });
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}

