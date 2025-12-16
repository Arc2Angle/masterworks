package com.masterworks.masterworks.resource.location;

import java.util.Optional;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public interface DataMappedReferenceResourceLocation<K, V>
        extends RegisteredReferenceResourceLocation<K> {

    public DataMapType<K, V> dataMapType();

    public default Optional<V> dataMapped() {
        return Optional.ofNullable(registered().getData(dataMapType()));
    }
}
