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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.EntityUtil;
import top.byteeeee.fuzz.utils.IdentifierUtil;

import java.util.Objects;

public class CoordCompassRenderer {
    private static final Translator tr = new Translator("command.coordCompass");

    public static Vec3 targetCoord;
    public static boolean isActive = false;
    private static final Identifier TARGET_ICON = IdentifierUtil.of("fuzz", "textures/waypoint/target_point.png");

    public static void register() {
        HudElementRegistry.addLast(IdentifierUtil.of("fuzz", "coord_compass_hud"), CoordCompassRenderer::renderHud);
        LevelRenderEvents.BEFORE_TRANSLUCENT.register(CoordCompassRenderer::renderWorld);
    }

    protected static void renderWorld(LevelRenderContext context) {
        if (!isActive || targetCoord == null || !FuzzSettings.commandCoordCompass) {
            return;
        }

        Minecraft client = Minecraft.getInstance();

        if (client.player == null) {
            return;
        }

        Camera camera = client.gameRenderer.getMainCamera();
        PoseStack matrixStack = context.poseStack();
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
        matrixStack.mulPose(new Quaternionf().rotationYXZ((float)Math.toRadians(yaw), (float)Math.toRadians(pitch), 0));
        float scale = 1.0F;
        matrixStack.scale(scale, scale, scale);
        PoseStack.Pose entry = matrixStack.last();
        RenderType renderLayer = RenderTypes.fireScreenEffect(TARGET_ICON);
        VertexConsumer vertexConsumer = Objects.requireNonNull(context.bufferSource()).getBuffer(renderLayer);
        vertexConsumer.addVertex(entry.pose(), -1F, -1F, 0F).setUv(0F, 0F).setColor(-1);
        vertexConsumer.addVertex(entry.pose(), -1F, 1F, 0F).setUv(0F, 1F).setColor(-1);
        vertexConsumer.addVertex(entry.pose(), 1F, 1F, 0F).setUv(1F, 1F).setColor(-1);
        vertexConsumer.addVertex(entry.pose(), 1F, -1F, 0F).setUv(1F, 0F).setColor(-1);
        matrixStack.popPose();
    }

    protected static void renderHud(GuiGraphics drawContext, DeltaTracker renderTickCounter) {
        if (!isActive || targetCoord == null) {
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
            float playerYaw = client.player.getViewYRot(renderTickCounter.getGameTimeDeltaPartialTick(true));
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

        drawContext.drawString(
            client.font, distanceText,
            centerX - client.font.width(distanceText) / 2,
            centerY + 20, 0xFFFFFF00
        );

        String targetX = formatCoord(targetCoord.x);
        String targetY = formatCoord(targetCoord.y);
        String targetZ = formatCoord(targetCoord.z);
        String playerX = formatCoord(playerPos.x);
        String playerY = formatCoord(playerPos.y);
        String playerZ = formatCoord(playerPos.z);

        String coordText = String.format("§b[ %s, %s, %s ]§f §r§a[ %s, %s, %s ]", targetX, targetY, targetZ, playerX, playerY, playerZ);

        drawContext.drawString(
            client.font, coordText,
            centerX - client.font.width(coordText) / 2,
            centerY + 30, 0xFF00FFFF
        );
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
            drawContext.fill(x, y, x + 1, y + 1, -16711936);

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
}
