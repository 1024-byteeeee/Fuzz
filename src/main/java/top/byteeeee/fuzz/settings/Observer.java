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

import java.util.Objects;

@Environment(EnvType.CLIENT)
public abstract class Observer<T> {
    public void notify(T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            onValueChange(oldValue, newValue);
        }
    }

    public abstract void onValueChange(T oldValue, T newValue);
}
