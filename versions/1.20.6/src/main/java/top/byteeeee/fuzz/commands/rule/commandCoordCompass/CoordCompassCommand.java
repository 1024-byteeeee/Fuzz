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

package top.byteeeee.fuzz.commands.rule.commandCoordCompass;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

//#if MC>=12100
//$$ import net.minecraft.client.render.RenderTickCounter;
//#endif
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.CommandUtil;

import java.util.function.Supplier;

@GameVersion(version = "Minecraft >= 1.20.6")
@Environment(EnvType.CLIENT)
public class CoordCompassCommand {
    private static final String RULE_NAME = "commandCoordCompass";
    private static Vec3d targetCoord;
    private static boolean isActive = false;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("coordCompass")
        .then(ClientCommandManager.literal("set")
        .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
        .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
        .then(ClientCommandManager.argument("z", IntegerArgumentType.integer())
        .executes(c -> checkEnabled(c, () -> set(c)))))))
        .then(ClientCommandManager.literal("clear")
        .executes(c -> checkEnabled(c, CoordCompassCommand::clear))));
        HudRenderCallback.EVENT.register(CoordCompassCommand::renderHud);
    }

    private static int checkEnabled(CommandContext<FabricClientCommandSource> context, Supplier<Integer> action) {
        return CommandUtil.checkEnabled(context.getSource(), FuzzSettings.commandCoordCompass, RULE_NAME, action);
    }

    private static int set(CommandContext<FabricClientCommandSource> context) {
        int x = IntegerArgumentType.getInteger(context, "x");
        int y = IntegerArgumentType.getInteger(context, "y");
        int z = IntegerArgumentType.getInteger(context, "z");
        targetCoord = new Vec3d(x, y, z);
        isActive = true;
        return 1;
    }

    private static int clear() {
        targetCoord = null;
        isActive = false;
        return 1;
    }

    private static void renderHud(
        DrawContext drawContext
        //#if MC>=12100
        //$$ , RenderTickCounter tickCounter
        //#else
        , float tickDelta
        //#endif
    ) {
        if (!isActive || targetCoord == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        Vec3d playerPos = client.player.getPos();
        Vec3d direction = targetCoord.subtract(playerPos);
        double distance = playerPos.distanceTo(targetCoord);
        boolean isClose = distance <= 2.0F;
        Vec3d horizontalDirection = new Vec3d(direction.x, 0, direction.z).normalize();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight - 62;

        if (isClose) {
            renderXMark(drawContext, centerX, centerY);
        } else {
            //#if MC>=12100
            //$$ float playerYaw = client.player.getYaw();
            //#else
            float playerYaw = client.player.getYaw(tickDelta);
            //#endif
            double targetYaw = Math.atan2(horizontalDirection.z, horizontalDirection.x) * 180.0 / Math.PI - 90.0;
            double angleDiff = ((targetYaw - playerYaw + 540) % 360) - 180;
            double angleRad = Math.toRadians(angleDiff);
            int arrowLength = 20;
            int arrowX = centerX + (int) (Math.sin(angleRad) * arrowLength);
            int arrowY = centerY - (int) (Math.cos(angleRad) * arrowLength);
            renderArrow(drawContext, centerX, centerY, arrowX, arrowY);
        }

        String distanceText;

        if (isClose) {
            distanceText = "§a已到达目标附近";
        } else {
            distanceText = String.format("§e%.1fm", distance);
        }

        drawContext.drawTextWithShadow(
            client.textRenderer, distanceText,
            centerX - client.textRenderer.getWidth(distanceText) / 2,
            centerY + 20, 0xFFFFFF00
        );

        String coordText =
            String.format("§b[ %d, %d, %d ]§f §r§a[ %d, %d, %d ]",
            (int) targetCoord.x, (int) targetCoord.y, (int) targetCoord.z,
            (int) playerPos.x, (int) playerPos.y, (int) playerPos.z);

        drawContext.drawTextWithShadow(
            client.textRenderer, coordText,
            centerX - client.textRenderer.getWidth(coordText) / 2,
            centerY + 30, 0xFFFFFFFF
        );
    }

    private static void renderXMark(DrawContext drawContext, int centerX, int centerY) {
        int size = 8;
        drawLine(drawContext, centerX - size, centerY - size, centerX + size, centerY + size);
        drawLine(drawContext, centerX - size, centerY + size, centerX + size, centerY - size);
    }

    private static void renderArrow(DrawContext drawContext, int startX, int startY, int endX, int endY) {
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

    private static void drawLine(DrawContext drawContext, int x1, int y1, int x2, int y2) {
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