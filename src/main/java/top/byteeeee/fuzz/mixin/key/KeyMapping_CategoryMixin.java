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

package top.byteeeee.fuzz.mixin.key;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import top.byteeeee.fuzz.utils.IdentifierUtil;
import top.byteeeee.fuzz.utils.Messenger;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.KeyMapping$Category")
public abstract class KeyMapping_CategoryMixin {
    @Shadow
    @Final
    private Identifier id;

    @WrapMethod(method = "label")
    private Component getLabel(Operation<Component> original) {
        if (this.id.equals(IdentifierUtil.of("fuzz", "fuzz"))) {
            return Messenger.tr("fuzz.key.category.fuzz");
        } else {
            return original.call();
        }
    }
}
