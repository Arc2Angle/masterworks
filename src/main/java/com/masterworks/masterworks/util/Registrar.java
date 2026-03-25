package com.masterworks.masterworks.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;

public final class Registrar<E extends Event> {
    private final String namespace;
    private final Class<E> clazz;
    private final List<Consumer<E>> entries = new LinkedList<>();

    public Registrar(String namespace, Class<E> clazz) {
        this.namespace = namespace;
        this.clazz = clazz;
    }

    public <T extends Consumer<E>> T addIdentified(String path, Function<Identifier, T> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
        T entry = factory.apply(id);
        entries.add(entry);
        return entry;
    }

    public <T extends Consumer<E>> T addUnidentified(T entry) {
        entries.add(entry);
        return entry;
    }

    public void register(IEventBus bus) {
        bus.addListener(clazz, (E event) -> entries.forEach(entry -> entry.accept(event)));
    }

    public abstract class Wrapper<T> implements Consumer<E> {
        public abstract T unwrap();
    }

    public abstract class Deferred<T> implements Consumer<E>, Supplier<T> {
        private T value;

        protected abstract T create(E event);

        @Override
        public T get() {
            if (value == null) {
                throw new IllegalStateException("Cannot access value before " + clazz.getSimpleName() + " is fired");
            }

            return value;
        }

        @Override
        public void accept(E event) {
            if (value != null) {
                throw new IllegalStateException(clazz.getSimpleName() + " has already been fired");
            }

            value = create(event);
        }
    }
}
