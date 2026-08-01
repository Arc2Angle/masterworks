package com.masterworks.masterworks.client.part.wavefront;

import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.part.PartGeometry;
import com.masterworks.masterworks.client.part.PartSubmitter;
import com.masterworks.masterworks.client.part.submit.NaiveGeometryPartSubmitter;
import com.masterworks.masterworks.client.part.submit.NamedPosePartSubmitter;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public record WavefrontPartGeometry(WavefrontObject model) implements PartGeometry {
    @Override
    public PartSubmitter bake(Palette palette) {
        return new NamedPosePartSubmitter(model.meshes().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> bake(entry.getValue(), palette))));
    }

    private static PartSubmitter bake(List<WavefrontObject.Face> mesh, Palette palette) {
        Vector3f[][] positions = new Vector3f[mesh.size()][];
        int[] colors = new int[mesh.size()];
        Vector3f[][] normals = new Vector3f[mesh.size()][];

        for (int i = 0; i < mesh.size(); i++) {
            WavefrontObject.Face face = mesh.get(i);

            positions[i] = face.positions();
            colors[i] = transformColor(face.color(), palette);
            normals[i] = face.normals();
        }

        return new NaiveGeometryPartSubmitter(positions, colors, normals);
    }

    private static int transformColor(int color, Palette palette) {
        int intensity = color & 0xFF;
        int index = Mth.clamp((255 - intensity) / 36, 0, 7);
        return palette.colors()[index];
    }
}
