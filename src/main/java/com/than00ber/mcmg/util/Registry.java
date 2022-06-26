package com.than00ber.mcmg.util;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

public final class Registry<E> {

    private RegistryType registry;
    private final HashMap<String, Supplier<E>> ENTRIES = new HashMap<>();

    public Registry(RegistryType registry) {
        this.registry = registry;
    }

    public Supplier<E> register(String name, Supplier<E> supplier) {
        return ENTRIES.put(name, supplier);
    }

    @Nullable
    public Supplier<E> get(String name) {
        return ENTRIES.getOrDefault(name, null);
    }

    public ImmutableList<String> getRegistryKeys() {
        return ImmutableList.copyOf(ENTRIES.keySet());
    }

    public enum RegistryType {
        MINIGAMES, ITEMS, TEAMS
    }
}
