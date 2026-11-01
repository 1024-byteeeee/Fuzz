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

package top.byteeeee.fuzz.renderer.rule.commandCoordCompass;

//#if MC>=12000
import com.mojang.blaze3d.vertex.PoseStack;
//#endif
//#if MC<260200
import com.mojang.blaze3d.vertex.VertexConsumer;
//#endif

//#if MC>=12106
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//#else
//$$ import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//#endif
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

import net.minecraft.client.Camera;
//#if MC>=12100
import net.minecraft.client.DeltaTracker;
//#endif
//#if MC<12000
//$$ import net.minecraft.client.gui.GuiComponent;
//#endif
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
//#if MC>=12109
import net.minecraft.client.renderer.rendertype.RenderType;
//#endif
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

//#if MC>=260300
//$$ import org.joml.Matrix4f;
//#endif
//#if MC>=11904
import org.joml.Quaternionf;
//#else
//$$ import com.mojang.math.Vector3f;
//#endif

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.EntityUtil;
import top.byteeeee.fuzz.utils.IdentifierUtil;

//#if MC<260200
import java.util.Objects;
//#endif

public class CoordCompassRenderer {
    private static final Translator tr = new Translator("command.coordCompass");

    public static Vec3 targetCoord;
    public static boolean isActive = false;
    private static final Identifier TARGET_ICON = IdentifierUtil.of("fuzz", "textures/waypoint/target_point.png");

    public static void register() {
        //#if MC>=12106
        HudElementRegistry.addLast(IdentifierUtil.of("fuzz", "coord_compass_hud"), CoordCompassRenderer::renderHud);
        //#else
        //$$ HudRenderCallback.EVENT.register(CoordCompassRenderer::renderHud);
        //#endif
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(CoordCompassRenderer::renderWorld);
    }

