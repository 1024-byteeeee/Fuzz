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

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import top.byteeeee.fuzz.FuzzMod;
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

@SuppressWarnings("LoggingSimilarMessage")
@Environment(EnvType.CLIENT)
public abstract class FuzzCommandContext {
    protected static final Translator tr = new Translator("command");

    public static int showRuleList(FabricClientCommandSource source) {
        List<MutableText> messages = initializeMessageList();
        boolean hasEnabledRules = collectEnabledRules(messages);

        if (!hasEnabledRules) {
            messages.add(Messenger.s("· · · · · ·").formatted(Formatting.GRAY));
        }

        addCategoriesInfo(messages);
        messages.forEach(message -> Messenger.tell(source, message));
        return 1;
    }

    private static List<MutableText> initializeMessageList() {
        List<MutableText> messages = new ArrayList<>();
        messages.add(tr.tr("enable_rule").formatted(Formatting.AQUA, Formatting.BOLD));
        messages.add(tr.tr("mod_version", FuzzMod.getInstance().getVersion()).formatted(Formatting.GRAY));
        return messages;
    }

    private static boolean collectEnabledRules(List<MutableText> messages) {
        boolean hasEnabledRules = false;
        for (Field field : getRuleAnnotatedFields()) {
            try {
                field.setAccessible(true);
                Object currentValue = field.get(null);
                Object defaultValue = FuzzSettings.DEFAULT_VALUES.get(field.getName());
                if (!Objects.equals(currentValue, defaultValue)) {
                    messages.add(ruleEntryText(field, currentValue));
                    hasEnabledRules = true;
                }
            } catch (IllegalAccessException e) {
                FuzzModClient.LOGGER.error("Field access error: {}", field.getName(), e);
            }
        }
        return hasEnabledRules;
    }

    private static void addCategoriesInfo(List<MutableText> messages) {
        messages.add(FuzzCategoriesContext.tr.tr("categories_list").formatted(Formatting.GOLD, Formatting.BOLD));
        messages.add(FuzzCategoriesContext.showCategories());
        messages.add(tr.tr("use_help").formatted(Formatting.ITALIC, Formatting.GRAY));
    }

    public static int showAllRules(FabricClientCommandSource source) {
        List<MutableText> messages = new ArrayList<>();
        messages.add(tr.tr("all_rules").formatted(Formatting.AQUA, Formatting.BOLD));

        List<Field> sortedFields = getSortedRuleAnnotatedFields();
        boolean hasAnyRule = addRuleEntries(messages, sortedFields);

        if (!hasAnyRule) {
            messages.add(Messenger.s("· · · · · ·").formatted(Formatting.GRAY));
        }

        messages.add(tr.tr("use_help").formatted(Formatting.ITALIC, Formatting.GRAY));
        messages.forEach(message -> Messenger.tell(source, message));
        return 1;
    }

    public static MutableText ruleEntryText(Field field, Object value) {
        MutableText valueDisplay = optionText(field, value);
        String funcNameTrKey = tr.getRuleNameTrKey(field.getName());

        return
            Messenger.s("")
            .append(Messenger.s("- ").formatted(Formatting.WHITE))
            .append(Messenger.tr(funcNameTrKey))
            .append(createFieldNameText(field))
            .styled(style -> style.withHoverEvent(createHoverEvent(field)).withClickEvent(createClickEvent(field)))
            .append(valueDisplay);
    }

    private static MutableText createFieldNameText(Field field) {
        return FuzzTranslations.isEnglish() ? Messenger.s(" ") : Messenger.s(" (" + field.getName() + ") ");
    }

    private static HoverEvent createHoverEvent(Field field) {
        return HoverEventUtil.event(
            HoverEventUtil.SHOW_TEXT,
            Messenger.tr(tr.getRuleDescTrKey(field.getName())).formatted(Formatting.YELLOW)
        );
    }

    private static ClickEvent createClickEvent(Field field) {
        return ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz " + field.getName());
    }

    public static int showRuleInfo(FabricClientCommandSource source, Field field) {
        try {
            List<MutableText> messages = buildRuleInfoMessages(field);
            messages.forEach(message -> Messenger.tell(source, message));
            return 1;
        } catch (IllegalAccessException e) {
            FuzzModClient.LOGGER.error("Field access error: {}", field.getName(), e);
            return 0;
        }
    }

