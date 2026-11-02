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

package top.byteeeee.fuzz.observers.rule.chestOptimization;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.server.packs.repository.PackRepository;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.settings.Observer;
import top.byteeeee.fuzz.utils.BuiltinResourcesPackAdder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ChestOptimizationObserver extends Observer<Boolean> {
    @Override
    public void onValueChange(FabricClientCommandSource source, Boolean oldValue, Boolean newValue) {
        PackRepository packManager = FuzzModClient.minecraftClient.getResourcePackRepository();
        net.minecraft.client.Options options = FuzzModClient.minecraftClient.options;
        String id = BuiltinResourcesPackAdder.chestPackId();
        packManager.reload();
        List<String> selectedPacks = new ArrayList<>(packManager.getSelectedIds());
        selectedPacks.removeIf(packId -> Objects.equals(packId, id));
        options.resourcePacks.removeIf(packId -> Objects.equals(packId, id));
        options.incompatibleResourcePacks.removeIf(packId -> Objects.equals(packId, id));

        if (newValue && packManager.isAvailable(id)) {
            selectedPacks.add(id);
            options.resourcePacks.add(id);
        }

        packManager.setSelected(selectedPacks);
        options.save();
        FuzzModClient.minecraftClient.reloadResourcePacks();
    }
}
