package com.cg.render_engine;

import com.cg.math.*;
import com.cg.model.Model;
import com.cg.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Matrix4x4 modelMatrix,
            // Твои параметры из GuiController:
            boolean drawWireframe,
            boolean useTexture,
            boolean useLighting,
            Image texture) {

        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();
        Matrix4x4 modelViewProjectionMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);

        // Инициализируем Z-буфер (чтобы задние полигоны не перекрывали передние)
        float[] zBuffer = new float[width * height];
        Arrays.fill(zBuffer, Float.NEGATIVE_INFINITY);
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        for (Polygon polygon : mesh.polygons) {
            // Рисуем каждый треугольник
            renderTriangle(polygon, mesh, modelViewProjectionMatrix, modelMatrix, camera,
                    width, height, zBuffer, pixelWriter, graphicsContext,
                    drawWireframe, useTexture, useLighting, texture);
        }
    }

    private static void renderTriangle(
            Polygon poly, Model mesh, Matrix4x4 mvp, Matrix4x4 modelMat, Camera cam,
            int w, int h, float[] zBuf, PixelWriter pw, GraphicsContext gc,
            boolean wire, boolean tex, boolean light, Image texture) {

        ArrayList<Integer> vIdx = poly.getVertexIndices();
        Vector4f[] screenV = new Vector4f[3]; // Вершины в экранных координатах
        Vector3f[] worldV = new Vector3f[3];  // Вершины в мировых (для света)

        // 1. Подготовка координат
        for (int i = 0; i < 3; i++) {
            Vector3f v = mesh.vertices.get(vIdx.get(i));

            // Мировые координаты (ручное исправление ошибки toVector3f)
            Vector4f vWorld4 = modelMat.multiplyOnVector(new Vector4f(v.x, v.y, v.z, 1.0f));
            worldV[i] = new Vector3f(vWorld4.x, vWorld4.y, vWorld4.z);

            // Проекция на экран
            screenV[i] = mvp.multiplyOnVector(new Vector4f(v.x, v.y, v.z, 1.0f));
            float invW = 1.0f / screenV[i].w;
            screenV[i].x *= invW; screenV[i].y *= invW; screenV[i].z *= invW;

            Vector2f p = GraphicConveyor.vertexToPoint(new Vector3f(screenV[i].x, screenV[i].y, screenV[i].z), w, h);
            screenV[i].x = p.x;
            screenV[i].y = p.y;
        }

        // 2. Отрисовка заливки (Текстура или Цвет + Свет)
        // Если хоть одна галочка (текстура или свет) включена — запускаем растеризатор
        if (tex || light || (!wire)) {
            drawFilledTriangle(screenV, worldV, poly, mesh, cam, w, h, zBuf, pw, tex, light, texture);
        }

        // 3. Отрисовка сетки (Пункт 4: Галочка Wireframe)
        if (wire) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeLine(screenV[0].x, screenV[0].y, screenV[1].x, screenV[1].y);
            gc.strokeLine(screenV[1].x, screenV[1].y, screenV[2].x, screenV[2].y);
            gc.strokeLine(screenV[2].x, screenV[2].y, screenV[0].x, screenV[0].y);
        }
    }

    private static void drawFilledTriangle(Vector4f[] screenV, Vector3f[] worldV, Polygon poly, Model mesh, Camera cam,
                                           int w, int h, float[] zBuf, PixelWriter pw,
                                           boolean tex, boolean light, Image texture) {
        // Ограничивающий прямоугольник (Bounding Box)
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

                        Color color = Color.LIGHTGRAY; // Базовый цвет если ничего не выбрано

                        // ПУНКТ 3: Текстура
                        if (tex && texture != null && !poly.getTextureVertexIndices().isEmpty()) {
                            Vector2f t0 = mesh.textureVertices.get(poly.getTextureVertexIndices().get(0));
                            Vector2f t1 = mesh.textureVertices.get(poly.getTextureVertexIndices().get(1));
                            Vector2f t2 = mesh.textureVertices.get(poly.getTextureVertexIndices().get(2));
                            float u = bary.x * t0.x + bary.y * t1.x + bary.z * t2.x;
                            float v = bary.x * t0.y + bary.y * t1.y + bary.z * t2.y;
                            color = texture.getPixelReader().getColor(
                                    (int)(u * (texture.getWidth()-1)),
                                    (int)((1-v) * (texture.getHeight()-1))
                            );
                        }

                        // ПУНКТ 3: Освещение
                        if (light && !poly.getNormalIndices().isEmpty()) {
                            Vector3f n0 = mesh.normals.get(poly.getNormalIndices().get(0));
                            Vector3f n1 = mesh.normals.get(poly.getNormalIndices().get(1));
                            Vector3f n2 = mesh.normals.get(poly.getNormalIndices().get(2));
                            Vector3f normal = new Vector3f(
                                    bary.x * n0.x + bary.y * n1.x + bary.z * n2.x,
                                    bary.x * n0.y + bary.y * n1.y + bary.z * n2.y,
                                    bary.x * n0.z + bary.y * n1.z + bary.z * n2.z
                            );
                            normal.normalize();
                            Vector3f lightDir = cam.getPosition().subtraction(worldV[0]);
                            lightDir.normalize();
                            float intensity = Math.max(0.2f, normal.dotProduct(lightDir));
                            color = Color.color(color.getRed()*intensity, color.getGreen()*intensity, color.getBlue()*intensity);
                        }

                        pw.setColor(x, y, color);
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