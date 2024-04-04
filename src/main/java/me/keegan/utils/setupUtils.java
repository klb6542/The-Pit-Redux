package me.keegan.utils;

import org.jetbrains.annotations.NotNull;

public interface setupUtils {
    void enable();
    void disable();

    static void pluginEnabled(@NotNull setupUtils subclass) {
        subclass.enable();
    }

    static void pluginDisabled(@NotNull setupUtils subclass) {
        subclass.disable();
    }
}
