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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.commands.AbstractRuleCommand;
import top.byteeeee.fuzz.config.rule.commandAnimatedFreeze.CommandAnimatedFreezeConfig;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.Layout;
import top.byteeeee.fuzz.utils.Messenger;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AnimatedFreezeCommand extends AbstractRuleCommand {
    private static final AnimatedFreezeCommand INSTANCE = new AnimatedFreezeCommand();
    private static final List<String> EXTRA_SUGGESTIONS = new ArrayList<>();
    private static final Translator tr = new Translator("commandAnimatedFreeze");
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
            .then(suggestions(StringArgumentType.string())
            .executes(c -> checkEnabled(c, () -> add(
                c.getSource(),
                StringArgumentType.getString(c, "block")
            )))))

            // remove
            .then(ClientCommands.literal("remove")
            .then(suggestions(StringArgumentType.string())
            .executes(c -> checkEnabled(c, () -> remove(
                c.getSource(),
                StringArgumentType.getString(c, "block")
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
        return
            ClientCommands.argument("block", type).suggests(
                (context, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase();

                    for (Identifier id : BuiltInRegistries.BLOCK.keySet()) {
                        String suggestion = id.toString().replace("minecraft:", "");
                        if (remaining.isEmpty() || suggestion.toLowerCase().startsWith(remaining)) {
                            builder.suggest(suggestion);
                        }
                    }

                    for (String extra : EXTRA_SUGGESTIONS) {
                        if (remaining.isEmpty() || extra.toLowerCase().startsWith(remaining)) {
                            builder.suggest(extra);
                        }
                    }

                    return builder.buildFuture();
                }
            );
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
        Messenger.tell(Messenger.f(Messenger.c(tr.tr("list_head"), Messenger.endl(), Messenger.sline()), Layout.AQUA));
        for (String blockName : FuzzSettings.animationDisableList) {
            Messenger.tell(source, Messenger.f(Messenger.s(blockName), Layout.AQUA));
        }
        return 1;
    }

    private static int help(FabricClientCommandSource source) {
        return 1;
    }

    private static void saveToConfigAndReload() {
        CommandAnimatedFreezeConfig.getInstance().saveToJson(FuzzSettings.animationDisableList);
        FuzzModClient.minecraftClient.reloadResourcePacks();
    }

    static {
        EXTRA_SUGGESTIONS.add("water_still");
        EXTRA_SUGGESTIONS.add("water_flow");
        EXTRA_SUGGESTIONS.add("lava_still");
        EXTRA_SUGGESTIONS.add("lava_flow");
        EXTRA_SUGGESTIONS.add("fire_0");
        EXTRA_SUGGESTIONS.add("fire_1");
        EXTRA_SUGGESTIONS.add("soul_fire_0");
        EXTRA_SUGGESTIONS.add("soul_fire_1");
        EXTRA_SUGGESTIONS.add("campfire_fire");
        EXTRA_SUGGESTIONS.add("campfire_log_lit");
        EXTRA_SUGGESTIONS.add("soul_campfire_fire");
        EXTRA_SUGGESTIONS.add("soul_campfire_log_lit");
        EXTRA_SUGGESTIONS.add("tall_seagrass_bottom");
        EXTRA_SUGGESTIONS.add("tall_seagrass_top");
    }
}
