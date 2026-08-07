package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.renderer.entity.layers.ConstructChestplateRenderLayer;
import com.masterworks.masterworks.util.Registrar;
import com.mojang.datafixers.util.Unit;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class MasterworksRenderLayers {
    public static final Registrar<EntityRenderersEvent.AddLayers> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, EntityRenderersEvent.AddLayers.class);

    public static <T extends RenderLayer<AvatarRenderState, PlayerModel>> Unit registerPlayerLayer(
            Function<AvatarRenderer<AbstractClientPlayer>, T> factory) {
        REGISTRAR.addUnidentified(event -> event.getSkins().stream()
                .flatMap(playerModelType -> Stream.<AvatarRenderer<AbstractClientPlayer>>ofNullable(
                        event.getPlayerRenderer(playerModelType)))
                .forEach(renderer -> renderer.addLayer(factory.apply(renderer))));
        return Unit.INSTANCE;
    }

    public static final Unit CONSTRUCT = registerPlayerLayer(ConstructChestplateRenderLayer::new);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
