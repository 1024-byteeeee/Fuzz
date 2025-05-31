package top.byteeeee.fuzz.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public abstract class Observer<T> {
    public void notify(T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            onValueChange(oldValue, newValue);
        }
    }

    public abstract void onValueChange(T oldValue, T newValue);
}
