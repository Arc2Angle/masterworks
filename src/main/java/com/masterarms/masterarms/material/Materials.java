package com.masterarms.masterarms.material;

import com.masterarms.masterarms.Masterarms;

import net.neoforged.neoforge.registries.DeferredHolder;

public class Materials {
        public static final DeferredHolder<Material, Material> IRON = Masterarms.MATERIALS
                        .register("iron", () -> new Material(
                                        250,
                                        6.0f,
                                        2.0f,
                                        14.0f,
                                        0.0f,
                                        1));

        public static final DeferredHolder<Material, Material> DIAMOND = Masterarms.MATERIALS
                        .register("diamond", () -> new Material(
                                        1561,
                                        8.0f,
                                        3.0f,
                                        20.0f,
                                        2.0f,
                                        1));

        public static final DeferredHolder<Material, Material> NETHERITE = Masterarms.MATERIALS
                        .register("netherite", () -> new Material(
                                        2031,
                                        9.0f,
                                        4.0f,
                                        20.0f,
                                        3.0f,
                                        1));

        public static final DeferredHolder<Material, Material> MYTHRIL = Masterarms.MATERIALS
                        .register("mythril", () -> new Material(
                                        1800,
                                        12.0f,
                                        3.5f,
                                        24.0f,
                                        1.0f,
                                        1));
}
