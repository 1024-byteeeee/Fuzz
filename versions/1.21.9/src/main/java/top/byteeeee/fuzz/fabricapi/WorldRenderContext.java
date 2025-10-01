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

package top.byteeeee.fuzz.fabricapi;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

@GameVersion(version = "Minecraft >= 1.21.9")
public final class WorldRenderContext {
    private final MatrixStack matrixStack;
    private final Camera camera;
    private final VertexConsumerProvider consumers;

    public WorldRenderContext(MatrixStack matrixStack, Camera camera, VertexConsumerProvider consumers) {
        this.matrixStack = matrixStack;
        this.camera = camera;
        this.consumers = consumers;
    }

    public MatrixStack matrixStack() {
        return matrixStack;
    }

    public Camera camera() {
        return camera;
    }

    public VertexConsumerProvider consumers() {
        return consumers;
    }
}