    protected static void renderWorld(WorldRenderContext context) {
        if (!isActive || targetCoord == null || targetCoord == Vec3.ZERO || !FuzzSettings.commandCoordCompass) {
            return;
        }

        Minecraft client = Minecraft.getInstance();

        if (client.player == null) {
            return;
        }

        //#if MC>=260200
        //$$ Camera camera = client.gameRenderer.mainCamera();
        //#else
        Camera camera = client.gameRenderer.getMainCamera();
        //#endif
        PoseStack matrixStack = context.matrices();
        //#if MC<12111
        //$$ if (matrixStack == null) {
        //$$     return;
        //$$ }
        //#endif
        matrixStack.pushPose();
        Vec3 cameraPos = camera.position();
        Vec3 offset = targetCoord.subtract(cameraPos);
        int renderDistance = 30;
        Vec3 renderOffset = offset;
        boolean isFar = offset.length() > renderDistance * 0.9;

        if (isFar) {
            renderOffset = offset.normalize().scale(renderDistance * 0.9);
        }

        Vec3 renderPos = cameraPos.add(renderOffset);

        matrixStack.translate(
            (float) (renderPos.x - cameraPos.x),
            (float) (renderPos.y - cameraPos.y),
            (float) (renderPos.z - cameraPos.z)
        );

        float yaw = -camera.yRot();
        float pitch = camera.xRot();
        //#if MC>=260300
        //$$ matrixStack.mulPose(new Matrix4f().rotation(new Quaternionf().rotationYXZ((float)Math.toRadians(yaw), (float)Math.toRadians(pitch), 0)));
        //#elseif MC>=11904
        matrixStack.mulPose(new Quaternionf().rotationYXZ((float)Math.toRadians(yaw), (float)Math.toRadians(pitch), 0));
        //#else
        //$$ matrixStack.mulPose(Vector3f.YP.rotationDegrees(yaw));
        //$$ matrixStack.mulPose(Vector3f.XP.rotationDegrees(pitch));
        //#endif
        float scale = 1.0F;
        matrixStack.scale(scale, scale, scale);
        //#if MC<260200
        PoseStack.Pose entry = matrixStack.last();
        //#endif
        RenderType renderLayer = RenderTypes.textSeeThrough(TARGET_ICON);
        //#if MC>=260200
        //$$ context.submitNodeCollector().order(-1).submitCustomGeometry(context.poseStack(), renderLayer, (pose, buffer) -> {
        //$$     buffer.addVertex(pose, -1F, -1F, 0F).setColor(1F, 1F, 1F, 1).setUv(0F, 0F);
        //$$     buffer.addVertex(pose, -1F, 1F, 0F).setColor(1F, 1F, 1F, 1).setUv(0F, 1F);
        //$$     buffer.addVertex(pose, 1F, 1F, 0F).setColor(1F, 1F, 1F, 1).setUv(1F, 1F);
        //$$     buffer.addVertex(pose, 1F, -1F, 0F).setColor(1F, 1F, 1F, 1).setUv(1F, 0F);
        //$$ });
        //#else
        VertexConsumer vertexConsumer = Objects.requireNonNull(context.consumers()).getBuffer(renderLayer);
        //#if MC>=12100
        vertexConsumer.addVertex(entry.pose(), -1F, -1F, 0F).setUv(0F, 0F).setColor(-1).setLight(0xF000F0);
        //#else
        //$$ vertexConsumer.vertex(entry.pose(), -1F, -1F, 0F).color(255, 255, 255, 255).uv(0F, 0F).uv2(0xF000F0).endVertex();
        //#endif
        //#if MC>=12100
        vertexConsumer.addVertex(entry.pose(), -1F, 1F, 0F).setUv(0F, 1F).setColor(-1).setLight(0xF000F0);
        //#else
        //$$ vertexConsumer.vertex(entry.pose(), -1F, 1F, 0F).color(255, 255, 255, 255).uv(0F, 1F).uv2(0xF000F0).endVertex();
        //#endif
        //#if MC>=12100
        vertexConsumer.addVertex(entry.pose(), 1F, 1F, 0F).setUv(1F, 1F).setColor(-1).setLight(0xF000F0);
        //#else
        //$$ vertexConsumer.vertex(entry.pose(), 1F, 1F, 0F).color(255, 255, 255, 255).uv(1F, 1F).uv2(0xF000F0).endVertex();
        //#endif
        //#if MC>=12100
        vertexConsumer.addVertex(entry.pose(), 1F, -1F, 0F).setUv(1F, 0F).setColor(-1).setLight(0xF000F0);
        //#else
        //$$ vertexConsumer.vertex(entry.pose(), 1F, -1F, 0F).color(255, 255, 255, 255).uv(1F, 0F).uv2(0xF000F0).endVertex();
        //#endif
        //#endif
        matrixStack.popPose();
    }

    //#if MC>=12100
    protected static void renderHud(GuiGraphics drawContext, DeltaTracker renderTickCounter) {
        float partialTick = renderTickCounter.getGameTimeDeltaPartialTick(true);
    //#else
    //$$ protected static void renderHud(GuiGraphics drawContext, float partialTick) {
    //#endif
        if (!isActive || targetCoord == Vec3.ZERO) {
            return;
        }

        Minecraft client = ClientUtil.getCurrentClient();

        if (client.player == null) {
            return;
        }

        Vec3 playerPos = EntityUtil.getEntityPos(ClientUtil.getCurrentPlayer());
        Vec3 direction = targetCoord.subtract(playerPos);
        double distance = playerPos.distanceTo(targetCoord);
        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        boolean isHorizontalClose = horizontalDistance <= 2.0;
        boolean isVerticalClose = Math.abs(direction.y) <= 2.0;
        boolean isClose = isHorizontalClose && isVerticalClose;
        Vec3 horizontalDirection = new Vec3(direction.x, 0, direction.z).normalize();
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = getArrowHudYPosition(screenHeight);

        if (isClose) {
            renderCircle(drawContext, centerX, centerY);
            renderXMark(drawContext, centerX, centerY);
        } else if (isHorizontalClose) {
            renderCircle(drawContext, centerX, centerY);
        } else {
            float playerYaw = client.player.getViewYRot(partialTick);
            double targetYaw = Math.atan2(horizontalDirection.z, horizontalDirection.x) * 180.0 / Math.PI - 90.0;
            double angleDiff = ((targetYaw - playerYaw + 540) % 360) - 180;
            double angleRad = Math.toRadians(angleDiff);
            int arrowLength = 20;
            int arrowX = centerX + (int) (Math.sin(angleRad) * arrowLength);
            int arrowY = centerY - (int) (Math.cos(angleRad) * arrowLength);
            renderArrow(drawContext, centerX, centerY, arrowX, arrowY);
        }

        String distanceText;
        String verticalIndicator = "";

        if (isClose) {
            distanceText = tr.tr("near_the_target").getString();
        } else {
            if (!isVerticalClose) {
                verticalIndicator = direction.y > 0 ? "↑" : "↓";
            }
            distanceText = String.format("§e%s %.1fm %s", verticalIndicator, distance, verticalIndicator);
        }

        drawCenteredString(drawContext, client.font, distanceText, centerX, centerY + 20, 0xFFFFFF00);

        String targetX = formatCoord(targetCoord.x);
        String targetY = formatCoord(targetCoord.y);
        String targetZ = formatCoord(targetCoord.z);
        String playerX = formatCoord(playerPos.x);
        String playerY = formatCoord(playerPos.y);
        String playerZ = formatCoord(playerPos.z);
        String coordText = String.format("§b[ %s, %s, %s ]§f §r§a[ %s, %s, %s ]", targetX, targetY, targetZ, playerX, playerY, playerZ);

        drawCenteredString(drawContext, client.font, coordText, centerX, centerY + 30, 0xFF00FFFF);
    }

