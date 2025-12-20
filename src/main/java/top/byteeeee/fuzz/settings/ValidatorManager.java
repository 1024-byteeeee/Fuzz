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

package top.byteeeee.fuzz.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import top.byteeeee.fuzz.FuzzModClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class ValidatorManager {
    private static final ConcurrentHashMap<Field, List<Validator<?>>> fieldValidators = new ConcurrentHashMap<>();

    public static void init(Field field) {
        Rule annotation = field.getAnnotation(Rule.class);

        if (annotation == null) {
            return;
        }

        Class<? extends Validator<?>>[] validatorClasses = annotation.validators();
        if (validatorClasses.length > 0) {
            List<Validator<?>> validators = new ArrayList<>();
            for (Class<? extends Validator<?>> validatorClass : validatorClasses) {
                try {
                    Validator<?> validator = validatorClass.getDeclaredConstructor().newInstance();
                    validators.add(validator);
                } catch (Exception e) {
                    FuzzModClient.LOGGER.error("Failed to instantiate validator: {}", validatorClass.getName(), e);
                }
            }
            if (!validators.isEmpty()) {
                fieldValidators.put(field, validators);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T validateValue(Field field, T value, FabricClientCommandSource source) {
        T resultValue = value;

        List<Validator<?>> validators = fieldValidators.get(field);
        if (validators != null && !validators.isEmpty()) {
            for (Validator<?> validator : validators) {
                Validator<T> typedValidator = (Validator<T>) validator;
                resultValue = typedValidator.validate(source, field, resultValue);
                if (resultValue == null) {
                    return null;
                }
            }
        }

        return resultValue;
    }

    public static List<String> getValidatorDescriptions(Field field) {
        List<Validator<?>> validators = fieldValidators.get(field);

        if (validators == null || validators.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> descriptions = new ArrayList<>();
        for (Validator<?> validator : validators) {
            descriptions.add(validator.description());
        }

        return descriptions;
    }
}
