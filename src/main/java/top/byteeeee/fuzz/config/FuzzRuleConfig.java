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

package top.byteeeee.fuzz.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.config.rule.commandHighLightEntities.CommandHighLightEntitiesConfig;

@Environment(EnvType.CLIENT)
public class FuzzRuleConfig {
    public static void load() {
        CommandHighLightEntitiesConfig.getInstance().loadFromJson(FuzzSettings.highlightEntityList);
    }
}
