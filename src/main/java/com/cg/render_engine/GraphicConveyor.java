package com.cg.render_engine;

import com.cg.math.*;

public class GraphicConveyor {

    public static Matrix4x4 rotateScaleTranslate(
            float scaleX,
            float scaleY,
            float scaleZ,
            float theta,
            float psi,
            float phi,
            Vector3f translationVector
    ) {
        Matrix4x4 scaleMatrix = new Matrix4x4();
        scaleMatrix.createMatrix4x4(
                scaleX, 0, 0, 0,
                0, scaleY, 0, 0,
                0, 0, scaleZ, 0,
                0, 0, 0, 1
        );

        Matrix4x4 rotateMatrixX = new Matrix4x4();
        rotateMatrixX.createMatrix4x4(
                1, 0, 0, 0,
                0, (float) Math.cos(theta), (float) Math.sin(theta), 0,
                0, -((float) Math.sin(theta)), (float) Math.cos(theta), 0,
                0, 0, 0, 1
        );

        Matrix4x4 rotateMatrixY = new Matrix4x4();
        rotateMatrixY.createMatrix4x4(
                (float) Math.cos(psi), 0, (float) Math.sin(psi), 0,
                0, 1, 0, 0,
                -((float) Math.sin(psi)), 0, (float) Math.cos(psi), 0,
                0, 0, 0, 1
        );

        Matrix4x4 rotateMatrixZ = new Matrix4x4();
        rotateMatrixZ.createMatrix4x4(
                (float) Math.cos(phi), (float) Math.sin(phi), 0, 0,
                -((float) Math.sin(phi)), (float) Math.cos(phi), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );

        Matrix4x4 rotateMatrix = rotateMatrixX.multiply(rotateMatrixY).multiply(rotateMatrixZ);

        Matrix4x4 translationMatrix = new Matrix4x4();
        translationMatrix.createMatrix4x4(
                1, 0, 0, translationVector.x,
                0, 1, 0, translationVector.y,
                0, 0, 1, translationVector.z,
                0, 0, 0, 1
        );

        Matrix4x4 modelMatrix = translationMatrix.multiply(rotateMatrix).multiply(scaleMatrix);
        return modelMatrix;
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target)  {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target, Vector3f up)  {
        Vector3f resultX;
        Vector3f resultY;
        Vector3f resultZ;

        resultZ = target.subtraction(eye);
        resultX = up.crossProduct(resultZ);
        resultY = resultZ.crossProduct(resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[] matrix = new float[]{
                resultX.x, resultX.y, resultX.z, -resultX.dotProduct(eye),
                resultY.x, resultY.y, resultY.z, -resultY.dotProduct(eye),
                resultZ.x, resultZ.y, resultZ.z, -resultZ.dotProduct(eye),
                0,         0,         0,         1
        };
        Matrix4x4 viewMatrix = new Matrix4x4();
        viewMatrix.createMatrix4x4(
                matrix[0], matrix[1], matrix[2], matrix[3],
                matrix[4], matrix[5], matrix[6], matrix[7],
                matrix[8], matrix[9], matrix[10], matrix[11],
                matrix[12], matrix[13], matrix[14], matrix[15]
        );
        return viewMatrix;
    }

    public static Matrix4x4 perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4x4 result = new Matrix4x4();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));

        result.createMatrix4x4(
                tangentMinusOnDegree / aspectRatio, 0, 0, 0,
                0, tangentMinusOnDegree, 0, 0,
                0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 2 * (nearPlane * farPlane) / (nearPlane - farPlane),
                0, 0, 1, 0
        );
        return result;
    }

    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Vector2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
