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

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import top.byteeeee.fuzz.FuzzMod;
import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.settings.Rule;
import top.byteeeee.fuzz.translations.FuzzTranslations;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.Layout;
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
        List<MutableComponent> messages = initializeMessageList();
        boolean hasEnabledRules = collectEnabledRules(messages);

        if (!hasEnabledRules) {
            messages.add(Messenger.f(Messenger.s("· · · · · ·"), Layout.GRAY));
        }

        addCategoriesInfo(messages);
        messages.forEach(message -> Messenger.tell(source, message));
        return 1;
    }

    private static List<MutableComponent> initializeMessageList() {
        List<MutableComponent> messages = new ArrayList<>();
        messages.add(Messenger.f(tr.tr("enable_rule"), Layout.AQUA, Layout.BOLD));
        messages.add(Messenger.f(tr.tr("mod_version", FuzzMod.getInstance().getVersion()), Layout.GRAY));
        return messages;
    }

    private static boolean collectEnabledRules(List<MutableComponent> messages) {
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

    private static void addCategoriesInfo(List<MutableComponent> messages) {
        messages.add(Messenger.f(FuzzCategoriesContext.tr.tr("categories_list"), Layout.GOLD, Layout.BOLD));
        messages.add(FuzzCategoriesContext.showCategories());
        messages.add(Messenger.f(tr.tr("use_help"), Layout.ITALIC, Layout.GRAY));
    }

    public static int showAllRules(FabricClientCommandSource source) {
        List<MutableComponent> messages = new ArrayList<>();
        messages.add(Messenger.f(tr.tr("all_rules"), Layout.AQUA, Layout.BOLD));

        List<Field> sortedFields = getSortedRuleAnnotatedFields();
        boolean hasAnyRule = addRuleEntries(messages, sortedFields);

        if (!hasAnyRule) {
            messages.add(Messenger.f(Messenger.s("· · · · · ·"), Layout.GRAY));
        }

        messages.add(Messenger.f(tr.tr("use_help"), Layout.ITALIC, Layout.GRAY));
        messages.forEach(message -> Messenger.tell(source, message));

        return 1;
    }

    public static MutableComponent ruleEntryText(Field field, Object value) {
        MutableComponent valueDisplay = optionText(field, value);
        String funcNameTrKey = tr.getRuleNameTrKey(field.getName());

        return
            Messenger.c(
                Messenger.f(Messenger.s("- "), Layout.WHITE),
                Messenger.tr(funcNameTrKey),
                createFieldNameText(field),
                valueDisplay
            ).withStyle(Messenger.emptyStyle().withClickEvent(createClickEvent(field)).withHoverEvent(createHoverEvent(field)));
    }

    private static MutableComponent createFieldNameText(Field field) {
        return FuzzTranslations.isEnglish() ? Messenger.s(" ") : Messenger.s(" (" + field.getName() + ") ");
    }

    private static HoverEvent createHoverEvent(Field field) {
        return HoverEventUtil.event(
            HoverEventUtil.SHOW_TEXT,
            Messenger.f(Messenger.tr(tr.getRuleDescTrKey(field.getName())), Layout.YELLOW)
        );
    }

    private static ClickEvent createClickEvent(Field field) {
        return ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz " + field.getName());
    }

    public static int showRuleInfo(FabricClientCommandSource source, Field field) {
        try {
            List<MutableComponent> messages = buildRuleInfoMessages(field);
            messages.forEach(message -> Messenger.tell(source, message));
            return 1;
        } catch (IllegalAccessException e) {
            FuzzModClient.LOGGER.error("Field access error: {}", field.getName(), e);
            return 0;
        }
    }

    private static List<MutableComponent> buildRuleInfoMessages(Field field) throws IllegalAccessException {
        List<MutableComponent> messages = new ArrayList<>();
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

    public static MutableComponent optionText(Field field, Object value) {
        Rule annotation = field.getAnnotation(Rule.class);
        String[] options = getOptions(field, annotation);
        String currentValue = value.toString();
        String defaultValue = getDefaultValue(field);
        boolean isDefaultState = currentValue.equals(defaultValue);

        MutableComponent optionsText = Messenger.s("");
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

    private static boolean addRuleEntries(List<MutableComponent> messages, List<Field> fields) {
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

    private static void addRuleHeader(List<MutableComponent> messages, Field field) {
        MutableComponent nameLine = Messenger.c(
            Messenger.f(Messenger.tr(tr.getRuleNameTrKey(field.getName())), Layout.GOLD, Layout.BOLD),
            Messenger.f(createFieldNameText(field), Layout.BOLD)
        );

        messages.add(nameLine);
    }

    private static void addRuleDescription(List<MutableComponent> messages, Field field) {
        messages.add(Messenger.tr(tr.getRuleDescTrKey(field.getName())));
    }

    private static void addExtraInformation(List<MutableComponent> messages, Field field) {
        int i = 0;

        while (true) {
            String extraKey = "fuzz.rule." + field.getName() + ".extra." + i;
            String extraText = Messenger.tr(extraKey).getString();

            if (extraKey.equals(extraText)) {
                break;
            }

            messages.add(Messenger.f(Messenger.s(extraText), Layout.GRAY));

            i++;
        }
    }

    private static void addCategoriesLine(List<MutableComponent> messages, Rule annotation) {
        MutableComponent categoriesLine = Messenger.f(tr.tr("rule_info_categories"), Layout.WHITE);

        for (String category : annotation.categories()) {
            categoriesLine = Messenger.c(categoriesLine, Messenger.s(" "), createCategoryButton(category));
        }

        messages.add(categoriesLine);
    }

    private static MutableComponent createCategoryButton(String category) {
        MutableComponent hoverText = createCategoryHoverText(category);

        return
            Messenger.c(
                Messenger.f(Messenger.s("[" + FuzzCategoriesContext.tr.tr(category).getString() + "]"), Layout.AQUA)
            )
            .withStyle(Messenger.emptyStyle()
            .withClickEvent(ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz " + "list " + category))
            .withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, hoverText)));
    }

    private static MutableComponent createCategoryHoverText(String category) {
        return
            FuzzTranslations.isEnglish() ?
            Messenger.f(tr.tr("rule_info_click_to_view", FuzzCategoriesContext.tr.tr(category)), Layout.YELLOW) :
            Messenger.f(tr.tr("rule_info_click_to_view", FuzzCategoriesContext.tr.tr(category), category), Layout.YELLOW);
    }

    private static void addValueInformation(List<MutableComponent> messages, Field field, Object currentValue, Object defaultValue) {
        messages.add(createCurrentValueLine(currentValue, defaultValue));
        Rule annotation = field.getAnnotation(Rule.class);

        if (annotation.options().length > 0 || field.getType() == boolean.class) {
            messages.add(tr.tr("options_option", optionText(field, currentValue)));
        }
    }

    private static MutableComponent createCurrentValueLine(Object currentValue, Object defaultValue) {
        return Messenger.f(Messenger.c(tr.tr("current_value", currentValue), createValueStateIcon(currentValue, defaultValue)), Layout.AQUA);
    }

    private static MutableComponent createValueStateIcon(Object currentValue, Object defaultValue) {
        boolean isDefault = Objects.equals(currentValue, defaultValue);
        return isDefault ? createDefaultValueIcon() : createModifiedValueIcon();
    }

    private static MutableComponent createDefaultValueIcon() {
        return Messenger.f(Messenger.s(" ✔"), Layout.GREEN).withStyle(Messenger.emptyStyle().withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, Messenger.f(tr.tr("info_default_value"), Layout.DARK_GREEN))));
    }

    private static MutableComponent createModifiedValueIcon() {
        return Messenger.f(Messenger.s(" ✎"), Layout.GOLD).withStyle(Messenger.emptyStyle().withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, Messenger.f(tr.tr("info_default_value"), Layout.DARK_RED))));
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

    private static boolean addOptionButton(MutableComponent optionsText, Field field, String option, boolean isCurrent, boolean isDefaultOption, boolean isDefaultState) {
        Layout color = determineOptionColor(isDefaultState, isDefaultOption);
        boolean underline = isDefaultState ? isDefaultOption : isCurrent;
        MutableComponent button = createOptionButton(field, option, isDefaultOption, color, underline);
        optionsText.append(button).append(" ");

        return isCurrent;
    }

    private static Layout determineOptionColor(boolean isDefaultState, boolean isDefaultOption) {
        if (isDefaultState) {
            return Layout.GRAY;
        }

        return isDefaultOption ? Layout.DARK_GREEN : Layout.YELLOW;
    }

    private static MutableComponent createOptionButton(Field field, String option, boolean isDefaultOption, Layout color, boolean withUnderline) {
        Layout underline = withUnderline ? Layout.UNDERLINE : color;

        return
            Messenger.f(Messenger.s("[" + option + "]"), color, underline)
            .withStyle(Messenger.emptyStyle()
            .withClickEvent(createOptionClickEvent(field, option))
            .withHoverEvent(createOptionHoverEvent(option, isDefaultOption)));
    }

    private static ClickEvent createOptionClickEvent(Field field, String option) {
        return ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/fuzz " + field.getName() + " " + option);
    }

    private static HoverEvent createOptionHoverEvent(String option, boolean isDefaultOption) {
        MutableComponent message = isDefaultOption ? tr.tr("default_value", option) : tr.tr("set_to", option);
        return HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, Messenger.f(message, Layout.GREEN));
    }

    private static void appendCustomValueButton(MutableComponent optionsText, Field field, String currentValue) {
        MutableComponent customButton = createCustomValueButton(field, currentValue);
        optionsText.append(customButton).append(" ");
    }

    private static MutableComponent createCustomValueButton(Field field, String currentValue) {
        return
            Messenger.f(Messenger.s("[" + currentValue + "]"), Layout.YELLOW, Layout.UNDERLINE)
            .withStyle(Messenger.emptyStyle()
            .withClickEvent(createOptionClickEvent(field, currentValue))
            .withHoverEvent(createCustomValueHoverEvent(currentValue)));
    }

    private static HoverEvent createCustomValueHoverEvent(String currentValue) {
        return HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, Messenger.f(tr.tr("custom_value", currentValue), Layout.GREEN));
    }
}