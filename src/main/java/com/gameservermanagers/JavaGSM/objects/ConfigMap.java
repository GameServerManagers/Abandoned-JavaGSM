package com.gameservermanagers.JavaGSM.objects;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

/**
 * Just like a HashMap except you can set defaults for values. Work around for not having getOrDefault because it's only available in Java 8
 */
public class ConfigMap<K, V> extends HashMap<K, V> {

    public LinkedTreeMap<K, V> defaults = new LinkedTreeMap<>();

    @Override
    public V get(Object key) {
        V superValue = super.get(key);
        return superValue != null ? superValue : defaults.get(key);
    }

}
