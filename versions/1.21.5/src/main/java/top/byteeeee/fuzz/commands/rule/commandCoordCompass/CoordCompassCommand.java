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

//#if MC<12106
import com.mojang.blaze3d.systems.RenderSystem;
//#endif
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.*;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import org.joml.Quaternionf;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.commands.AbstractRuleCommand;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.IdentifierUtil;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.fuzz.utils.Messenger;

import java.util.Objects;

@GameVersion(version = "Minecraft >= 1.21.5")
@Environment(EnvType.CLIENT)
public class CoordCompassCommand extends AbstractRuleCommand {
    private static final CoordCompassCommand INSTANCE = new CoordCompassCommand();
    private static final Translator tr = new Translator("command.coordCompass");
    private static final String MAIN_CMD_NAME = "coordCompass";
    private static final String RULE_NAME = "commandCoordCompass";
    private static Vec3d targetCoord;
    private static boolean isActive = false;

    private static final Identifier TARGET_ICON = IdentifierUtil.of("fuzz", "textures/waypoint/target_point.png");

    public static CoordCompassCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal(MAIN_CMD_NAME)
        .then(ClientCommandManager.literal("set")
        .then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
        .then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
        .then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
        .executes(c -> checkEnabled(c, () -> set(c)))))))
        .then(ClientCommandManager.literal("clear")
        .executes(c -> checkEnabled(c, CoordCompassCommand::clear)))
        .then(ClientCommandManager.literal("help").executes(c ->checkEnabled(c, () -> help(c)))));
    }

    @Override
    protected boolean getCondition() {
        return FuzzSettings.commandCoordCompass;
    }

    @Override
    protected String getRuleName() {
        return RULE_NAME;
    }

    private static int set(CommandContext<FabricClientCommandSource> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        targetCoord = new Vec3d(x, y, z);
        isActive = true;
        return 1;
    }

    private static int clear() {
        targetCoord = null;
        isActive = false;
        return 1;
    }

    private static int help(CommandContext<FabricClientCommandSource> ctx) {
        Messenger.tell(ctx.getSource(), tr.tr("help.set").formatted(Formatting.GRAY));
        Messenger.tell(ctx.getSource(), tr.tr("help.clear").formatted(Formatting.GRAY));
        Messenger.tell(ctx.getSource(), tr.tr("help.help").formatted(Formatting.GRAY));
        return 1;
    }

    protected static void renderWorld(WorldRenderContext context) {
        if (!isActive || targetCoord == null || !FuzzSettings.commandCoordCompass) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || context.camera() == null) {
            return;
        }

        MatrixStack matrixStack = context.matrixStack();

        if (matrixStack == null) {
            return;
        }

        matrixStack.push();
        Vec3d cameraPos = context.camera().getPos();
        Vec3d offset = targetCoord.subtract(cameraPos);
        int renderDistance = 30;
        Vec3d renderOffset = offset;
        boolean isFar = offset.length() > renderDistance * 0.9;
        if (isFar) {
            renderOffset = offset.normalize().multiply(renderDistance * 0.9);
        }
        Vec3d renderPos = cameraPos.add(renderOffset);
        matrixStack.translate(
            (float) (renderPos.x - cameraPos.x),
            (float) (renderPos.y - cameraPos.y),
            (float) (renderPos.z - cameraPos.z)
        );
        float yaw = -context.camera().getYaw();
        float pitch = context.camera().getPitch();
        matrixStack.multiply(new Quaternionf().rotationYXZ((float)Math.toRadians(yaw), (float)Math.toRadians(pitch), 0));
        float scale = 1.0F;
        matrixStack.scale(scale, scale, scale);
        MatrixStack.Entry entry = matrixStack.peek();
        //#if MC>=12106
        //$$ RenderLayer renderLayer = RenderLayer.getFireScreenEffect(TARGET_ICON);
        //#else
        RenderLayer renderLayer = RenderLayer.getGuiTexturedOverlay(TARGET_ICON);
        //#endif
        VertexConsumer vertexConsumer = Objects.requireNonNull(context.consumers()).getBuffer(renderLayer);
        vertexConsumer.vertex(entry.getPositionMatrix(), -1F, -1F, 0F).texture(0F, 0F).color(-1);
        vertexConsumer.vertex(entry.getPositionMatrix(), -1F, 1F, 0F).texture(0F, 1F).color(-1);
        vertexConsumer.vertex(entry.getPositionMatrix(), 1F, 1F, 0F).texture(1F, 1F).color(-1);
        vertexConsumer.vertex(entry.getPositionMatrix(), 1F, -1F, 0F).texture(1F, 0F).color(-1);
        //#if MC<12106
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //#endif
        matrixStack.pop();
    }

    protected static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!isActive || targetCoord == null) {
            return;
        }

        MinecraftClient client = ClientUtil.getCurrentClient();
        if (client.player == null) {
            return;
        }

        Vec3d playerPos = client.player.getPos();
        Vec3d direction = targetCoord.subtract(playerPos);
        double distance = playerPos.distanceTo(targetCoord);
        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        boolean isHorizontalClose = horizontalDistance <= 2.0;
        boolean isVerticalClose = Math.abs(direction.y) <= 2.0;
        boolean isClose = isHorizontalClose && isVerticalClose;
        Vec3d horizontalDirection = new Vec3d(direction.x, 0, direction.z).normalize();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = getArrowHudYPosition(screenHeight);

        if (isClose) {
            renderCircle(drawContext, centerX, centerY);
            renderXMark(drawContext, centerX, centerY);
        } else if (isHorizontalClose) {
            renderCircle(drawContext, centerX, centerY);
        } else {
            float playerYaw = client.player.getYaw(renderTickCounter.getTickProgress(true));
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
            distanceText = String.format("%s §e%.1fm %s", verticalIndicator, distance, verticalIndicator);
        }

        drawContext.drawTextWithShadow(
            client.textRenderer, distanceText,
            centerX - client.textRenderer.getWidth(distanceText) / 2,
            centerY + 20, 0xFFFFFF00
        );

        String targetX = formatCoord(targetCoord.x);
        String targetY = formatCoord(targetCoord.y);
        String targetZ = formatCoord(targetCoord.z);
        String playerX = formatCoord(playerPos.x);
        String playerY = formatCoord(playerPos.y);
        String playerZ = formatCoord(playerPos.z);

        String coordText = String.format("§b[ %s, %s, %s ]§f §r§a[ %s, %s, %s ]", targetX, targetY, targetZ, playerX, playerY, playerZ);

        drawContext.drawTextWithShadow(
            client.textRenderer, coordText,
            centerX - client.textRenderer.getWidth(coordText) / 2,
            centerY + 30, 0xFF00FFFF
        );
    }

    private static int getArrowHudYPosition(int screenHeight) {
        int centerY;

        if (ClientUtil.getCurrentPlayer().isCreative() || ClientUtil.getCurrentPlayer().isSpectator()) {
            centerY = screenHeight - 62;
        } else if(ClientUtil.getCurrentPlayer().getArmor() <= 0) {
            centerY = screenHeight - 79;
        } else {
            centerY = screenHeight - 90;
        }

        return centerY;
    }

    private static void renderXMark(DrawContext drawContext, int centerX, int centerY) {
        int size = 8;
        drawLine(drawContext, centerX - size, centerY - size, centerX + size, centerY + size);
        drawLine(drawContext, centerX - size, centerY + size, centerX + size, centerY - size);
    }

    private static void renderCircle(DrawContext drawContext, int centerX, int centerY) {
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
