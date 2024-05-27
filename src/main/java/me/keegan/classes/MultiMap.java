package me.keegan.classes;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class MultiMap<K, V, B> {
    // used to save two values represented by one key

    private final Class<V> valueClass;
    private final Class<B> bonusClass;
    private final HashMap<K, V> values = new HashMap<>();
    private final HashMap<K, B> bonuses = new HashMap<>();

    public MultiMap(Class<V> valueClass, Class<B> bonusClass) {
        this.valueClass = valueClass;
        this.bonusClass = bonusClass;
    }

    public void put(K key, Object object) {
        // object is either a value or bonus
        // check if its either and then put in hashmap

        // this is kinda shitty cuz value and bonus can't be the same types
        if (object.getClass().equals(this.valueClass)) {
            this.values.put(key, this.valueClass.cast(object));
        }

        if (object.getClass().equals(this.bonusClass)) {
            this.bonuses.put(key, this.bonusClass.cast(object));
        }
    }

    public void put(K key, Object object, Object object2) {
        this.put(key, object);
        this.put(key, object2);
    }

    public void resetMaps(K key) {
        this.values.remove(key);
        this.bonuses.remove(key);
    }

    public Set<K> getKeySet() {
        return this.values.keySet();
    }

    @Nullable
    public V getValue(K key) {
        return this.values.getOrDefault(key, null);
    }

    @Nullable
    public B getBonus(K key) {
        return this.bonuses.getOrDefault(key, null);
    }

    public boolean containsKey(K key) {
        return this.values.containsKey(key) && this.bonuses.containsKey(key);
    }
}