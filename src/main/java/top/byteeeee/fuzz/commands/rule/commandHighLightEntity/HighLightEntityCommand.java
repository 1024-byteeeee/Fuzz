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

package top.byteeeee.fuzz.commands.rule.commandHighLightEntity;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.commands.AbstractRuleCommand;
import top.byteeeee.fuzz.commands.suggestionProviders.ListSuggestionProvider;
import top.byteeeee.fuzz.commands.suggestionProviders.SetSuggestionProvider;
import top.byteeeee.fuzz.config.rule.commandHighlightEntities.CommandHighlightEntitiesConfig;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.Layout;
import top.byteeeee.fuzz.utils.Messenger;

import java.util.List;

@Environment(EnvType.CLIENT)
public class HighLightEntityCommand extends AbstractRuleCommand {
    private static final Translator tr = new Translator("command.highlightEntity");
    private static final HighLightEntityCommand INSTANCE = new HighLightEntityCommand();
    private static final String MAIN_CMD_NAME = "highlightEntity";
    private static final String RULE_NAME = "commandHighlightEntities";

    public static HighLightEntityCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            ClientCommands.literal(MAIN_CMD_NAME)
            // Add entity command
            .then(ClientCommands.literal("add")
            .then(ClientCommands.argument("entityId", StringArgumentType.greedyString())
            .suggests(SetSuggestionProvider.fromEntityRegistry())
            .executes(c -> checkEnabled(
                c, () -> add(c.getSource(), StringArgumentType.getString(c, "entityId"))
            ))))

            // Remove entity command
            .then(ClientCommands.literal("remove")
            .then(ClientCommands.argument("entityId", StringArgumentType.greedyString())
            .suggests(ListSuggestionProvider.of(FuzzSettings.highlightEntityList))
            .executes(c -> checkEnabled(
                c, () -> remove(c.getSource(), StringArgumentType.getString(c, "entityId"))
            ))))

            // Clear all entities
            .then(ClientCommands.literal("clear")
            .executes(c -> checkEnabled(c, () -> clear(c.getSource()))))

            // List entities
            .then(ClientCommands.literal("list")
            .executes(c -> checkEnabled(c, () -> list(c.getSource()))))

            // Show help
            .then(ClientCommands.literal("help")
            .executes(c -> checkEnabled(c, () -> help(c.getSource()))))
        );
    }

    @Override
    protected boolean getCondition() {
        return FuzzSettings.commandHighlightEntities;
    }

    @Override
    protected String getRuleName() {
        return RULE_NAME;
    }

    private static int add(FabricClientCommandSource source, String entity) {
        if (FuzzSettings.highlightEntityList.contains(entity)) {
            Messenger.tell(source, Messenger.f(tr.tr("already_in_list", entity), Layout.RED));
            return 0;
        }
        FuzzSettings.highlightEntityList.add(entity);
        saveToJson();
        Messenger.tell(source, Messenger.f(tr.tr("added", entity), Layout.GREEN));
        return 1;
    }

    private static int remove(FabricClientCommandSource source, String entity) {
        if (!FuzzSettings.highlightEntityList.remove(entity)) {
            Messenger.tell(source, Messenger.f(tr.tr("not_in_list", entity), Layout.RED));
            return 0;
        }
        saveToJson();
        Messenger.tell(source, Messenger.f(tr.tr("removed", entity), Layout.AQUA));
        return 1;
    }

    private static int clear(FabricClientCommandSource source) {
        FuzzSettings.highlightEntityList.clear();
        saveToJson();
        Messenger.tell(source, Messenger.f(tr.tr("cleared"), Layout.GREEN));
        return 1;
    }

    private static int list(FabricClientCommandSource source) {
        List<String> entities = FuzzSettings.highlightEntityList;

        if (entities.isEmpty()) {
            Messenger.tell(source, Messenger.f(tr.tr("list_is_empty"), Layout.YELLOW));
            return 0;
        }

        Messenger.tell(source, Messenger.f(tr.tr("list_title", entities.size()), Layout.AQUA, Layout.BOLD));

        for (String entity : entities) {
            Messenger.tell(source, Messenger.f(Messenger.s("- " + entity), Layout.WHITE));
        }

        return 1;
    }

    private static int help(FabricClientCommandSource source) {
        Messenger.tell(source, Messenger.f(Messenger.c(
            tr.tr("help.add"), Messenger.endl(),
            tr.tr("help.remove"), Messenger.endl(),
            tr.tr("help.clear"), Messenger.endl(),
            tr.tr("help.list"), Messenger.endl()
        ), Layout.GRAY));

        return 1;
    }

    private static void saveToJson() {
        CommandHighlightEntitiesConfig.getInstance().saveToJson(FuzzSettings.highlightEntityList);
    }
}
