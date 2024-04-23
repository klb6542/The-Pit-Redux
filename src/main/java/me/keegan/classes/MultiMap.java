package me.keegan.classes;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class MultiMap<K, V, B> {
    // used to save two values represented by one key
    // 4/22/2024: I just realized this 'MultiMap' is completely useless, sorry

    private final Class<V> valueClass;
    private final Class<B> bonusClass;
    private final HashMap<K, V> values = new HashMap<>();
    private final HashMap<K, B> bonuses = new HashMap<>();

    public MultiMap(Class<V> valueClass, Class<B> bonusClass) {
        this.valueClass = valueClass;
        this.bonusClass = bonusClass;
    }

    public void put(K key, Object object) throws ClassNotFoundException {
        // object is either a value or bonus
        // check if its either and then put in hashmap

        if (object.getClass().equals(this.valueClass)) {
            this.values.put(key, this.valueClass.cast(object));
            return;
        }

        if (object.getClass().equals(this.bonusClass)) {
            this.bonuses.put(key, this.bonusClass.cast(object));
            return;
        }

        throw new ClassNotFoundException("Object is not of type "
                + this.valueClass.getTypeName() + " or "
                + this.bonusClass.getTypeName());
    }

    @Nullable
    public V getValue(K key) {
        return this.values.getOrDefault(key, null);
    }

    @Nullable
    public B getBonus(K key) {
        return this.bonuses.getOrDefault(key, null);
    }
}