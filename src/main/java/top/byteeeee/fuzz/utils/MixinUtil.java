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

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.network.chat.MutableComponent;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;

import top.byteeeee.fuzz.FuzzModClient;

public class MixinUtil {
    public static boolean audit(@Nullable FabricClientCommandSource source) {
        boolean ok;
        MutableComponent response;
        try {
            MixinEnvironment.getCurrentEnvironment().audit();
            response = Messenger.s("Mixin environment audited successfully");
            ok = true;
        } catch (Exception e) {
            FuzzModClient.LOGGER.error("Error when auditing mixin", e);
            response = Messenger.s(String.format("Mixin environment auditing failed, check console for more information (%s)", e));
            ok = false;
        }
        if (source != null) {
            Messenger.tell(source, response);
        }
        return ok;
    }
}