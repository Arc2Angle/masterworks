package com.masterworks.masterworks.location;

import java.util.Optional;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public interface DataMappedReferenceLocation<K, V> extends RegisteredReferenceLocation<K> {

    public DataMapType<K, V> dataMapType();

    public default Optional<V> dataMapped() {
        return Optional.ofNullable(registered().getData(dataMapType()));
    }
}
