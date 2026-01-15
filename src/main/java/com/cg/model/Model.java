package com.cg.model;

import com.cg.math.Vector2f;
import com.cg.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class Model {
    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<>();
    public ArrayList<Vector3f> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();

    // ПУНКТ 1: Триангуляция (метод веера)
    public void triangulate() {
        ArrayList<Polygon> newPolygons = new ArrayList<>();
        for (Polygon polygon : polygons) {
            ArrayList<Integer> vInd = polygon.getVertexIndices();
            ArrayList<Integer> tInd = polygon.getTextureVertexIndices();
            ArrayList<Integer> nInd = polygon.getNormalIndices();

            if (vInd.size() < 3) continue;

            for (int i = 1; i < vInd.size() - 1; i++) {
                Polygon triangle = new Polygon();

                ArrayList<Integer> subV = new ArrayList<>(List.of(vInd.get(0), vInd.get(i), vInd.get(i + 1)));
                triangle.setVertexIndices(subV);

                if (!tInd.isEmpty()) {
                    ArrayList<Integer> subT = new ArrayList<>(List.of(tInd.get(0), tInd.get(i), tInd.get(i + 1)));
                    triangle.setTextureVertexIndices(subT);
                }
                if (!nInd.isEmpty()) {
                    ArrayList<Integer> subN = new ArrayList<>(List.of(nInd.get(0), nInd.get(i), nInd.get(i + 1)));
                    triangle.setNormalIndices(subN);
                }
                newPolygons.add(triangle);
            }
        }
        this.polygons = newPolygons;
    }

    // ПУНКТ 1: Пересчет нормалей (на основе вашего кода)
    public void recalculateNormals() {
        normals.clear();
        ArrayList<Vector3f> vertexNormals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) vertexNormals.add(new Vector3f(0, 0, 0));

        for (Polygon polygon : polygons) {
            List<Integer> vIdx = polygon.getVertexIndices();
            if (vIdx.size() < 3) continue;

            Vector3f v0 = vertices.get(vIdx.get(0));
            Vector3f v1 = vertices.get(vIdx.get(1));
            Vector3f v2 = vertices.get(vIdx.get(2));

            Vector3f edge1 = v1.subtraction(v0);
            Vector3f edge2 = v2.subtraction(v0);
            Vector3f polyNormal = edge2.crossProduct(edge1);
            polyNormal.normalize();

            for (Integer idx : vIdx) {
                Vector3f current = vertexNormals.get(idx);
                vertexNormals.set(idx, current.addition(polyNormal));
            }
        }

        for (Vector3f vn : vertexNormals) {
            vn.normalize();
            normals.add(vn);
        }

        for (Polygon polygon : polygons) {
            ArrayList<Integer> nIdx = new ArrayList<>(polygon.getVertexIndices());
            polygon.setNormalIndices(nIdx);
        }
    }
}