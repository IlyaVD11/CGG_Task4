package mathematics.conveyor;
import mathematics.linAlg.*;

public class GraphicConveyor {

    public static Matrix4x4 rotateScaleTranslate() {
        return Matrix4x4.identityMatrix4x4();
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target) throws Exception {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target, Vector3f up) throws Exception {
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
                tangentMinusOnDegree, 0, 0, 0,
                0, tangentMinusOnDegree / aspectRatio, 0, 0,
                0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 2 * (nearPlane * farPlane) / (nearPlane - farPlane),
                0, 0, 1, 0
        );
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4x4 matrix, final Vector3f vertex) {
        Vector4f vector4f = new Vector4f(vertex.x, vertex.y, vertex.z, 1.0F);
        Vector4f result = matrix.multiplyOnVector(vector4f);

        return new Vector3f(result.x, result.y, result.z);
    }

    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Vector2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
