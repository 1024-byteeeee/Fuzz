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

package top.byteeeee.fuzz.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

public class EntityUtil {
    public static World getEntityWorld(@NotNull Entity entity) {
        //#if MC>=12106 && MC<12109
        //$$ return entity.getWorld();
        //#else
        return entity.getEntityWorld();
        //#endif
    }

    public static Vec3d getEntityPos(@NotNull Entity entity) {
        //#if MC>=12109
        //$$ return entity.getEntityPos();
        //#else
        return entity.getPos();
        //#endif
    }
}
