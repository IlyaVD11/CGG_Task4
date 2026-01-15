package tests.ObjWriter;

import com.cg.math.Vector2f;
import com.cg.math.Vector3f;
import com.cg.model.Model;
import com.cg.model.Polygon;
import com.cg.objreader.ObjReader;
import com.cg.objwriter.ObjWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjWriterTest {

    @TempDir
    Path tempDir;

    @Test
    public void testReconstruction() throws IOException {
        Model original = new Model();
        original.vertices.add(new Vector3f(1.5f, 2.5f, 3.5f));
        original.vertices.add(new Vector3f(4.0f, 5.0f, 6.0f));
        original.vertices.add(new Vector3f(7.0f, 8.0f, 9.0f));

        original.textureVertices.add(new Vector2f(0.1f, 0.2f));
        original.normals.add(new Vector3f(0, 1, 0));

        Polygon p = new Polygon();
        p.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        original.polygons.add(p);

        Path path = tempDir.resolve("cycle.obj");
        ObjWriter.write(original, path.toString());

        Model loaded = ObjReader.read(Files.readString(path));

        assertEquals(original.vertices.size(), loaded.vertices.size());
        assertEquals(original.vertices.get(0).x, loaded.vertices.get(0).x, 0.0001f);
    }
}