package com.cg.render_engine;

import com.cg.math.*;
import com.cg.model.Model;
import com.cg.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.Arrays;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Matrix4x4 modelMatrix,
            // Параметры режимов (Пункт 4)
            boolean drawWireframe,
            boolean useTexture,
            boolean useLighting,
            Image texture) {

        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();
        Matrix4x4 modelViewProjectionMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);

        // ПУНКТ 2: Инициализация Z-буфера
        float[] zBuffer = new float[width * height];
        Arrays.fill(zBuffer, Float.NEGATIVE_INFINITY);
        PixelWriter pw = graphicsContext.getPixelWriter();

        for (Polygon polygon : mesh.polygons) {
            processTriangle(polygon, mesh, modelViewProjectionMatrix, modelMatrix, camera,
                    width, height, zBuffer, pw, drawWireframe, useTexture, useLighting, texture);
        }
    }

    private static void processTriangle(Polygon poly, Model mesh, Matrix4x4 mvp, Matrix4x4 modelMat, Camera cam,
                                        int w, int h, float[] zBuf, PixelWriter pw,
                                        boolean wire, boolean tex, boolean light, Image texture) {

        var vIdx = poly.getVertexIndices();
        Vector4f[] screenV = new Vector4f[3];
        Vector3f[] worldV = new Vector3f[3];
        Vector3f[] normals = new Vector3f[3];
        Vector2f[] uvs = new Vector2f[3];

        for (int i = 0; i < 3; i++) {
            Vector3f v = mesh.vertices.get(vIdx.get(i));
            worldV[i] = modelMat.multiplyOnVector(new Vector4f(v.x, v.y, v.z, 1)).toVector3f(); // Нужен метод в Vector4f или руками

            screenV[i] = mvp.multiplyOnVector(new Vector4f(v.x, v.y, v.z, 1.0f));
            float invW = 1.0f / screenV[i].w;
            screenV[i].x *= invW; screenV[i].y *= invW; screenV[i].z *= invW;

            // Преобразование в экранные координаты
            Vector2f p = GraphicConveyor.vertexToPoint(new Vector3f(screenV[i].x, screenV[i].y, screenV[i].z), w, h);
            screenV[i].x = p.x; screenV[i].y = p.y;

            if (light) normals[i] = mesh.normals.get(poly.getNormalIndices().get(i));
            if (tex) uvs[i] = mesh.textureVertices.get(poly.getTextureVertexIndices().get(i));
        }

        // Отрисовка сетки (Пункт 4)
        if (wire) {
            pw.setColor((int)screenV[0].x, (int)screenV[0].y, Color.BLACK); // Упрощенно
        }

        // ПУНКТ 2 & 3: Растеризация треугольника через барицентрические координаты
        int minX = (int) Math.max(0, Math.min(screenV[0].x, Math.min(screenV[1].x, screenV[2].x)));
        int maxX = (int) Math.min(w - 1, Math.max(screenV[0].x, Math.max(screenV[1].x, screenV[2].x)));
        int minY = (int) Math.max(0, Math.min(screenV[0].y, Math.min(screenV[1].y, screenV[2].y)));
        int maxY = (int) Math.min(h - 1, Math.max(screenV[0].y, Math.max(screenV[1].y, screenV[2].y)));

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                Vector3f bary = calculateBarycentric(x, y, screenV[0], screenV[1], screenV[2]);

                if (bary.x >= 0 && bary.y >= 0 && bary.z >= 0) {
                    float z = bary.x * screenV[0].z + bary.y * screenV[1].z + bary.z * screenV[2].z;

                    if (z > zBuf[y * w + x]) {
                        zBuf[y * w + x] = z;

                        Color finalColor = Color.GRAY;

                        // ПУНКТ 3: Текстурирование
                        if (tex && texture != null) {
                            float u = bary.x * uvs[0].x + bary.y * uvs[1].x + bary.z * uvs[2].x;
                            float v = bary.x * uvs[0].y + bary.y * uvs[1].y + bary.z * uvs[2].y;
                            finalColor = texture.getPixelReader().getColor(
                                    (int)(u * (texture.getWidth()-1)),
                                    (int)((1-v) * (texture.getHeight()-1))
                            );
                        }

                        // ПУНКТ 3: Освещение (Ламберт)
                        if (light) {
                            Vector3f interpolatedNormal = new Vector3f(
                                    bary.x * normals[0].x + bary.y * normals[1].x + bary.z * normals[2].x,
                                    bary.x * normals[0].y + bary.y * normals[1].y + bary.z * normals[2].y,
                                    bary.x * normals[0].z + bary.y * normals[1].z + bary.z * normals[2].z
                            );
                            interpolatedNormal.normalize();

                            Vector3f lightDir = cam.getPosition().subtraction(worldV[0]); // Источник света в камере
                            lightDir.normalize();

                            float intensity = Math.max(0.1f, interpolatedNormal.dotProduct(lightDir));
                            finalColor = Color.color(
                                    finalColor.getRed() * intensity,
                                    finalColor.getGreen() * intensity,
                                    finalColor.getBlue() * intensity
                            );
                        }

                        pw.setColor(x, y, finalColor);
                    }
                }
            }
        }
    }

    private static Vector3f calculateBarycentric(float x, float y, Vector4f a, Vector4f b, Vector4f c) {
        float det = (b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y);
        float l1 = ((b.y - c.y) * (x - c.x) + (c.x - b.x) * (y - c.y)) / det;
        float l2 = ((c.y - a.y) * (x - c.x) + (a.x - c.x) * (y - c.y)) / det;
        float l3 = 1.0f - l1 - l2;
        return new Vector3f(l1, l2, l3);
    }
}