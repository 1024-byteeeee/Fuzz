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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.settings.Rule;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class FuzzConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("fuzz");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("settings.json");

    public static void load() {
        try {
            if (!Files.exists(CONFIG_FILE)) {
                saveConfig();
                return;
            }
            String json = new String(Files.readAllBytes(CONFIG_FILE), StandardCharsets.UTF_8);
            Map<String, Object> configMap = GSON.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
            loadFromMap(configMap);
        } catch (IOException e) {
            FuzzModClient.LOGGER.warn("Failed to load config: {}", e.getMessage());
        }
    }

    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_DIR);
            String json = GSON.toJson(toMap());
            Files.write(CONFIG_FILE, json.getBytes());
        } catch (IOException e) {
            FuzzModClient.LOGGER.warn("Failed to save config: {}", e.getMessage());
        }
    }

    private static void loadFromMap(Map<String, Object> map) {
        try {
            for (Field field : FuzzSettings.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(Rule.class)) {
                    Object value = map.get(field.getName());
                    if (value != null) {
                        field.setAccessible(true);
                        field.set(null, convertValue(field.getType(), value));
                    }
                }
            }
        } catch (Exception e) {
            FuzzModClient.LOGGER.warn("Field access error: {}", e.getMessage());
        }
    }

    private static Object convertValue(Class<?> targetType, Object value) {
        if (targetType == int.class || targetType == Integer.class) {
            return ((Number) value).intValue();
        } else if (targetType == double.class || targetType == Double.class) {
            return ((Number) value).doubleValue();
        }
        return value;
    }

    private static Map<String, Object> toMap() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            for (Field field : FuzzSettings.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(Rule.class)) {
                    map.put(field.getName(), field.get(null));
                }
            }
        } catch (Exception e) {
            FuzzModClient.LOGGER.warn("Field read error: {}", e.getMessage());
        }
        return map;
    }
}
