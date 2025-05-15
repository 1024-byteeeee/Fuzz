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

package top.byteeeee.kkk.commands.argumentHandler;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.kkk.commands.suggestionStrategy.SuggestionStrategy;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class DoubleHandler extends AbstractArgumentHandler<Double> {
    @Override
    public ArgumentType<Double> getArgumentType() {
        return DoubleArgumentType.doubleArg();
    }

    @Override
    public void configureArgument(LiteralArgumentBuilder<FabricClientCommandSource> literal, Field field) {
        super.configureArgument(literal, field);
    }

    @Override
    public Double parseValue(CommandContext<FabricClientCommandSource> ctx) {
        return DoubleArgumentType.getDouble(ctx, "value");
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
        return new SuggestionStrategy().suggestOptions(builder, getAnnotationOptions());
    }
}