    private static List<MutableText> buildRuleInfoMessages(Field field) throws IllegalAccessException {
        List<MutableText> messages = new ArrayList<>();
        Object currentValue = getFieldValue(field);
        Object defaultValue = FuzzSettings.DEFAULT_VALUES.get(field.getName());
        Rule annotation = field.getAnnotation(Rule.class);

        addRuleHeader(messages, field);
        addRuleDescription(messages, field);
        addExtraInformation(messages, field);
        addCategoriesLine(messages, annotation);
        addValueInformation(messages, field, currentValue, defaultValue);

        return messages;
    }

    public static MutableText optionText(Field field, Object value) {
        Rule annotation = field.getAnnotation(Rule.class);
        String[] options = getOptions(field, annotation);
        String currentValue = value.toString();
        String defaultValue = getDefaultValue(field);
        boolean isDefaultState = currentValue.equals(defaultValue);

        MutableText optionsText = Messenger.s("");
        boolean hasCustomValue = false;

        for (String option : options) {
            boolean isCurrent = option.equals(currentValue);
            boolean isDefaultOption = option.equals(defaultValue);
            hasCustomValue = addOptionButton(optionsText, field, option, isCurrent, isDefaultOption, isDefaultState) || hasCustomValue;
        }

        if (!hasCustomValue && !isDefaultState) {
            appendCustomValueButton(optionsText, field, currentValue);
        }

        return optionsText;
    }

