package top.byteeeee.fuzz.validators.rule.fuzzCommandAlias;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.fuzz.settings.Validator;
import top.byteeeee.fuzz.translations.Translator;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public class FuzzCommandAliasValidator extends Validator<String> {
    private static final Translator tr = new Translator("validator.fuzzCommandAlias");

    @Override
    public String validate(FabricClientCommandSource source, Field field, String value) {
        return value.matches("^[a-zA-Z]+$") ? value : null;
    }

    @Override
    public String description() {
        return tr.tr("value_range").getString();
    }
}
