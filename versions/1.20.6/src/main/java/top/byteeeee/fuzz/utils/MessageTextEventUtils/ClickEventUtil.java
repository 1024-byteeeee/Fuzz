/*
 * This file is part of the KKK project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 1024_byteeeee and contributors
 *
 * KKK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KKK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KKK. If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.fuzz.utils.MessageTextEventUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.ClickEvent;
import top.byteeeee.annotationtoolbox.annotation.GameVersion;

@GameVersion(version = "Minecraft < 1.21.5")
@Environment(EnvType.CLIENT)
public class ClickEventUtil {
    public static final ClickEvent.Action OPEN_URL = ClickEvent.Action.OPEN_URL;
    public static final ClickEvent.Action OPEN_FILE = ClickEvent.Action.OPEN_FILE;
    public static final ClickEvent.Action RUN_COMMAND = ClickEvent.Action.RUN_COMMAND;
    public static final ClickEvent.Action SUGGEST_COMMAND = ClickEvent.Action.SUGGEST_COMMAND;
    public static final ClickEvent.Action CHANGE_PAGE = ClickEvent.Action.CHANGE_PAGE;
    public static final ClickEvent.Action COPY_TO_CLIPBOARD = ClickEvent.Action.COPY_TO_CLIPBOARD;

    public static ClickEvent event(ClickEvent.Action action, Object value) {
        return new ClickEvent(action, String.valueOf(value));
    }
}
