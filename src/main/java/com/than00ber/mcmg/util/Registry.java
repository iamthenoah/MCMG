package com.than00ber.mcmg.util;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

public final class Registry<E> {

    private final HashMap<String, Supplier<E>> ENTRIES = new HashMap<>();

    public E register(String name, Supplier<E> supplier) {
        ENTRIES.put(name, supplier);
        return supplier.get();
    }

    @Nullable
    public E get(String name) {
        return ENTRIES.getOrDefault(name, null).get();
    }

    public ImmutableList<String> getRegistryKeys() {
        return ImmutableList.copyOf(ENTRIES.keySet());
    }
}
