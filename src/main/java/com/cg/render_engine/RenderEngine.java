package com.cg.render_engine;

import com.cg.math.*;
import com.cg.model.Model;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

import static com.cg.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height)  {
        Matrix4x4 modelMatrix = camera.getModelMatrix();
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();

        Matrix4x4 modelViewProjectionMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Vector2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                Vector4f vertex4f = new Vector4f(vertex.x, vertex.y, vertex.z, 1.0F);
                Vector4f resultVertex = modelViewProjectionMatrix.multiplyOnVector(vertex4f);

                Vector3f normalizedCoordinates = new Vector3f(
                        resultVertex.x / resultVertex.w,
                        resultVertex.y / resultVertex.w,
                        resultVertex.z / resultVertex.w
                );

                Vector2f resultPoint = vertexToPoint(normalizedCoordinates, width, height);
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).x,
                        resultPoints.get(vertexInPolygonInd - 1).y,
                        resultPoints.get(vertexInPolygonInd).x,
                        resultPoints.get(vertexInPolygonInd).y);
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).x,
                        resultPoints.get(nVerticesInPolygon - 1).y,
                        resultPoints.get(0).x,
                        resultPoints.get(0).y);
        }
    }
}