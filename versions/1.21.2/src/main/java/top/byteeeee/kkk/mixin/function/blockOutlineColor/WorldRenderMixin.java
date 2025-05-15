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

package top.byteeeee.kkk.mixin.function.blockOutlineColor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.shape.VoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.helpers.HexValidator;

import java.util.Objects;

@GameVersion(version = "Minecraft >= 1.21.2")
@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRenderMixin implements WorldRendererAccessor {
    @WrapOperation(
        method = "renderTargetBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V"
        )
    )
    private void renderBlockOutlineWrapper(
        WorldRenderer worldRenderer, MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity,
        double cameraX, double cameraY, double cameraZ,
        BlockPos pos, BlockState state, int originalColor, Operation<Void> original
    ) {
        if (!Objects.equals(KKKSettings.blockOutlineColor, "false")) {
            String colorString = KKKSettings.blockOutlineColor;
            colorString = HexValidator.appendSharpIfNone(colorString);
            if (HexValidator.isValidHexColor(colorString)) {
                int red = Integer.parseInt(colorString.substring(1, 3), 16);
                int green = Integer.parseInt(colorString.substring(3, 5), 16);
                int blue = Integer.parseInt(colorString.substring(5, 7), 16);
                double alpha = KKKSettings.blockOutlineAlpha;
                int customColor = ColorHelper.getArgb((int) alpha, red, green, blue);
                VoxelShape shape = state.getOutlineShape(this.getWorld(), pos, ShapeContext.of(entity));
                VertexRendering.drawOutline(matrices, vertexConsumer, shape, pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ, customColor);
            }
        } else {
            original.call(worldRenderer, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state, originalColor);
        }
    }
}

