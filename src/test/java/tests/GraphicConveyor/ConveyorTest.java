package tests.GraphicConveyor;

import com.cg.math.Matrix4x4;
import com.cg.math.Vector2f;
import com.cg.math.Vector3f;
import com.cg.render_engine.GraphicConveyor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConveyorTest {
    @Test
    public void testRotateScaleTranslate() {
        Matrix4x4 result = GraphicConveyor.rotateScaleTranslate(
                2, 3, 4,
                 0, 0, 0,
             10, 20, 30
        );
        Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(
                2, 0, 0, 10,
                0, 3, 0, 20,
                0, 0, 4, 30,
                0, 0, 0, 1
        );
        assertEquals(expectedResult, result);
    }

    @Test
    public void testLookAt() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);

        Matrix4x4 result = GraphicConveyor.lookAt(eye, target, up);
        Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(
                -1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, -1, 10,
                0, 0, 0, 1
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void testPerspective() {
        float fov = 45;
        float aspectRatio = 1.0F;
        float nearPlane = 0.1F;
        float farPlane = 100.0F;
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));

        Matrix4x4 result = GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
        Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(
                tangentMinusOnDegree / aspectRatio, 0, 0, 0,
                0, tangentMinusOnDegree, 0, 0,
                0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 2 * (nearPlane * farPlane) / (nearPlane - farPlane),
                0, 0, 1, 0
        );
        assertEquals(expectedResult, result);
    }

    @Test
    public void testVertexToPoint() {
        float epsilon = 1e-6f;
        Vector3f vertex = new Vector3f(0.5F, 0.5F, 0);
        int width = 800;
        int height = 600;

        Vector2f result = GraphicConveyor.vertexToPoint(vertex, width, height);
        Vector2f expectedResult = new Vector2f(800, 0);
        assertTrue(Math.abs(result.x - expectedResult.x) <= epsilon);
        assertTrue(Math.abs(result.y - expectedResult.y) <= epsilon);
    }
}
