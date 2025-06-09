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

package top.byteeeee.fuzz.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import top.byteeeee.fuzz.FuzzModClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class ObserverManager {
    private static final ConcurrentHashMap<Field, List<Observer<?>>> fieldObservers = new ConcurrentHashMap<>();

    public static void init(Field field) {
        Rule annotation = field.getAnnotation(Rule.class);

        if (annotation == null) {
            return;
        }

        Class<? extends Observer<?>>[] observerClasses = annotation.observers();
        if (observerClasses.length > 0) {
            List<Observer<?>> observers = new ArrayList<>();
            for (Class<? extends Observer<?>> observerClass : observerClasses) {
                try {
                    Observer<?> observer = observerClass.getDeclaredConstructor().newInstance();
                    observers.add(observer);
                } catch (Exception e) {
                    FuzzModClient.LOGGER.error("Failed to instantiate observer: {}", observerClass.getName(), e);
                }
            }
            if (!observers.isEmpty()) {
                fieldObservers.put(field, observers);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void notifyObservers(Field field, T oldValue, T newValue) {
        List<Observer<?>> observers = fieldObservers.get(field);
        if (observers != null && !observers.isEmpty()) {
            for (Observer<?> observer : observers) {
                Observer<T> typedObserver = (Observer<T>) observer;
                typedObserver.notify(oldValue, newValue);
            }
        }
    }
}