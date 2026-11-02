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

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
//#if MC<11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import org.lwjgl.opengl.GL11;
//#endif
//#if MC>=12105
import net.minecraft.client.renderer.RenderPipelines;
//#else
//$$ import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//#endif
//#if MC>=11700 && MC<12105
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//#endif
import java.util.OptionalDouble;

@GameVersion(version = "Minecraft <= 1.21.8")
public final class OutlineRenderTypes extends RenderStateShard {
    //#if MC<11700
    //$$ private static final int LEGACY_LINE_BUFFER_SIZE = 256;
    //$$ private static final float LINE_SHADER_VIEW_SHRINK = 1.0F - 1.0F / 256.0F;
    //$$ private static final LayeringStateShard LEGACY_LINE_LAYERING = new LayeringStateShard(
    //$$     "fuzz_legacy_line_layering",
    //$$     () -> {
    //$$         VIEW_OFFSET_Z_LAYERING.setupRenderState();
    //$$         RenderSystem.assertThread(RenderSystem::isOnRenderThread);
    //$$         GL11.glScalef(LINE_SHADER_VIEW_SHRINK, LINE_SHADER_VIEW_SHRINK, LINE_SHADER_VIEW_SHRINK);
    //$$     },
    //$$     VIEW_OFFSET_Z_LAYERING::clearRenderState
    //$$ );
    //#endif
    private static double cachedWidth = Double.NaN;
    private static RenderType cachedLines;
    private static RenderType cachedQuads;

    private OutlineRenderTypes() {
        super("fuzz_outline", () -> {}, () -> {});
    }

    public static RenderType relativeQuads() {
        if (cachedQuads == null) {
            RenderType.CompositeState.CompositeStateBuilder state = RenderType.CompositeState.builder();
            //#if MC>=12105
            cachedQuads = RenderType.create("fuzz_world_outline", RenderType.SMALL_BUFFER_SIZE, RenderPipelines.DEBUG_QUADS, state.createCompositeState(false));
            //#elseif MC>=11700
            //$$ state.setShaderState(POSITION_COLOR_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL);
            //$$ cachedQuads = RenderType.create("fuzz_world_outline", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, RenderType.SMALL_BUFFER_SIZE, state.createCompositeState(false));
            //#else
            //$$ state.setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL);
            //$$ cachedQuads = RenderType.create("fuzz_world_outline", DefaultVertexFormat.POSITION_COLOR, org.lwjgl.opengl.GL11.GL_QUADS, LEGACY_LINE_BUFFER_SIZE, state.createCompositeState(false));
            //#endif
        }
        return cachedQuads;
    }

    public static RenderType lines(double width) {
        if (cachedLines == null || Double.compare(width, cachedWidth) != 0) {
            RenderType.CompositeState.CompositeStateBuilder state = RenderType.CompositeState.builder()
                .setLineState(new LineStateShard(width == -1.0D ? OptionalDouble.empty() : OptionalDouble.of(width)))
                //#if MC<11700
                //$$ .setLayeringState(LEGACY_LINE_LAYERING)
                //#else
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                //#endif
                .setOutputState(ITEM_ENTITY_TARGET);
            //#if MC>=12105
            cachedLines = RenderType.create("fuzz_block_outline", RenderType.SMALL_BUFFER_SIZE, RenderPipelines.LINES, state.createCompositeState(false));
            //#elseif MC>=11700
            //$$ state.setShaderState(RENDERTYPE_LINES_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL);
            //$$ cachedLines = RenderType.create("fuzz_block_outline", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, RenderType.SMALL_BUFFER_SIZE, state.createCompositeState(false));
            //#else
            //$$ state.setTransparencyState(TRANSLUCENT_TRANSPARENCY);
            //$$ cachedLines = RenderType.create("fuzz_block_outline", DefaultVertexFormat.POSITION_COLOR, org.lwjgl.opengl.GL11.GL_LINES, LEGACY_LINE_BUFFER_SIZE, state.createCompositeState(false));
            //#endif
            cachedWidth = width;
        }
        return cachedLines;
    }
}
