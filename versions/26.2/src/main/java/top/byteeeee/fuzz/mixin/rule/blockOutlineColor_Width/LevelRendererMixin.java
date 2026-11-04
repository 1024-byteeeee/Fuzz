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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import top.byteeeee.fuzz.renderer.rule.blockOutline.WorldOutlineRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.rule.blockOutline.RainbowColorHelper;
import top.byteeeee.fuzz.validators.HexValidator;

import java.util.Objects;

@GameVersion(version = "Minecraft >= 26.2")
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements LevelRendererAccessor {

    @Unique
    private int getCustomOutlineColor(int fallback) {
        String colorCfg = FuzzSettings.blockOutlineColor;

        if (Objects.equals(colorCfg, "false")) {
            return fallback;
        }

        if (Objects.equals(colorCfg, "rainbow")) {
            return RainbowColorHelper.getRainbowColor();
        }

        if (HexValidator.isValidHexColor(colorCfg)) {
            int r = Integer.parseInt(colorCfg.substring(1, 3), 16);
            int g = Integer.parseInt(colorCfg.substring(3, 5), 16);
            int b = Integer.parseInt(colorCfg.substring(5, 7), 16);
            int alpha = Math.clamp(FuzzSettings.blockOutlineAlpha, 0, 255);
            return ARGB.color(alpha, r, g, b);
        }

        return fallback;
    }

    @WrapOperation(
        method = "submitBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;submitHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;IFZ)V"
        )
    )
    private void wrapSubmitHitOutline(
        LevelRenderer instance, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
        RenderType renderType, BlockOutlineRenderState state,
        int originalColor, float lineWidth, boolean translucent,
        Operation<Void> original
    ) {
        int color = getCustomOutlineColor(originalColor);
        double width = FuzzSettings.blockOutlineWidth;
        if (width >= 0.0D) {
            Vec3 eye = Minecraft.getInstance().gameRenderer.mainCamera().position();
            double eyeX = eye.x() - state.pos().getX();
            double eyeY = eye.y() - state.pos().getY();
            double eyeZ = eye.z() - state.pos().getZ();
            submitNodeCollector.order(0).submitCustomGeometry(
                poseStack, RenderTypes.debugQuads(), (pose, buffer) ->
                WorldOutlineRenderer.render(
                    pose, buffer, state.shape(),
                    0, 0, 0,
                    eyeX, eyeY, eyeZ, width, color
                )
            );
        } else {
            original.call(instance, poseStack, submitNodeCollector, renderType, state, color, lineWidth, translucent);
        }
    }

    @WrapOperation(
        method = "submitBlockOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;highContrast()Z"
        )
    )
    private boolean wrapHighContrast(BlockOutlineRenderState state, Operation<Boolean> original) {
        return FuzzSettings.blockOutlineWidth < 0.0D && original.call(state);
    }
}
