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

package top.byteeeee.fuzz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.byteeeee.fuzz.commands.RegisterCommands;
import top.byteeeee.fuzz.event.ClientEvent;
import top.byteeeee.fuzz.key.KeyBindings;

@Environment(EnvType.CLIENT)
public class FuzzModClient implements ClientModInitializer {
    public static final String MOD_NAME = "Fuzz";
    public static final String MOD_ID = "fuzz";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static MinecraftClient minecraftClient;

    @Override
    public void onInitializeClient() {
        LOGGER.info(MOD_NAME + " " + "loaded!");
        minecraftClient = MinecraftClient.getInstance();
        RegisterCommands.register();
        ClientEvent.register();
        KeyBindings.register();
    }
}
