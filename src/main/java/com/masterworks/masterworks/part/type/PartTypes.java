package com.masterworks.masterworks.part.type;

import com.masterworks.masterworks.Masterworks;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PartTypes {
        public static void init() {}

        public static final DeferredHolder<PartType, PartType> BLADE = Masterworks.PART_TYPES
                        .register("blade", () -> new PartType("Blade", 0.6f, 1.0f, 0.2f));

        public static final DeferredHolder<PartType, PartType> HANDLE = Masterworks.PART_TYPES
                        .register("handle", () -> new PartType("Handle", 0.2f, 0.0f, 0.6f));

        public static final DeferredHolder<PartType, PartType> BINDING = Masterworks.PART_TYPES
                        .register("binding", () -> new PartType("Binding", 0.2f, 0.0f, 0.2f));
}
