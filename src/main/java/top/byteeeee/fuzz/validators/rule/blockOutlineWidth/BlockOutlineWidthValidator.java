package top.byteeeee.fuzz.validators.rule.blockOutlineWidth;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.fuzz.settings.Validator;
import top.byteeeee.fuzz.translations.Translator;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public class BlockOutlineWidthValidator extends Validator<Double> {
    private static final Translator tr = new Translator("validator.blockOutlineWidth");

    @Override
    public boolean validate(FabricClientCommandSource source, Field field, Double value) {
        return value >= -1.0D && value <= 80.0D;
    }

    @Override
    public String description() {
        return tr.tr("value_range").getString();
    }
}
