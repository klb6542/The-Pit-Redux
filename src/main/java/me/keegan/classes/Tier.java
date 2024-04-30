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
        return (this.contains(index)) ? this.values[index] : null;
    }

    public T getOrDefault(int index, T defaultObject) {
        return (this.get(index) != null) ? this.get(index) : defaultObject;
    }

    public boolean contains(int index) {
        return index < this.values.length && index >= 0;
    }
}
