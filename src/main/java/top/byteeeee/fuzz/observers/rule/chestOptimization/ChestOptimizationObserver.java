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

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackRepository;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.settings.Observer;
import top.byteeeee.fuzz.utils.IdentifierUtil;

@Environment(EnvType.CLIENT)
public class ChestOptimizationObserver extends Observer<Boolean> {
    private static final Identifier PACK_ID = IdentifierUtil.of("fuzz_mod", "chest_optimization");

    @Override
    public void onValueChange(FabricClientCommandSource source, Boolean oldValue, Boolean newValue) {
        PackRepository packManager = FuzzModClient.minecraftClient.getResourcePackRepository();
        var options = FuzzModClient.minecraftClient.options;
        String id = PACK_ID.toString();
        boolean repoHas = packManager.isAvailable(id);
        boolean selected = options.resourcePacks.contains(id);

        if (FuzzSettings.chestOptimization && !selected && repoHas) {
            packManager.addPack(id);
            options.resourcePacks.add(id);
        }

        if (!FuzzSettings.chestOptimization && selected) {
            packManager.removePack(id);
            options.resourcePacks.remove(id);
        }

        options.save();
        FuzzModClient.minecraftClient.reloadResourcePacks();
    }
}
