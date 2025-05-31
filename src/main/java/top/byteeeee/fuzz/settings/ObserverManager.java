package top.byteeeee.fuzz.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import top.byteeeee.fuzz.FuzzModClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class ObserverManager {
    private static final ConcurrentHashMap<Field, List<Observer<?>>> fieldObservers = new ConcurrentHashMap<>();

    public static void init(Field field) {
        Rule annotation = field.getAnnotation(Rule.class);

        if (annotation == null) {
            return;
        }

        Class<? extends Observer<?>>[] observerClasses = annotation.observers();
        if (observerClasses.length > 0) {
            List<Observer<?>> observers = new ArrayList<>();
            for (Class<? extends Observer<?>> observerClass : observerClasses) {
                try {
                    Observer<?> observer = observerClass.getDeclaredConstructor().newInstance();
                    observers.add(observer);
                } catch (Exception e) {
                    FuzzModClient.LOGGER.error("Failed to instantiate observer: {}", observerClass.getName(), e);
                }
            }
            if (!observers.isEmpty()) {
                fieldObservers.put(field, observers);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void notifyObservers(Field field, T oldValue, T newValue) {
        List<Observer<?>> observers = fieldObservers.get(field);
        if (observers != null && !observers.isEmpty()) {
            for (Observer<?> observer : observers) {
                Observer<T> typedObserver = (Observer<T>) observer;
                typedObserver.notify(oldValue, newValue);
            }
        }
    }
}