package com.masterworks.masterworks.client.mesh.wavefront;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.MasterworksMeshDefinitionTypes;
import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.mesh.MeshDefinition;
import com.masterworks.masterworks.util.SimpleTransform;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public record WavefrontMeshDefinition(Identifier model, SimpleTransform transform, boolean flipV)
        implements MeshDefinition {
    public static final MapCodec<WavefrontMeshDefinition> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Identifier.CODEC.fieldOf("model").forGetter(WavefrontMeshDefinition::model),
                            SimpleTransform.CODEC.fieldOf("transform").forGetter(WavefrontMeshDefinition::transform),
                            Codec.BOOL.optionalFieldOf("flip_v", true).forGetter(WavefrontMeshDefinition::flipV))
                    .apply(instance, WavefrontMeshDefinition::new));

    private static final FileToIdConverter MODELS = new FileToIdConverter("wavefront", ".obj");

    @Override
    public Type<?> type() {
        return MasterworksMeshDefinitionTypes.WAVEFRONT.get();
    }

    @Override
    public Mesh bake(ResourceManager resourceManager) {
        Identifier location = MODELS.idToFile(model);
        var params = new WavefrontObject.Params(
                resourceManager,
                location.getNamespace(),
                Path.of(location.getPath()).getParent(),
                transform.complete(),
                flipV);

        MasterworksMod.LOGGER.info("transform {}", params.transform().getMatrix());

        try (InputStream stream = resourceManager.open(location)) {
            return WavefrontObject.PARSER.parse(stream, params);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read object " + location, e);
        }
    }
}
