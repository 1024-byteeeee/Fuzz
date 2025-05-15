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

package top.byteeeee.kkk.utils;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.text.BaseText;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;

import top.byteeeee.kkk.KKKModClient;

public class MixinUtil {
    public static boolean audit(@Nullable FabricClientCommandSource source) {
        boolean ok;
        BaseText response;
        try {
            MixinEnvironment.getCurrentEnvironment().audit();
            response = Messenger.s("Mixin environment audited successfully");
            ok = true;
        } catch (Exception e) {
            KKKModClient.LOGGER.error("Error when auditing mixin", e);
            response = Messenger.s(String.format("Mixin environment auditing failed, check console for more information (%s)", e));
            ok = false;
        }
        if (source != null) {
            Messenger.tell(source, response);
        }
        return ok;
    }
}