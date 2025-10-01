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

package top.byteeeee.fuzz.mixin.hooks.fabricapi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.buffers.GpuBufferSlice;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.fabricapi.WorldRenderContext;
import top.byteeeee.fuzz.fabricapi.WorldRenderEvents;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Shadow
    @Final
    private DefaultFramebufferSet framebufferSet;

    @Unique
    private WorldRenderContext context;

    @Unique
    private MatrixStack matrixStack;

    @Inject(method = "render", at = @At("HEAD"))
    private void onStart(CallbackInfo ci) {
        WorldRenderEvents.START.invoker().onStart();
    }

    @WrapOperation(
        method = "method_62214",
        at = @At(
            value = "NEW",
            target = "()Lnet/minecraft/client/util/math/MatrixStack;"
        )
    )
    private MatrixStack setMatrixStack(Operation<MatrixStack> original) {
        MatrixStack result = original.call();
        this.matrixStack = result;
        return result;
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/FramePass;setRenderer(Ljava/lang/Runnable;)V"
        )
    )
    private void setContext(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci, @Local Frustum frustum) {
        this.context = new WorldRenderContext(this.matrixStack, camera, this.bufferBuilders.getEntityVertexConsumers());
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;getCloudRenderModeValue()Lnet/minecraft/client/option/CloudRenderMode;"
        )
    )
    private void onAfterTranslucent(CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder) {
        FramePass pass = frameGraphBuilder.createPass(FuzzModClient.MOD_ID + ":afterTranslucent");
        this.framebufferSet.mainFramebuffer = pass.transfer(this.framebufferSet.mainFramebuffer);
        pass.setRenderer(() -> WorldRenderEvents.AFTER_TRANSLUCENT.invoker().render(this.context));
    }

    @Inject(
        method = "method_62214",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDDZ)V"
        )
    )
    private void onDebug(CallbackInfo ci) {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().render(this.context);
    }
}