    private static int getArrowHudYPosition(int screenHeight) {
        int centerY;

        if (ClientUtil.getCurrentPlayer().isCreative() || ClientUtil.getCurrentPlayer().isSpectator()) {
            centerY = screenHeight - 62;
        } else if(ClientUtil.getCurrentPlayer().getArmorValue() <= 0) {
            centerY = screenHeight - 79;
        } else {
            centerY = screenHeight - 90;
        }

        return centerY;
    }

    private static void renderXMark(GuiGraphics drawContext, int centerX, int centerY) {
        int size = 8;
        drawLine(drawContext, centerX - size, centerY - size, centerX + size, centerY + size);
        drawLine(drawContext, centerX - size, centerY + size, centerX + size, centerY - size);
    }

    private static void renderCircle(GuiGraphics drawContext, int centerX, int centerY) {
        int radius = 13;
        int segments = 66;

        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            int x1 = centerX + (int) (radius * Math.cos(angle1));
            int y1 = centerY + (int) (radius * Math.sin(angle1));
            int x2 = centerX + (int) (radius * Math.cos(angle2));
            int y2 = centerY + (int) (radius * Math.sin(angle2));

            drawLine(drawContext, x1, y1, x2, y2);
        }
    }

    private static String formatCoord(double value) {
        if (value == (int) value) {
            return String.format("%d", (int) value);
        } else {
            String formatted = String.format("%.2f", value);
            if (formatted.endsWith(".00")) {
                return formatted.substring(0, formatted.length() - 3);
            } else if (formatted.endsWith("0")) {
                return formatted.substring(0, formatted.length() - 1);
            }
            return formatted;
        }
    }

    private static void renderArrow(GuiGraphics drawContext, int startX, int startY, int endX, int endY) {
        drawLine(drawContext, startX, startY, endX, endY);

        double angle = Math.atan2(endY - startY, endX - startX);
        int arrowSize = 5;

        int arrow1X = endX - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int arrow1Y = endY - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
        int arrow2X = endX - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int arrow2Y = endY - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        drawLine(drawContext, endX, endY, arrow1X, arrow1Y);
        drawLine(drawContext, endX, endY, arrow2X, arrow2Y);
    }

    private static void drawLine(GuiGraphics drawContext, int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1;
        int y = y1;

        while (true) {
            //#if MC>=12000
            drawContext.fill(x, y, x + 1, y + 1, -16711936);
            //#else
            //$$ GuiComponent.fill(drawContext, x, y, x + 1, y + 1, -16711936);
            //#endif

            if (x == x2 && y == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    private static void drawCenteredString(GuiGraphics graphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        //#if MC>=12000
        graphics.drawCenteredString(font, text, x, y, color);
        //#else
        //$$ GuiComponent.drawCenteredString(graphics, font, text, x, y, color);
        //#endif
    }
}
