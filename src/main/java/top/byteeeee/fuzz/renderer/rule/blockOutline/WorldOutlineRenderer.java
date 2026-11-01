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

package top.byteeeee.fuzz.renderer.rule.blockOutline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.shapes.VoxelShape;
//#if MC>=11904
import org.joml.Vector3f;
//#else
//$$ import com.mojang.math.Vector4f;
//#endif

/** Camera-facing ribbons whose width is a percentage of one world-space block. */
public final class WorldOutlineRenderer {
    // Match vanilla line shader and VIEW_OFFSET_Z_LAYERING depth separation.
    // The quad render type must not apply either offset a second time.
    private static final float VIEW_SHRINK = (1.0F - 1.0F / 256.0F) * (1.0F - 1.0F / 4096.0F);

    private WorldOutlineRenderer() {}

    public static void render(PoseStack.Pose pose, VertexConsumer consumer, VoxelShape shape,
                              double x, double y, double z, double eyeX, double eyeY, double eyeZ,
                              double percentage, int color) {
        if (!(percentage > 0.0D) || !Double.isFinite(percentage)) return;
        final double halfWidth = percentage / 200.0D;
        shape.forAllEdges((ax, ay, az, bx, by, bz) -> {
            ax += x; ay += y; az += z;
            bx += x; by += y; bz += z;
            double dx = bx - ax, dy = by - ay, dz = bz - az;
            double vx = ax - eyeX, vy = ay - eyeY, vz = az - eyeZ;
            double nx = dy * vz - dz * vy;
            double ny = dz * vx - dx * vz;
            double nz = dx * vy - dy * vx;
            double length = Math.sqrt(nx * nx + ny * ny + nz * nz);
            // A zero-length edge, or an edge viewed end-on, has no projected line.
            if (!(length > 0.0D) || !Double.isFinite(length)) return;
            double scale = halfWidth / length;
            nx *= scale; ny *= scale; nz *= scale;
            vertex(pose, consumer, ax - nx, ay - ny, az - nz, color);
            vertex(pose, consumer, ax + nx, ay + ny, az + nz, color);
            vertex(pose, consumer, bx + nx, by + ny, bz + nz, color);
            vertex(pose, consumer, bx - nx, by - ny, bz - nz, color);
        });
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, double x, double y, double z, int color) {
        //#if MC>=11904
        Vector3f position = pose.pose().transformPosition(new Vector3f((float) x, (float) y, (float) z));
        //#else
        //$$ Vector4f position = new Vector4f((float) x, (float) y, (float) z, 1.0F);
        //$$ position.transform(pose.pose());
        //#endif
        float px = position.x() * VIEW_SHRINK, py = position.y() * VIEW_SHRINK, pz = position.z() * VIEW_SHRINK;
        //#if MC>=12100
        consumer.addVertex(px, py, pz).setColor(color);
        //#else
        //$$ consumer.vertex(px, py, pz).color((color >>> 16) & 255, (color >>> 8) & 255, color & 255, (color >>> 24) & 255).endVertex();
        //#endif
    }
}
