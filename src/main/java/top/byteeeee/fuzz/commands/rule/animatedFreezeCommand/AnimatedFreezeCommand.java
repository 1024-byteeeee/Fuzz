/*
 * This file is part of the Kaleidoscope project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024 1024_byteeeee and contributors
 *
 * Kaleidoscope is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kaleidoscope is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Kaleidoscope. If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.fuzz.commands.rule.animatedFreezeCommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.commands.AbstractRuleCommand;
import top.byteeeee.fuzz.commands.suggestionProviders.ListSuggestionProvider;
import top.byteeeee.fuzz.config.rule.commandAnimatedFreeze.CommandAnimatedFreezeConfig;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.Layout;
import top.byteeeee.fuzz.utils.Messenger;

@Environment(EnvType.CLIENT)
public class AnimatedFreezeCommand extends AbstractRuleCommand {
    private static final AnimatedFreezeCommand INSTANCE = new AnimatedFreezeCommand();
    private static final Translator tr = new Translator("command.animatedFreeze");
    private static final String MAIN_CMD_NAME = "animatedFreeze";
    private static final String RULE_NAME = "commandAnimatedFreeze";

    public static AnimatedFreezeCommand getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean getCondition() {
        return FuzzSettings.commandAnimatedFreeze;
    }

    @Override
    protected String getRuleName() {
        return RULE_NAME;
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            ClientCommands.literal(MAIN_CMD_NAME)
            // add
            .then(ClientCommands.literal("add")
            .then(suggestions(StringArgumentType.greedyString())
            .executes(c -> checkEnabled(c, () -> add(
                c.getSource(),
                StringArgumentType.getString(c, "texture")
            )))))

            // remove
            .then(ClientCommands.literal("remove")
            .then(ClientCommands.argument("texture", StringArgumentType.greedyString())
            .suggests(ListSuggestionProvider.of(FuzzSettings.animationDisableList))
            .executes(c -> checkEnabled(c, () -> remove(
                c.getSource(),
                StringArgumentType.getString(c, "texture")
            )))))

            // removeAll
            .then(ClientCommands.literal("removeAll")
            .executes(c -> checkEnabled(c, () -> removeAll(c.getSource()))))

            // list
            .then(ClientCommands.literal("list")
            .executes(c -> checkEnabled(c, () -> list(c.getSource()))))

            // help
            .then(ClientCommands.literal("help")
            .executes(c -> checkEnabled(c, () -> help(c.getSource()))))
        );
    }

    private static RequiredArgumentBuilder<FabricClientCommandSource, String> suggestions(StringArgumentType type) {
        return ClientCommands.argument("texture", type).suggests((context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            Minecraft client = ClientUtil.getCurrentClient();
            ResourceManager resourceManager = client.getResourceManager();

            resourceManager.listResources("textures", id -> true).forEach((id, resource) -> {
                String suggestion = id.getPath();

                int dot = suggestion.lastIndexOf('.');
                if (dot != -1) {
                    suggestion = suggestion.substring(0, dot);
                }

                int slash = suggestion.lastIndexOf('/');
                if (slash != -1) {
                    suggestion = suggestion.substring(slash + 1);
                }

                if (suggestion.matches("\\d+")) {
                    return;
                }

                if (remaining.isEmpty() || suggestion.toLowerCase().startsWith(remaining)) {
                    builder.suggest(suggestion);
                }
            });

            return builder.buildFuture();
        });
    }

    private static int add(FabricClientCommandSource source, String blockName) {
        if (!FuzzSettings.animationDisableList.contains(blockName)) {
            FuzzSettings.animationDisableList.add(blockName);
            saveToConfigAndReload();
            Messenger.tell(source, Messenger.f(tr.tr("add_success", blockName), Layout.GREEN));
            return 1;
        } else {
            Messenger.tell(source, Messenger.f(tr.tr("add_fail", blockName), Layout.YELLOW));
            return 0;
        }
    }

    private static int remove(FabricClientCommandSource source, String blockName) {
        if (FuzzSettings.animationDisableList.contains(blockName)) {
            FuzzSettings.animationDisableList.remove(blockName);
            saveToConfigAndReload();
            Messenger.tell(source, Messenger.f(tr.tr("remove_success", blockName), Layout.GREEN));
            return 1;
        } else {
            Messenger.tell(source, Messenger.f(tr.tr("remove_fail", blockName), Layout.YELLOW));
            return 0;
        }
    }

    private static int removeAll(FabricClientCommandSource source) {
        if (!FuzzSettings.animationDisableList.isEmpty()) {
            FuzzSettings.animationDisableList.clear();
            saveToConfigAndReload();
            Messenger.tell(source, Messenger.f(tr.tr("removeAll_success"), Layout.GREEN));
            return 1;
        } else {
            Messenger.tell(source, Messenger.f(tr.tr("removeAll_fail"), Layout.YELLOW));
            return 0;
        }
    }

    private static int list(FabricClientCommandSource source) {
        Messenger.tell(Messenger.f(Messenger.c(tr.tr("list_head"), Messenger.endl(), Messenger.sline()), Layout.AQUA), false);

        for (String blockName : FuzzSettings.animationDisableList) {
            Messenger.tell(source, Messenger.f(Messenger.s(blockName), Layout.AQUA));
        }

        return 1;
    }

    private static int help(FabricClientCommandSource source) {
        Messenger.tell(source, Messenger.f(Messenger.c(
            tr.tr("help.add"), Messenger.endl(),
            tr.tr("help.remove"), Messenger.endl(),
            tr.tr("help.removeAll"), Messenger.endl(),
            tr.tr("help.list"), Messenger.endl()
        ), Layout.GRAY));
        return 1;
    }

    private static void saveToConfigAndReload() {
        CommandAnimatedFreezeConfig.getInstance().saveToJson(FuzzSettings.animationDisableList);
        FuzzModClient.minecraftClient.reloadResourcePacks();
    }
}
