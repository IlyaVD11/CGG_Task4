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
    public void testWriteVerticesOnly() throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(1.0f, 1.0f, 1.0f));
        model.vertices.add(new Vector3f(2.0f, 2.0f, 2.0f));
        model.vertices.add(new Vector3f(3.0f, 3.0f, 3.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path filePath = tempDir.resolve("vertices_only.obj");
        ObjWriter.write(model, filePath.toString());

        List<String> lines = Files.readAllLines(filePath);
        String faceLine = findFaceLine(lines);

        assertEquals("f 1 2 3", faceLine);
    }

    @Test
    public void testWriteVerticesAndTextures() throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(0, 1, 0));
        model.vertices.add(new Vector3f(0, 0, 1));

        model.textureVertices.add(new Vector2f(0, 0));
        model.textureVertices.add(new Vector2f(1, 0));
        model.textureVertices.add(new Vector2f(0, 1));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        polygon.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path filePath = tempDir.resolve("vert_text.obj");
        ObjWriter.write(model, filePath.toString());

        List<String> lines = Files.readAllLines(filePath);
        String faceLine = findFaceLine(lines);

        assertEquals("f 1/1 2/2 3/3", faceLine);
    }

    @Test
    public void testWriteVerticesAndNormals() throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(0, 1, 0));
        model.vertices.add(new Vector3f(0, 0, 1));

        model.normals.add(new Vector3f(0, 0, 1));
        model.normals.add(new Vector3f(0, 0, 1));
        model.normals.add(new Vector3f(0, 0, 1));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        polygon.setNormalIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path filePath = tempDir.resolve("vert_norm.obj");
        ObjWriter.write(model, filePath.toString());

        List<String> lines = Files.readAllLines(filePath);
        String faceLine = findFaceLine(lines);

        assertEquals("f 1//1 2//2 3//3", faceLine);
    }

    @Test
    public void testWriteFullData() throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(1, 1, 1));
        model.vertices.add(new Vector3f(2, 2, 2));
        model.vertices.add(new Vector3f(3, 3, 3));

        model.textureVertices.add(new Vector2f(0, 0));
        model.textureVertices.add(new Vector2f(1, 0));
        model.textureVertices.add(new Vector2f(0, 1));

        model.normals.add(new Vector3f(0, 0, 1));
        model.normals.add(new Vector3f(0, 0, 1));
        model.normals.add(new Vector3f(0, 0, 1));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        polygon.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        polygon.setNormalIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path filePath = tempDir.resolve("full.obj");
        ObjWriter.write(model, filePath.toString());

        String content = Files.readString(filePath);
        assertTrue(content.contains("f 1/1/1 2/2/2 3/3/3"));
    }

    @Test
    public void testIndexingConversion() throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0,0,0));
        model.vertices.add(new Vector3f(0,0,0));
        model.vertices.add(new Vector3f(0,0,0));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path path = tempDir.resolve("indexing.obj");
        ObjWriter.write(model, path.toString());

        String content = Files.readString(path);
        assertTrue(content.contains("f 1 2 3"), "Indices should be 1-based");
    }

    @Test
    public void testOutputOrder() throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0,0,0));
        model.textureVertices.add(new Vector2f(0,0));
        model.normals.add(new Vector3f(0,0,0));

        Polygon p = new Polygon();
        p.setVertexIndices(new ArrayList<>(Arrays.asList(0, 0, 0)));
        p.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 0, 0)));
        p.setNormalIndices(new ArrayList<>(Arrays.asList(0, 0, 0)));
        model.polygons.add(p);

        Path path = tempDir.resolve("order.obj");
        ObjWriter.write(model, path.toString());

        String content = Files.readString(path);
        int vPos = content.indexOf("v ");
        int vtPos = content.indexOf("vt ");
        int vnPos = content.indexOf("vn ");
        int fPos = content.indexOf("f ");

        assertTrue(vPos < vtPos, "Vertices before Textures");
        assertTrue(vtPos < vnPos, "Textures before Normals");
        assertTrue(vnPos < fPos, "Normals before Faces");
    }

    @Test
    public void testReconstruction() throws IOException {
        Model original = new Model();
        original.vertices.add(new Vector3f(1.5f, 2.5f, 3.5f));
        original.vertices.add(new Vector3f(4.0f, 5.0f, 6.0f));
        original.vertices.add(new Vector3f(7.0f, 8.0f, 9.0f));

        original.textureVertices.add(new Vector2f(0.1f, 0.2f));
        original.textureVertices.add(new Vector2f(0.3f, 0.4f));
        original.textureVertices.add(new Vector2f(0.5f, 0.6f));

        original.normals.add(new Vector3f(1, 0, 0));
        original.normals.add(new Vector3f(0, 1, 0));
        original.normals.add(new Vector3f(0, 0, 1));

        Polygon p = new Polygon();
        p.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        p.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        p.setNormalIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        original.polygons.add(p);

        Path path = tempDir.resolve("cycle.obj");
        ObjWriter.write(original, path.toString());

        Model loaded = ObjReader.read(Files.readString(path));

        assertEquals(original.vertices.size(), loaded.vertices.size());
        assertEquals(original.vertices.get(0).x, loaded.vertices.get(0).x, 0.0001f);

        assertEquals(original.textureVertices.size(), loaded.textureVertices.size());
        assertEquals(original.normals.size(), loaded.normals.size());
        assertEquals(original.polygons.size(), loaded.polygons.size());
    }

    @Test
    public void testFloatFormatting() throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(1.555f, 0.0f, 0.0f));

        Path path = tempDir.resolve("format.obj");
        ObjWriter.write(model, path.toString());

        String content = Files.readString(path);
        assertTrue(content.contains("1.555"), "Decimal separator must be a dot");
        assertFalse(content.contains("1,555"), "Decimal separator must NOT be a comma");
    }

    private String findFaceLine(List<String> lines) {
        return lines.stream()
                .filter(l -> l.startsWith("f "))
                .findFirst()
                .map(String::trim)
                .orElse("");
    }
}