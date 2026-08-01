package com.masterworks.masterworks.data;

import com.masterworks.masterworks.MasterworksMod;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

/**
 * <p>A component's role is its function or purpose within a composition.
 * Roles are essentially completely abstract, with their meaning defined
 * by the compositions which use them.</p>
 *
 * <p>Three special roles serve as interface points between compositions and the rendering pipeline:
 * {@link #ITEM} and {@link #LAYER} are <b>requested</b> by the rendering pipeline
 * while {@link #MATERIAL} is <b>provided</b> by the material rendering pipeline.</p>
 *
 * <p>Every other role is composite in a chain connecting item/layer to material.</p>
 *
 * @param id the identifier of the role
 */
public record Role(Identifier id) {
    public static final Codec<Role> CODEC = Identifier.CODEC.xmap(Role::new, Role::id);

    /**
     * A special role which is requested by the handheld and GUI rendering pipeline.
     * A composition without an item role cannot be rendered in the player's hand or in the inventory.
     * Renderers should not request this role.
     */
    public static final Role ITEM = new Role(Identifier.fromNamespaceAndPath(MasterworksMod.ID, "item"));

    /**
     * A special role which is requested by the equipment rendering pipeline.
     * A composition without a layer role cannot be rendered on the player's body.
     * Renderers should not request this role.
     */
    public static final Role LAYER = new Role(Identifier.fromNamespaceAndPath(MasterworksMod.ID, "layer"));

    /**
     * A special role which is provided by the material rendering pipeline.
     * Compositions should not provide this role.
     */
    public static final Role MATERIAL = new Role(Identifier.fromNamespaceAndPath(MasterworksMod.ID, "material"));
}