    private static List<Field> getRuleAnnotatedFields() {
        return Arrays.stream(FuzzSettings.class.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Rule.class)).collect(Collectors.toList());
    }

    private static List<Field> getSortedRuleAnnotatedFields() {
        return getRuleAnnotatedFields().stream().sorted(Comparator.comparing(Field::getName)).collect(Collectors.toList());
    }

    private static boolean addRuleEntries(List<MutableText> messages, List<Field> fields) {
        boolean hasAnyRule = false;
        for (Field field : fields) {
            try {
                Object currentValue = getFieldValue(field);
                messages.add(ruleEntryText(field, currentValue));
                hasAnyRule = true;
            } catch (IllegalAccessException e) {
                FuzzModClient.LOGGER.error("Field access error: {}", field.getName(), e);
            }
        }
        return hasAnyRule;
    }

    private static Object getFieldValue(Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(null);
    }

    private static void addRuleHeader(List<MutableText> messages, Field field) {
        MutableText nameLine = Messenger.s("")
            .append(Messenger.tr(tr.getRuleNameTrKey(field.getName())))
            .formatted(Formatting.BOLD)
            .append(createFieldNameText(field).formatted(Formatting.BOLD));

        messages.add(nameLine);
    }

    private static void addRuleDescription(List<MutableText> messages, Field field) {
        messages.add(Messenger.tr(tr.getRuleDescTrKey(field.getName())));
    }

    private static void addExtraInformation(List<MutableText> messages, Field field) {
        int i = 0;
        while (true) {
            String extraKey = "fuzz.settings." + field.getName() + ".extra." + i;
            String extraText = Messenger.tr(extraKey).getString();
            if (extraKey.equals(extraText)) {
                break;
            }
            messages.add(Messenger.s(extraText).formatted(Formatting.GRAY));
            i++;
        }
    }

    private static void addCategoriesLine(List<MutableText> messages, Rule annotation) {
        MutableText categoriesLine = tr.tr("rule_info_categories").formatted(Formatting.WHITE);
        for (String category : annotation.categories()) {
            categoriesLine.append(createCategoryButton(category)).append(" ");
        }
        messages.add(categoriesLine);
    }

    private static MutableText createCategoryButton(String category) {
        Text hoverText = createCategoryHoverText(category);
        return
            Messenger.s("[" + FuzzCategoriesContext.tr.tr(category).getString() + "]")
            .formatted(Formatting.AQUA)
            .styled(style -> style
            .withClickEvent(ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz " + "list " + category))
            .withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, hoverText)));
    }

    private static Text createCategoryHoverText(String category) {
        return
            FuzzTranslations.isEnglish() ?
            tr.tr("rule_info_click_to_view", FuzzCategoriesContext.tr.tr(category)).formatted(Formatting.YELLOW) :
            tr.tr("rule_info_click_to_view", FuzzCategoriesContext.tr.tr(category), category).formatted(Formatting.YELLOW);
    }

    private static void addValueInformation(List<MutableText> messages, Field field, Object currentValue, Object defaultValue) {
        messages.add(createCurrentValueLine(currentValue, defaultValue));
        Rule annotation = field.getAnnotation(Rule.class);
        if (annotation.options().length > 0 || field.getType() == boolean.class) {
            messages.add(tr.tr("options_option").append(optionText(field, currentValue)));
        }
    }

    private static MutableText createCurrentValueLine(Object currentValue, Object defaultValue) {
        return
            tr.tr("current_value")
            .append(Messenger.s(currentValue.toString())
            .append(createValueStateIcon(currentValue, defaultValue))
            .formatted(Formatting.AQUA));
    }

    private static MutableText createValueStateIcon(Object currentValue, Object defaultValue) {
        boolean isDefault = Objects.equals(currentValue, defaultValue);
        return isDefault ? createDefaultValueIcon() : createModifiedValueIcon();
    }

    private static MutableText createDefaultValueIcon() {
        return
            Messenger.s(" ✔")
            .styled(style -> style
            .withHoverEvent(HoverEventUtil.event(
                HoverEventUtil.SHOW_TEXT,
                tr.tr("info_default_value").formatted(Formatting.DARK_GREEN)))
            .withColor(Formatting.GREEN));
    }

    private static MutableText createModifiedValueIcon() {
        return
            Messenger.s(" ✎")
            .styled(style -> style
            .withHoverEvent(HoverEventUtil.event(
                HoverEventUtil.SHOW_TEXT,
                tr.tr("info_modified_value").formatted(Formatting.DARK_RED)
            ))
            .withColor(Formatting.GOLD));
    }

    private static String[] getOptions(Field field, Rule annotation) {
        String[] options = annotation.options();
        if (options.length == 0 && field.getType() == boolean.class) {
            return new String[]{"false", "true"};
        } else if (options.length == 0) {
            String defaultValue = getDefaultValue(field);
            return new String[]{defaultValue};
        }
        return options;
    }

    private static String getDefaultValue(Field field) {
        Object defaultValueObj = FuzzSettings.DEFAULT_VALUES.get(field.getName());
        return defaultValueObj != null ? defaultValueObj.toString() : "";
    }

    private static boolean addOptionButton(MutableText optionsText, Field field, String option, boolean isCurrent, boolean isDefaultOption, boolean isDefaultState) {
        Formatting color = determineOptionColor(isDefaultState, isDefaultOption);
        boolean underline = isDefaultState ? isDefaultOption : isCurrent;
        MutableText button = createOptionButton(field, option, isDefaultOption, color, underline);
        optionsText.append(button).append(" ");

        return isCurrent;
    }

    private static Formatting determineOptionColor(boolean isDefaultState, boolean isDefaultOption) {
        if (isDefaultState) {
            return Formatting.GRAY;
        }
        return isDefaultOption ? Formatting.DARK_GREEN : Formatting.YELLOW;
    }

    private static MutableText createOptionButton(Field field, String option, boolean isDefaultOption, Formatting color, boolean underline) {
        return
            Messenger.s("[" + option + "]")
            .formatted(color)
            .styled(style -> style
            .withUnderline(underline)
            .withClickEvent(createOptionClickEvent(field, option))
            .withHoverEvent(createOptionHoverEvent(option, isDefaultOption)));
    }

    private static ClickEvent createOptionClickEvent(Field field, String option) {
        return ClickEventUtil.event(
            ClickEventUtil.RUN_COMMAND,
            "/fuzz " + field.getName() + " " + option
        );
    }

    private static HoverEvent createOptionHoverEvent(String option, boolean isDefaultOption) {
        String message = isDefaultOption ? tr.tr("default_value").getString() + option : tr.tr("set_to").getString() + option;
        return HoverEventUtil.event(
            HoverEventUtil.SHOW_TEXT,
            Messenger.s(message).formatted(Formatting.GREEN)
        );
    }

    private static void appendCustomValueButton(MutableText optionsText, Field field, String currentValue) {
        MutableText customButton = createCustomValueButton(field, currentValue);
        optionsText.append(customButton).append(" ");
    }

    private static MutableText createCustomValueButton(Field field, String currentValue) {
        return
            Messenger.s("[" + currentValue + "]")
            .formatted(Formatting.YELLOW)
            .styled(style -> style
            .withUnderline(true)
            .withClickEvent(createOptionClickEvent(field, currentValue))
            .withHoverEvent(createCustomValueHoverEvent(currentValue)));
    }

    private static HoverEvent createCustomValueHoverEvent(String currentValue) {
        return HoverEventUtil.event(
            HoverEventUtil.SHOW_TEXT,
            Messenger.s(tr.tr("custom_value").getString() + currentValue).formatted(Formatting.GREEN)
        );
    }
}