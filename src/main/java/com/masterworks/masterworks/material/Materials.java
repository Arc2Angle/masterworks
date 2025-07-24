package com.masterworks.masterworks.material;

import com.masterworks.masterworks.Masterworks;
import net.neoforged.neoforge.registries.DeferredHolder;

public class Materials {
        public static void init() {}

        public static final DeferredHolder<Material, Material> IRON =
                        Masterworks.MATERIALS.register("iron", () -> new Material("Iron", 250, 6.0f,
                                        2.0f, 14.0f, 0.0f, 1));

        public static final DeferredHolder<Material, Material> DIAMOND =
                        Masterworks.MATERIALS.register("diamond", () -> new Material("Diamond",
                                        1561, 8.0f, 3.0f, 20.0f, 2.0f, 1));

        public static final DeferredHolder<Material, Material> NETHERITE =
                        Masterworks.MATERIALS.register("netherite", () -> new Material("Netherite",
                                        2031, 9.0f, 4.0f, 20.0f, 3.0f, 1));

        public static final DeferredHolder<Material, Material> MYTHRIL =
                        Masterworks.MATERIALS.register("mythril", () -> new Material("Mythril",
                                        1800, 12.0f, 3.5f, 24.0f, 1.0f, 1));
}
