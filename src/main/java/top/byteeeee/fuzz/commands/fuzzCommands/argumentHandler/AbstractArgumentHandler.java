/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 1024_byteeeee and contributors
 */

package top.byteeeee.fuzz.commands.fuzzCommands.argumentHandler;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.ChatFormatting;

import top.byteeeee.fuzz.settings.ObserverManager;
import top.byteeeee.fuzz.settings.Rule;
import top.byteeeee.fuzz.config.FuzzConfig;
import top.byteeeee.fuzz.commands.fuzzCommands.context.FuzzCommandContext;
import top.byteeeee.fuzz.translations.FuzzTranslations;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.Layout;
import top.byteeeee.fuzz.utils.Messenger;
import top.byteeeee.fuzz.settings.ValidatorManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class AbstractArgumentHandler<T> implements ArgumentHandlerInterface<T> {
    protected static final Translator tr = new Translator("command");
    protected Field currentField;

    @Override
    public void configureArgument(LiteralArgumentBuilder<FabricClientCommandSource> literal, Field field) {
        this.currentField = field;
        RequiredArgumentBuilder<FabricClientCommandSource, T> valueArg = buildValueArgument(field);
        literal.executes(ctx -> FuzzCommandContext.showRuleInfo(ctx.getSource(), field)).then(valueArg);
    }

    private RequiredArgumentBuilder<FabricClientCommandSource, T> buildValueArgument(Field field) {
        return ClientCommands.argument("value", getArgumentType()).suggests(this::getSuggestions).executes(ctx -> executeSetValue(ctx, field));
    }

    private int executeSetValue(CommandContext<FabricClientCommandSource> ctx, Field field) throws CommandSyntaxException {
        T value = parseValue(ctx);

        if (!validateInputValue(ctx, value)) {
            return 0;
        }

        T validatedValue = validateWithManager(ctx, field, value);
        if (validatedValue == null) {
            return 0;
        }

        Optional<T> oldValue = getOldValue(field);
        if (oldValue.isEmpty()) {
            return 0;
        }

        if (!setNewValue(field, validatedValue)) {
            return 0;
        }

        sendSuccessMessage(ctx, field, validatedValue);
        ObserverManager.notifyObservers(ctx.getSource(), field, oldValue.get(), validatedValue);

        return 1;
    }

    private boolean validateInputValue(CommandContext<FabricClientCommandSource> ctx, T value) {
        if (isStrictMode() && !isValidOption(value.toString())) {
            Messenger.tell(ctx.getSource(), Messenger.f(tr.tr("is_not_valid_value"), Layout.RED));
            return false;
        }

        return true;
    }

    private T validateWithManager(CommandContext<FabricClientCommandSource> ctx, Field field, T value) {
        T validatedValue = ValidatorManager.validateValue(field, value, ctx.getSource());

        if (validatedValue == null) {
            List<String> descriptions = ValidatorManager.getValidatorDescriptions(field);
            String errorMsg = descriptions.isEmpty() ? "Validation failed" : descriptions.getFirst();
            Messenger.tell(ctx.getSource(), Messenger.s(errorMsg).withStyle(ChatFormatting.RED));
            return null;
        }

        return validatedValue;
    }

    private Optional<T> getOldValue(Field field) {
        try {
            @SuppressWarnings("unchecked")
            T currentValue = (T) field.get(null);
            return Optional.ofNullable(currentValue);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to get field value", e);
        }
    }

    private boolean setNewValue(Field field, T value) {
        try {
            field.set(null, value);
            FuzzConfig.saveConfig();
            return true;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set field value", e);
        }
    }

    protected boolean isValidOption(String value) {
        if (!isStrictMode()) {
            return true;
        }

        String[] options = getAnnotationOptions();

        return options.length == 0 || Arrays.asList(options).contains(value);
    }

    protected boolean isStrictMode() {
        return Optional.ofNullable(currentField).map(field -> field.getAnnotation(Rule.class)).map(Rule::strict).orElse(false);
    }

    protected String[] getAnnotationOptions() {
        return Optional.ofNullable(currentField).map(field -> field.getAnnotation(Rule.class)).map(Rule::options).orElse(new String[0]);
    }

    private void sendSuccessMessage(CommandContext<FabricClientCommandSource> ctx, Field field, T value) {
        String funcNameTrKey = tr.getRuleNameTrKey(field.getName());

        if (FuzzTranslations.isEnglish()) {
            Messenger.tell(ctx.getSource(), tr.tr("set_value", field.getName(), value));
        } else {
            Messenger.tell(ctx.getSource(), tr.tr("set_value", Messenger.tr(funcNameTrKey), field.getName(), value));
        }
    }

    protected abstract ArgumentType<T> getArgumentType();
}
