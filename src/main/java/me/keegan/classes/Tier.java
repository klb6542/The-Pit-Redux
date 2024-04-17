package me.keegan.classes;

import java.util.Arrays;

public class Tier<T> {
    private final T[] values;

    @SafeVarargs
    public Tier(T... values) {
        // make it immutable
        this.values = Arrays.copyOf(values, values.length);
    }

    public T get(int index) {
        return (index < this.values.length && index >= 0) ? this.values[index] : null;
    }
}
