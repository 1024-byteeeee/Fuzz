package top.byteeeee.fuzz.observers.rule.fuzzCommandAlias;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.util.Formatting;

import top.byteeeee.fuzz.settings.Observer;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.Messenger;

@Environment(EnvType.CLIENT)
public class FuzzCommandAliasObserver extends Observer<String> {
    private static final Translator tr = new Translator("validator.fuzzCommandAlias");

    @Override
    public void onValueChange(String oldValue, String newValue) {
        Messenger.sendMsgToPlayer(tr.tr("onValueChange").formatted(Formatting.YELLOW));
    }
}
