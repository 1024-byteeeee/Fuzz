/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024 A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.kkk.commands.suggestionProviders;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class SetSuggestionProvider<E> implements SuggestionProvider<FabricClientCommandSource> {
    private final Set<E> options;

    public SetSuggestionProvider(Set<E> options) {
        this.options = options;
    }

    public static SetSuggestionProvider<?> of(Set<?> options) {
        return new SetSuggestionProvider<>(options);
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        options.forEach(option -> builder.suggest(option.toString()));
        return builder.buildFuture();
    }

    public static SetSuggestionProvider<String> fromEntityRegistry() {
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        for (Identifier id : Registry.ENTITY_TYPE.getIds()) {
            ids.add(id.toString());
        }
        return new SetSuggestionProvider<>(ids);
    }
}
