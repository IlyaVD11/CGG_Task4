package com.cg.objwriter;

import com.cg.math.Vector2f;
import com.cg.math.Vector3f;
import com.cg.model.Model;
import com.cg.model.Polygon;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

public class ObjWriter {
    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";

    public static void write(Model model, String fileName) throws IOException {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null.");
        }

        File file = new File(fileName);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("# Exported by CGVSU ObjWriter");
            writer.println();

            for (Vector3f v : model.vertices) {
                writer.printf(Locale.US, "%s %.4f %.4f %.4f%n", OBJ_VERTEX_TOKEN, v.x, v.y, v.z);
            }

            if (model.textureVertices != null) {
                for (Vector2f vt : model.textureVertices) {
                    writer.printf(Locale.US, "%s %.4f %.4f%n", OBJ_TEXTURE_TOKEN, vt.x, vt.y);
                }
            }

            if (model.normals != null) {
                for (Vector3f vn : model.normals) {
                    writer.printf(Locale.US, "%s %.4f %.4f %.4f%n", OBJ_NORMAL_TOKEN, vn.x, vn.y, vn.z);
                }
            }

            if (model.polygons != null) {
                for (Polygon polygon : model.polygons) {
                    writer.print(OBJ_FACE_TOKEN);

                    ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
                    ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
                    ArrayList<Integer> normalIndices = polygon.getNormalIndices();

                    for (int i = 0; i < vertexIndices.size(); i++) {
                        writer.print(" ");
                        writer.print(vertexIndices.get(i) + 1);

                        boolean hasTextures = !textureIndices.isEmpty();
                        boolean hasNormals = !normalIndices.isEmpty();

                        if (hasTextures || hasNormals) {
                            writer.print("/");
                            if (hasTextures) {
                                writer.print(textureIndices.get(i) + 1);
                            }
                            if (hasNormals) {
                                writer.print("/");
                                writer.print(normalIndices.get(i) + 1);
                            }
                        }
                    }
                    writer.println();
                }
            }
        }
    }
}