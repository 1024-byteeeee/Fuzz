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

package top.byteeeee.fuzz.commands.fuzzCommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.commands.fuzzCommands.context.FuzzCategoriesContext;
import top.byteeeee.fuzz.commands.fuzzCommands.context.FuzzCommandContext;
import top.byteeeee.fuzz.settings.Rule;
import top.byteeeee.fuzz.commands.fuzzCommands.argumentHandler.ArgumentHandlerInterface;
import top.byteeeee.fuzz.commands.fuzzCommands.argumentHandler.ArgumentHandlerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class FuzzCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(buildFuzzCommand("fuzz"));
        if (!Objects.equals(FuzzSettings.fuzzCommandAlias, "false")) {
            dispatcher.register(buildFuzzCommand(FuzzSettings.fuzzCommandAlias));
        }
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildFuzzCommand(String commandName) {
        LiteralArgumentBuilder<FabricClientCommandSource> main = ClientCommandManager.literal(commandName)
        .executes(ctx -> FuzzCommandContext.showRuleList(ctx.getSource()));
        LiteralArgumentBuilder<FabricClientCommandSource> listCommand = ClientCommandManager.literal("list")
        .executes(ctx -> FuzzCommandContext.showAllRules(ctx.getSource()))
        .then(ClientCommandManager.argument("category", StringArgumentType.string())
        .suggests((context, builder) -> {
            FuzzCategoriesContext.getAllCategories().forEach(builder::suggest);
            return CompletableFuture.completedFuture(builder.build());
        })
        .executes(ctx -> {
            String category = StringArgumentType.getString(ctx, "category");
            return FuzzCategoriesContext.showFunctionListByCategory(ctx.getSource(), category);
        }));

        main.then(listCommand);

        Arrays.stream(FuzzSettings.class.getDeclaredFields())
        .filter(f -> f.isAnnotationPresent(Rule.class))
        .forEach(field -> {
            LiteralArgumentBuilder<FabricClientCommandSource> cmd = ClientCommandManager.literal(field.getName())
                .executes(context -> {
                    FuzzCommandContext.showRuleList(context.getSource());
                    FuzzCommandContext.showRuleInfo(context.getSource(), field);
                    return 1;
                });
            ArgumentHandlerInterface<?> handler = ArgumentHandlerFactory.create(field.getType());
            handler.configureArgument(cmd, field);
            main.then(cmd);
        });

        return main;
    }
}
