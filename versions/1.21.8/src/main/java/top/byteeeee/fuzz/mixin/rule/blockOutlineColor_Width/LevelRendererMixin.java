/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 1024_byteeeee and contributors
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
//#if MC>=12102
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//#endif

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.rule.blockOutline.RainbowColorHelper;
import top.byteeeee.fuzz.renderer.rule.blockOutline.OutlineRenderTypes;
import top.byteeeee.fuzz.renderer.rule.blockOutline.WorldOutlineRenderer;
import top.byteeeee.fuzz.validators.HexValidator;

import java.util.Objects;

@GameVersion(version = "Minecraft <= 1.21.8")
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @WrapOperation(
        method = "renderHitOutline",
        at = @At(value = "INVOKE",
            //#if MC>=12102
            target = "Lnet/minecraft/client/renderer/ShapeRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDI)V"
            //#else
            //$$ target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"
            //#endif
        )
    )
    private void customizeOutline(PoseStack poses, VertexConsumer consumer, VoxelShape shape, double x, double y, double z,
        //#if MC>=12102
        int originalColor, Operation<Void> original
        //#else
        //$$ float red, float green, float blue, float alpha, Operation<Void> original
        //#endif
    ) {
        double width = FuzzSettings.blockOutlineWidth;
        if (width == 0.0D) {
            return;
        }

        String color = FuzzSettings.blockOutlineColor;
        boolean customColor = Objects.equals(color, "rainbow") || HexValidator.isValidHexColor(color);
        if (customColor) {
            int argb = Objects.equals(color, "rainbow") ? RainbowColorHelper.getRainbowColor() : (FuzzSettings.blockOutlineAlpha << 24) | Integer.parseInt(color.substring(1), 16);
            //#if MC>=12102
            originalColor = argb;
            //#else
            //$$ red = ((argb >>> 16) & 255) / 255.0F;
            //$$ green = ((argb >>> 8) & 255) / 255.0F;
            //$$ blue = (argb & 255) / 255.0F;
            //$$ alpha = ((argb >>> 24) & 255) / 255.0F;
            //#endif
        }

        //#if MC<11700
        //$$ RenderType customLines = null;
        //#endif
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        if (width > 0.0D) {
            RenderType quads = OutlineRenderTypes.relativeQuads();
            //#if MC>=12102
            int argb = originalColor;
            //#else
            //$$ int argb = (Math.round(alpha * 255) << 24) | (Math.round(red * 255) << 16) | (Math.round(green * 255) << 8) | Math.round(blue * 255);
            //#endif
            WorldOutlineRenderer.render(poses.last(), buffers.getBuffer(quads), shape, x, y, z, 0, 0, 0, width, argb);
            buffers.endBatch(quads);
            return;
        }
        //#if MC<11700
        //$$ if (customColor) {
        //$$     customLines = OutlineRenderTypes.lines(-1.0D);
        //$$     consumer = buffers.getBuffer(customLines);
        //$$ }
        //#endif
        //#if MC>=12102
        original.call(poses, consumer, shape, x, y, z, originalColor);
        //#else
        //$$ original.call(poses, consumer, shape, x, y, z, red, green, blue, alpha);
        //#endif
        //#if MC<11700
        //$$ if (customLines != null) {
        //$$     buffers.endBatch(customLines);
        //$$ }
        //#endif
    }

    //#if MC>=12102
    @ModifyExpressionValue(method = "renderBlockOutline", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
    private boolean useCustomOutline(boolean highContrast) {
        return Objects.equals(FuzzSettings.blockOutlineColor, "false") && FuzzSettings.blockOutlineWidth == -1.0D && highContrast;
    }
    //#endif
}
