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

package top.byteeeee.fuzz.config.rule.commandHighLightEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.loader.api.FabricLoader;

import top.byteeeee.fuzz.config.template.AbstractListJsonConfig;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class CommandHighLightEntitiesConfig extends AbstractListJsonConfig<String> {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("fuzz").resolve("commandHighLightEntities").resolve("entities.json");

    private static final CommandHighLightEntitiesConfig INSTANCE = new CommandHighLightEntitiesConfig();

    private CommandHighLightEntitiesConfig() {
        super(CONFIG_PATH);
    }

    public static CommandHighLightEntitiesConfig getInstance() {
        return INSTANCE;
    }

    @Override
    protected Class<String> getElementType() {
        return String.class;
    }
}
