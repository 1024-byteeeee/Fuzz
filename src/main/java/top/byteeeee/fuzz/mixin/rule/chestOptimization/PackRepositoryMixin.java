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

package top.byteeeee.fuzz.mixin.rule.chestOptimization;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.BuiltinResourcesPackAdder;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Mixin(value = PackRepository.class, priority = 16888)
public abstract class PackRepositoryMixin {
    @Shadow
    public abstract boolean isAvailable(String id);

    @ModifyVariable(method = "rebuildSelected", at = @At("HEAD"), argsOnly = true)
    private Collection<String> syncChestPack(Collection<String> selectedIds) {
        String id = BuiltinResourcesPackAdder.chestPackId();
        Collection<String> selected = new ArrayList<>(selectedIds);
        selected.removeIf(packId -> Objects.equals(packId, id));
        if (FuzzSettings.chestOptimization && isAvailable(id)) {
            selected.add(id);
        }
        return selected;
    }

    @ModifyReturnValue(method = "getAvailablePacks()Ljava/util/Collection;", at = @At("RETURN"))
    private Collection<Pack> modifyAvailablePacks(Collection<Pack> original) {
        return original.stream().filter(pack -> !Objects.equals(pack.getId(), top.byteeeee.fuzz.utils.BuiltinResourcesPackAdder.chestPackId())).collect(Collectors.toList());
    }
}
