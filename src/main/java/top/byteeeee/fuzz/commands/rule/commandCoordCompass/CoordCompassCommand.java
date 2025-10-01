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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
//#if MC>=11900
//$$ import org.joml.Quaternionf;
//#endif
//#if MC<11900
import net.minecraft.util.math.Vec3f;
//#endif
//#if MC<11700
import org.lwjgl.opengl.GL11;
//#endif
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.commands.AbstractRuleCommand;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.EntityUtil;
import top.byteeeee.fuzz.utils.IdentifierUtil;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.fuzz.utils.Messenger;

@GameVersion(version = "Minecraft < 1.20.6")
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
        Vec3d cameraPos = context.camera().getPos();
        Vec3d offset = targetCoord.subtract(cameraPos);
        int renderDistance = 30;
        Vec3d correctedOffset = offset;

        if (offset.length() > renderDistance) {
            correctedOffset = offset.normalize().multiply(renderDistance);
        }

        matrixStack.push();
        matrixStack.translate(correctedOffset.getX(), correctedOffset.getY(), correctedOffset.getZ());
        float distance = (float) correctedOffset.length();
        float scale = Math.max(distance / 30.0F, 0.5F);
        scale = Math.max(scale * (1.0F - ((distance / 40.0F) * 0.1F)), scale * 0.75F);
        matrixStack.scale(scale, scale, scale);
        float yaw = context.camera().getYaw();
        float pitch = context.camera().getPitch();
        //#if MC>=11700
        //$$ RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //$$ RenderSystem.setShaderTexture(0, TARGET_ICON);
        //$$ RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //#else
        RenderSystem.enableTexture();
        //#endif
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        client.getTextureManager().bindTexture(TARGET_ICON);
        //#if MC>=11900
        //$$ float pitchRad = (float) Math.toRadians(pitch);
        //$$ float yawRad = (float) Math.toRadians(-yaw);
        //$$ matrixStack.multiply(new Quaternionf().rotationYXZ(yawRad, pitchRad, 0));
        //#else
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
        //#endif
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        MatrixStack.Entry entry = matrixStack.peek();
        //#if MC>=11700
        //$$ buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        //#else
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        //#endif
        buffer.vertex(entry.getModel(), -1.0f, -1.0f, 0.0f).texture(0, 0).next();
        buffer.vertex(entry.getModel(), -1.0f, 1.0f, 0.0f).texture(0, 1).next();
        buffer.vertex(entry.getModel(), 1.0f, 1.0f, 0.0f).texture(1, 1).next();
        buffer.vertex(entry.getModel(), 1.0f, -1.0f, 0.0f).texture(1, 0).next();
        tessellator.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrixStack.pop();
    }

    protected static void renderHud(MatrixStack matrixStack, float tickDelta) {
        if (!isActive || targetCoord == null) {
            return;
        }

        MinecraftClient client = ClientUtil.getCurrentClient();
        if (client.player == null) {
            return;
        }

        Vec3d playerPos = EntityUtil.getEntityPos(ClientUtil.getCurrentPlayer());
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
            renderCircle(matrixStack, centerX, centerY);
            renderXMark(matrixStack, centerX, centerY);
        } else if (isHorizontalClose) {
            renderCircle(matrixStack, centerX, centerY);
        } else {
            float playerYaw = client.player.getYaw(tickDelta);
            double targetYaw = Math.atan2(horizontalDirection.z, horizontalDirection.x) * 180.0 / Math.PI - 90.0;
            double angleDiff = ((targetYaw - playerYaw + 540) % 360) - 180;
            double angleRad = Math.toRadians(angleDiff);
            int arrowLength = 20;
            int arrowX = centerX + (int) (Math.sin(angleRad) * arrowLength);
            int arrowY = centerY - (int) (Math.cos(angleRad) * arrowLength);
            renderArrow(matrixStack, centerX, centerY, arrowX, arrowY);
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

        client.textRenderer.drawWithShadow(
            matrixStack, distanceText,
            centerX - (float) client.textRenderer.getWidth(distanceText) / 2,
            centerY + 20, 0xFFFFFF00
        );

        String targetX = formatCoord(targetCoord.x);
        String targetY = formatCoord(targetCoord.y);
        String targetZ = formatCoord(targetCoord.z);
        String playerX = formatCoord(playerPos.x);
        String playerY = formatCoord(playerPos.y);
        String playerZ = formatCoord(playerPos.z);

        String coordText = String.format("§b[ %s, %s, %s ]§f §r§a[ %s, %s, %s ]", targetX, targetY, targetZ, playerX, playerY, playerZ);

        client.textRenderer.drawWithShadow(
            matrixStack, coordText,
            centerX - (float) client.textRenderer.getWidth(coordText) / 2,
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

    private static void renderXMark(MatrixStack matrixStack, int centerX, int centerY) {
        int size = 8;
        drawLine(matrixStack, centerX - size, centerY - size, centerX + size, centerY + size);
        drawLine(matrixStack, centerX - size, centerY + size, centerX + size, centerY - size);
    }

    private static void renderCircle(MatrixStack matrixStack, int centerX, int centerY) {
        int radius = 13;
        int segments = 66;
        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            int x1 = centerX + (int) (radius * Math.cos(angle1));
            int y1 = centerY + (int) (radius * Math.sin(angle1));
            int x2 = centerX + (int) (radius * Math.cos(angle2));
            int y2 = centerY + (int) (radius * Math.sin(angle2));

            drawLine(matrixStack, x1, y1, x2, y2);
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

    private static void renderArrow(MatrixStack matrixStack, int startX, int startY, int endX, int endY) {
        drawLine(matrixStack, startX, startY, endX, endY);

        double angle = Math.atan2(endY - startY, endX - startX);
        int arrowSize = 5;

        int arrow1X = endX - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int arrow1Y = endY - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
        int arrow2X = endX - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int arrow2Y = endY - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        drawLine(matrixStack, endX, endY, arrow1X, arrow1Y);
        drawLine(matrixStack, endX, endY, arrow2X, arrow2Y);
    }

    private static void drawLine(MatrixStack matrixStack, int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1;
        int y = y1;

        while (true) {
            fill(matrixStack, x, y, x + 1, y + 1);

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

    private static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2) {
        DrawableHelper.fill(matrices, x1, y1, x2, y2, -16711936);
    }
}
