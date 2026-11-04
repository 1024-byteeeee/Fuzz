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

package top.byteeeee.fuzz.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public final class FuzzRuleRegistrar {
    private static final Map<String, Object> DEFAULT_VALUES = new ConcurrentHashMap<>();

    private FuzzRuleRegistrar() {
    }

    public static void register(Class<?> settingsClass) {
        for (Field field : settingsClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Rule.class)) {
                try {
                    DEFAULT_VALUES.put(field.getName(), field.get(null));
                    ValidatorManager.init(field);
                    ObserverManager.init(field);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to register rule: " + field.getName(), e);
                }
            }
        }
    }

    public static Object getDefaultValue(String ruleName) {
        return DEFAULT_VALUES.get(ruleName);
    }
}
