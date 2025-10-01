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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

@GameVersion(version = "Minecraft >= 1.21.9")
public class WorldRenderEvents {
    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, listeners -> () -> {
        for (Start start : listeners) {
            start.onStart();
        }
    });

    public static final Event<BeforeDebugRender> BEFORE_DEBUG_RENDER = EventFactory.createArrayBacked(BeforeDebugRender.class, listeners -> context -> {
        for (BeforeDebugRender render : listeners) {
            render.render(context);
        }
    });

    public static final Event<AfterTranslucent> AFTER_TRANSLUCENT = EventFactory.createArrayBacked(AfterTranslucent.class, listeners -> context -> {
        for (AfterTranslucent after : listeners) {
            after.render(context);
        }
    });

    public interface BeforeDebugRender {
        void render(WorldRenderContext context);
    }

    public interface AfterTranslucent {
        void render(WorldRenderContext context);
    }

    public interface Start {
        void onStart();
    }
}
