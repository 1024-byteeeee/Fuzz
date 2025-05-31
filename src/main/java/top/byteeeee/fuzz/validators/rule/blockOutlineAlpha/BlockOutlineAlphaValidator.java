package top.byteeeee.fuzz.validators.rule.blockOutlineAlpha;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.fuzz.settings.Validator;
import top.byteeeee.fuzz.translations.Translator;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public class BlockOutlineAlphaValidator extends Validator<Integer> {
    private static final Translator tr = new Translator("validator.blockOutlineAlpha");

    @Override
    public Integer validate(FabricClientCommandSource source, Field field, Integer value) {
        return value >= -1 && value <= 255 ? null : value;
    }

    @Override
    public String description() {
        return tr.tr("value_range").getString();
    }
}
