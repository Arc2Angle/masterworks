package com.masterworks.masterworks.client.part.wavefront;

import com.masterworks.masterworks.client.MasterworksPartDefinitionTypes;
import com.masterworks.masterworks.client.part.PartDefinition;
import com.masterworks.masterworks.client.part.PartGeometry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public record WavefrontPartDefinition(Identifier model, boolean flipV) implements PartDefinition {
    public static final MapCodec<WavefrontPartDefinition> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Identifier.CODEC.fieldOf("model").forGetter(WavefrontPartDefinition::model),
                            Codec.BOOL.optionalFieldOf("flipV", true).forGetter(WavefrontPartDefinition::flipV))
                    .apply(instance, WavefrontPartDefinition::new));

    private static final FileToIdConverter MODELS = new FileToIdConverter("parts/object", ".obj");

    @Override
    public Type<?> type() {
        return MasterworksPartDefinitionTypes.WAVEFRONT.get();
    }

    @Override
    public PartGeometry bake(ResourceManager resourceManager) {
        Identifier location = MODELS.idToFile(model);
        var params = new WavefrontObject.Params(
                resourceManager,
                location.getNamespace(),
                Path.of(location.getPath()).getParent(),
                flipV);

        try (InputStream stream = resourceManager.open(location)) {
            return WavefrontObject.PARSER.parse(stream, params);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to load object or texture", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse object " + location, e);
        }
    }
}
