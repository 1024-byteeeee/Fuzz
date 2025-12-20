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

package top.byteeeee.fuzz.commands.fuzzCommands.context;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.ChatFormatting;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.settings.Rule;
import top.byteeeee.fuzz.translations.FuzzTranslations;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.ClickEventUtil;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.HoverEventUtil;
import top.byteeeee.fuzz.utils.Messenger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class FuzzCategoriesContext {
    protected static final Translator tr = new Translator("categories");

    protected static MutableComponent showCategories() {
        Set<String> categories = getAllCategories();
        return createCategoryButtons(categories);
    }

    public static Set<String> getAllCategories() {
        return 
            Arrays.stream(FuzzSettings.class.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Rule.class))
            .map(field -> field.getAnnotation(Rule.class).categories())
            .flatMap(Arrays::stream)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static int showFunctionListByCategory(FabricClientCommandSource source, String category) {
        List<MutableComponent> messages = new ArrayList<>();
        addCategoryTitle(messages, category);
        addCategoryFunctions(messages, category);
        messages.forEach(message -> Messenger.tell(source, message));
        return 1;
    }

    private static MutableComponent createCategoryButtons(Set<String> categories) {
        MutableComponent categoryButtons = Messenger.s("");
        categories.forEach(category -> {
            MutableComponent buttonText = createCategoryButton(category);
            categoryButtons.append(buttonText).append(" ");
        });
        return categoryButtons;
    }

    private static MutableComponent createCategoryButton(String category) {
        return
            Messenger.s(String.format("[%s]", tr.tr(category).getString()))
            .withStyle(style -> style
            .withColor(ChatFormatting.AQUA)
            .withClickEvent(createClickEvent(category))
            .withHoverEvent(createHoverEvent(category)));
    }

    private static ClickEvent createClickEvent(String category) {
        return ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz list " + category);
    }

    private static HoverEvent createHoverEvent(String category) {
        return HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, createHoverText(category));
    }

    private static MutableComponent createHoverText(String category) {
        if (FuzzTranslations.isEnglish()) {
            return tr.tr("click_to_view", tr.tr(category)).withStyle(ChatFormatting.YELLOW);
        }
        return tr.tr("click_to_view", tr.tr(category), category).withStyle(ChatFormatting.YELLOW);
    }

    private static void addCategoryTitle(List<MutableComponent> messages, String category) {
        messages.add(tr.tr("list_title_for_category", tr.tr(category)).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
    }

    private static void addCategoryFunctions(List<MutableComponent> messages, String category) {
        getRuleFieldsForCategory(category).forEach(field -> addFunctionEntry(messages, field));
    }

    private static List<Field> getRuleFieldsForCategory(String category) {
        return
            Arrays.stream(FuzzSettings.class.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Rule.class))
            .filter(field -> Arrays.asList(field.getAnnotation(Rule.class).categories()).contains(category))
            .collect(Collectors.toList());
    }

    private static void addFunctionEntry(List<MutableComponent> messages, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(null);
            messages.add(FuzzCommandContext.ruleEntryText(field, value));
        } catch (IllegalAccessException e) {
            FuzzModClient.LOGGER.error("Error accessing field {}: {}", field.getName(), e.getMessage());
        }
    }
}
