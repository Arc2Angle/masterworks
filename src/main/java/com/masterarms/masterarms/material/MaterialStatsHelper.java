package com.masterarms.masterarms.material;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import com.masterarms.masterarms.Masterarms;

public class MaterialStatsHelper {

    /**
     * Get material by resource location
     */
    public static Optional<Material> getMaterial(ResourceLocation location) {
        if (Masterarms.MATERIAL_STATS_REGISTRY == null) {
            return Optional.empty();
        }
        return Masterarms.MATERIAL_STATS_REGISTRY.get(location).map(Holder::value);
    }

    /**
     * Get material by string id
     */
    public static Optional<Material> getMaterial(String modid, String name) {
        return getMaterial(ResourceLocation.fromNamespaceAndPath(modid, name));
    }

    /**
     * Get all registered material
     */
    public static Iterable<Material> getAllMaterials() {
        if (Masterarms.MATERIAL_STATS_REGISTRY == null) {
            return java.util.Collections.emptyList();
        }
        return Masterarms.MATERIAL_STATS_REGISTRY;
    }

    /**
     * Check if a material exists
     */
    public static boolean hasMaterial(ResourceLocation location) {
        if (Masterarms.MATERIAL_STATS_REGISTRY == null) {
            return false;
        }
        return Masterarms.MATERIAL_STATS_REGISTRY.containsKey(location);
    }
}