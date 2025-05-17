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

package top.byteeeee.kkk.commands.function.commandHighLightEntity;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.utils.Messenger;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class HighLightEntityCommand {
    private static final SuggestionProvider<FabricClientCommandSource> ENTITY_SUGGESTIONS =
        (context, builder) -> {
            Registry.ENTITY_TYPE.forEach(entityType -> {
                Identifier id = EntityType.getId(entityType);
                builder.suggest(id.toString());   // minecraft:creeper
            });
            return CompletableFuture.completedFuture(builder.build());
        };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            ClientCommandManager.literal("highlightEntity")
            .requires(s -> s.hasPermissionLevel(0))
            // Add entity command
            .then(ClientCommandManager.literal("add")
            .then(ClientCommandManager.argument("entity", StringArgumentType.greedyString())
            .suggests(ENTITY_SUGGESTIONS)
            .executes(context -> add(
                context.getSource(),
                StringArgumentType.getString(context, "entity")
            ))))

            // Remove entity command
            .then(ClientCommandManager.literal("remove")
            .then(ClientCommandManager.argument("entity", StringArgumentType.greedyString())
            .suggests((context, builder) -> {
                KKKSettings.highlightedEntities.forEach(builder::suggest);
                return CompletableFuture.completedFuture(builder.build());
            })
            .executes(context -> remove(
                context.getSource(),
                StringArgumentType.getString(context, "entity")
            ))))

            // Clear all entities
            .then(ClientCommandManager.literal("clear")
            .executes(context -> clear(context.getSource())))

            // List entities
            .then(ClientCommandManager.literal("list")
            .executes(context -> list(context.getSource())))

            // Show help
            .then(ClientCommandManager.literal("help")
            .executes(context -> help(context.getSource())))
        );
    }

    private static int add(FabricClientCommandSource source, String entity) {
        if (KKKSettings.highlightedEntities.contains(entity)) {
            source.sendError(Messenger.s("实体 " + entity + " 已在列表中"));
            return 0;
        }
        KKKSettings.highlightedEntities.add(entity);
        source.sendFeedback(Messenger.s("已添加高亮实体: " + entity));
        return 1;
    }

    private static int remove(FabricClientCommandSource source, String entity) {
        if (!KKKSettings.highlightedEntities.remove(entity)) {
            source.sendError(Messenger.s("实体 " + entity + " 不在列表中"));
            return 0;
        }

        source.sendFeedback(Messenger.s("已移除高亮实体: " + entity));
        return 1;
    }

    private static int clear(FabricClientCommandSource source) {
        int count = KKKSettings.highlightedEntities.size();
        KKKSettings.highlightedEntities.clear();
        source.sendFeedback(Messenger.s("已清除所有高亮实体 (" + count + " 个)"));
        return 1;
    }

    private static int list(FabricClientCommandSource source) {
        List<String> entities = KKKSettings.highlightedEntities;

        if (entities.isEmpty()) {
            source.sendFeedback(Messenger.s("当前没有高亮实体"));
            return 0;
        }

        source.sendFeedback(Messenger.s("高亮实体列表 (" + entities.size() + "):"));
        for (String entity : entities) {
            source.sendFeedback(Messenger.s("- " + entity));
        }
        return 1;
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(Messenger.s("高亮实体命令使用说明:"));
        source.sendFeedback(Messenger.s("/highLight add <实体ID> - 添加高亮实体"));
        source.sendFeedback(Messenger.s("/highLight remove <实体ID> - 移除高亮实体"));
        source.sendFeedback(Messenger.s("/highLight clear - 清除所有高亮实体"));
        source.sendFeedback(Messenger.s("/highLight list - 列出所有高亮实体"));
        return 1;
    }
}