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

package top.byteeeee.fuzz.commands.fuzzCommands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.settings.Rule;
import top.byteeeee.fuzz.translations.LanguageJudge;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.ClickEventUtil;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.HoverEventUtil;
import top.byteeeee.fuzz.utils.Messenger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("LoggingSimilarMessage")
@Environment(EnvType.CLIENT)
public abstract class FuzzCommandContext {
    protected static final Translator tr = new Translator("command");

    public static int showFunctionList(FabricClientCommandSource source) {
        List<MutableText> messages = new ArrayList<>();
        messages.add(tr.tr("enable_function").formatted(Formatting.AQUA, Formatting.BOLD));
        messages.add(tr.tr("mod_version", FuzzModClient.VERSION).formatted(Formatting.GRAY));

        boolean hasEnabledFunctions = false;
        for (Field field : FuzzSettings.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Rule.class)) {
                try {
                    field.setAccessible(true);
                    Object currentValue = field.get(null);
                    Object defaultValue = FuzzSettings.DEFAULT_VALUES.get(field.getName());
                    if (!Objects.equals(currentValue, defaultValue)) {
                        MutableText mainText = functionEntryText(field, currentValue);
                        messages.add(mainText);
                        hasEnabledFunctions = true;
                    }
                } catch (IllegalAccessException e) {
                    FuzzModClient.LOGGER.error("Unable to access field {}: {}", field.getName(), e.getMessage());
                }
            }
        }

        if (!hasEnabledFunctions) {
            messages.add(Messenger.s("· · · · · ·").formatted(Formatting.GRAY));
        }

        messages.add(FuzzCategories.tr.tr("categories_list").formatted(Formatting.GOLD, Formatting.BOLD));
        messages.add(FuzzCategories.showCategories());
        messages.add(tr.tr("use_help").formatted(Formatting.ITALIC, Formatting.GRAY));
        messages.forEach(message -> Messenger.tell(source, message));
        return 1;
    }

    public static int showAllFunctions(FabricClientCommandSource source) {
        List<MutableText> messages = new ArrayList<>();
        messages.add(tr.tr("all_functions").formatted(Formatting.AQUA, Formatting.BOLD));

        List<Field> allFields = Arrays.stream(FuzzSettings.class.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(Rule.class))
            .sorted(Comparator.comparing(Field::getName))
            .collect(Collectors.toList());

        boolean hasAnyFunction = false;
        for (Field field : allFields) {
            try {
                field.setAccessible(true);
                Object currentValue = field.get(null);
                messages.add(functionEntryText(field, currentValue));
                hasAnyFunction = true;
            } catch (IllegalAccessException e) {
                FuzzModClient.LOGGER.error("Field access error: {}", field.getName(), e);
            }
        }

        if (!hasAnyFunction) {
            messages.add(Messenger.s("· · · · · ·").formatted(Formatting.GRAY));
        }

        messages.add(tr.tr("use_help").formatted(Formatting.ITALIC, Formatting.GRAY));
        messages.forEach(message -> Messenger.tell(source, message));
        return 1;
    }

    public static MutableText functionEntryText(Field field, Object value) {
        MutableText valueDisplay = optionText(field, value);
        String funcNameTrKey = tr.getFuncNameTrKey(field.getName());

        return Messenger.s("")
            .append(Messenger.s("- ").formatted(Formatting.WHITE))
            .append(Messenger.tr(funcNameTrKey))
            .append(LanguageJudge.isEnglish() ? Messenger.s(" ") : Messenger.s(" (" + field.getName() + ") "))
            .styled(style -> style.withHoverEvent(HoverEventUtil.event(
                HoverEventUtil.SHOW_TEXT,
                Messenger.tr(tr.getFuncDescTrKey(field.getName())).formatted(Formatting.YELLOW)
            )).withClickEvent(
                ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz " + field.getName())
            ))
            .append(valueDisplay);
    }

    public static int showFunctionInfo(FabricClientCommandSource source, Field field) {
        List<MutableText> messages = new ArrayList<>();

        try {
            field.setAccessible(true);
            Object currentValue = field.get(null);
            Object defaultValue = FuzzSettings.DEFAULT_VALUES.get(field.getName());
            Rule annotation = field.getAnnotation(Rule.class);

            MutableText nameLine = Messenger.s("")
                .append(Messenger.tr(tr.getFuncNameTrKey(field.getName()))).formatted(Formatting.BOLD)
                .append(LanguageJudge.isEnglish() ? Messenger.s("") : Messenger.s(" (" + field.getName() + ")").formatted(Formatting.BOLD));
            messages.add(nameLine);

            MutableText descLine = Messenger.tr(tr.getFuncDescTrKey(field.getName()));
            messages.add(descLine);

            int extraIndex = 0;
            while (true) {
                String extraKey = "fuzz.settings." + field.getName() + ".extra." + extraIndex;
                String extraText = Messenger.tr(extraKey).getString();
                if (extraKey.equals(extraText)) {
                    break;
                }
                messages.add(Messenger.s(extraText).formatted(Formatting.GRAY));
                extraIndex++;
            }

            MutableText categoriesLine = tr.tr("function_info_categories").formatted(Formatting.WHITE);
            for (String category : annotation.categories()) {
                Text hoverText;
                if (LanguageJudge.isEnglish()) {
                    hoverText = tr.tr("function_info_click_to_view", FuzzCategories.tr.tr(category)).formatted(Formatting.YELLOW);
                } else {
                    hoverText = tr.tr("function_info_click_to_view", FuzzCategories.tr.tr(category), category).formatted(Formatting.YELLOW);
                }
                categoriesLine.append(
                    Messenger.s("[" + FuzzCategories.tr.tr(category).getString() + "]")
                    .formatted(Formatting.AQUA)
                    .styled(style -> style
                    .withClickEvent(ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz list " + category))
                    .withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, hoverText)))).append(" ");
            }
            messages.add(categoriesLine);

            MutableText currentValueLine = tr.tr("current_value")
                .append(Messenger.s(currentValue.toString())
                .append(
                    Objects.equals(currentValue, defaultValue) ?
                    Messenger.s(" ✔")
                        .styled(style -> style
                            .withHoverEvent(
                                HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, tr.tr("info_default_value")
                                .formatted(Formatting.DARK_GREEN))
                            )
                            .withColor(Formatting.GREEN)) :
                    Messenger.s(" ✎")
                        .styled(style -> style
                            .withHoverEvent(
                                HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, tr.tr("info_modified_value")
                                .formatted(Formatting.DARK_RED))
                            )
                            .withColor(Formatting.GOLD))
                )
                .formatted(Formatting.AQUA));
            messages.add(currentValueLine);

            if (annotation.options().length > 0 || field.getType() == boolean.class) {
                MutableText optionsLine = tr.tr("options_option").append(optionText(field, currentValue));
                messages.add(optionsLine);
            }

            messages.forEach(message -> Messenger.tell(source, message));
            return 1;
        } catch (IllegalAccessException e) {
            FuzzModClient.LOGGER.error("Unable to access field {}: {}", field.getName(), e.getMessage());
            return 0;
        }
    }

    public static MutableText optionText(Field field, Object value) {
        Rule annotation = field.getAnnotation(Rule.class);
        String[] options = annotation.options();
        Object defaultValueObj = FuzzSettings.DEFAULT_VALUES.get(field.getName());
        String defaultValue = defaultValueObj != null ? defaultValueObj.toString() : "";
        String currentValue = value.toString();
        boolean isDefaultState = currentValue.equals(defaultValue);

        if (options.length == 0 && field.getType() == boolean.class) {
            options = new String[]{"false", "true"};
        } else if(options.length == 0) {
            options = new String[]{defaultValue};
        }

        MutableText optionsText = Messenger.s("");
        boolean hasCustomValue = false;

        for (String option : options) {
            boolean isCurrent = option.equals(currentValue);
            boolean isDefaultOption = option.equals(defaultValue);
            Formatting color;
            boolean underline;

            if (isDefaultState) {
                color = Formatting.GRAY;
                underline = isDefaultOption;
            } else {
                color = isDefaultOption ? Formatting.DARK_GREEN : Formatting.YELLOW;
                underline = isCurrent;
            }

            MutableText button = Messenger.s("[" + option + "]")
                .formatted(color)
                .styled(style -> style
                    .withUnderline(underline)
                    .withClickEvent(ClickEventUtil.event(
                        ClickEventUtil.RUN_COMMAND,
                        "/fuzz " + field.getName() + " " + option
                    ))
                    .withHoverEvent(HoverEventUtil.event(
                        HoverEventUtil.SHOW_TEXT,
                        Messenger.s(
                            isDefaultOption ?
                            tr.tr("default_value").getString() + option :
                            tr.tr("set_to").getString() + option
                        ).formatted(Formatting.GREEN)
                    ))
                );
            optionsText.append(button).append(" ");
            hasCustomValue = hasCustomValue || isCurrent;
        }

        if (!hasCustomValue && !isDefaultState) {
            MutableText customButton = Messenger.s("[" + currentValue + "]")
                .formatted(Formatting.YELLOW)
                .styled(style -> style
                    .withUnderline(true)
                    .withClickEvent(
                        ClickEventUtil.event(
                        ClickEventUtil.RUN_COMMAND,
                        "/fuzz " + field.getName() + " " + currentValue
                    ))
                    .withHoverEvent(HoverEventUtil.event(
                        HoverEventUtil.SHOW_TEXT,
                        Messenger.s(tr.tr("custom_value").getString() + currentValue).formatted(Formatting.GREEN)
                    ))
                );
            optionsText.append(customButton).append(" ");
        }

        return optionsText;
    }
}
