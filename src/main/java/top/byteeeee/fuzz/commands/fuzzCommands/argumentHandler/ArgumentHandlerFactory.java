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

package top.byteeeee.fuzz.commands.fuzzCommands.argumentHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ArgumentHandlerFactory {
    private static final Map<Class<?>, Supplier<ArgumentHandlerInterface<?>>> HANDLERS = new ConcurrentHashMap<>();

    public static ArgumentHandlerInterface<?> create(Class<?> type) {
        Supplier<ArgumentHandlerInterface<?>> supplier = HANDLERS.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
        return supplier.get();
    }

    static {
        HANDLERS.put(boolean.class, BooleanHandler::new);
        HANDLERS.put(int.class, IntegerHandler::new);
        HANDLERS.put(double.class, DoubleHandler::new);
        HANDLERS.put(String.class, StringHandler::new);
    }
}
