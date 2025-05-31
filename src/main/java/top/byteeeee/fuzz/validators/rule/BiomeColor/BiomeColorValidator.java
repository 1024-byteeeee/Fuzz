package top.byteeeee.fuzz.validators.rule.BiomeColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.fuzz.settings.Validator;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.validators.HexValidator;

import java.lang.reflect.Field;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BiomeColorValidator extends Validator<String> {
    private static final Translator tr = new Translator("validator.biomeColorValidator");

    @Override
    public boolean validate(FabricClientCommandSource source, Field field, String value) {
        return HexValidator.isValidHexColor(value) || Objects.equals(value, "false");
    }

    @Override
    public String description() {
        return tr.tr("value_range").getString();
    }
}
