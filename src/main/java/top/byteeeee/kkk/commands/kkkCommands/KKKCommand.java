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

package top.byteeeee.kkk.commands.kkkCommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.settings.KKKFunction;
import top.byteeeee.kkk.commands.argumentHandler.ArgumentHandlerInterface;
import top.byteeeee.kkk.commands.argumentHandler.ArgumentHandlerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class KKKCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> main = ClientCommandManager.literal("kkk")
        .executes(ctx -> KKKCommandContext.showFunctionList(ctx.getSource()));
        LiteralArgumentBuilder<FabricClientCommandSource> listCommand = ClientCommandManager.literal("list")
        .executes(ctx -> KKKCommandContext.showAllFunctions(ctx.getSource()))
        .then(ClientCommandManager.argument("category", StringArgumentType.string())
        .suggests((context, builder) -> {
            KKKCategories.getAllCategories().forEach(builder::suggest);
            return CompletableFuture.completedFuture(builder.build());
        })
        .executes(ctx -> {
            String category = StringArgumentType.getString(ctx, "category");
            return KKKCategories.showFunctionListByCategory(ctx.getSource(), category);
        }));
        main.then(listCommand);
        Arrays.stream(KKKSettings.class.getDeclaredFields())
        .filter(f -> f.isAnnotationPresent(KKKFunction.class))
        .forEach(field -> {
            LiteralArgumentBuilder<FabricClientCommandSource> cmd = ClientCommandManager.literal(field.getName())
            .executes(context -> {
                KKKCommandContext.showFunctionList(context.getSource());
                KKKCommandContext.showFunctionInfo(context.getSource(), field);
                return 1;
            });
            ArgumentHandlerInterface<?> handler = ArgumentHandlerFactory.create(field.getType());
            handler.configureArgument(cmd, field);
            main.then(cmd);
        });
        dispatcher.register(main);
    }
}
