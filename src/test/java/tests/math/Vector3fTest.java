package tests.math;

import com.cg.math.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Vector3fTest {
    private static final float eps = 1e-7f;

    @Test
    public void testAddition3f() {
        final Vector3f vector3f = new Vector3f(1.5f, 3, 7);
        final Vector3f result = vector3f.addition(new Vector3f(5, 3, 1));
        final Vector3f expectedResult = new Vector3f(6.5f, 6, 8);
        assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testSubtraction3f() {
        final Vector3f vector3f = new Vector3f(1.5f, 3, 14);
        final Vector3f result = vector3f.subtraction(new Vector3f(1, 1.5f, 7));
        final Vector3f expectedResult = new Vector3f(0.5f, 1.5f, 7);
        assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testMultiply3f() {
        final Vector3f vector3f = new Vector3f(1.5f, 3, 4);
        final Vector3f result = vector3f.multiply(1.5f);
        final Vector3f expectedResult = new Vector3f(2.25f, 4.5f, 6);
        assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testDivision3f() {
        final Vector3f vector3f = new Vector3f(1.5f, 3, 4.3f);
        try {
            vector3f.division(0);
            Assertions.fail();
        } catch (Exception e) {
            String expectedError = "Деление на ноль";
            Assertions.assertEquals(expectedError, e.getMessage());
        }
    }

    @Test
    public void testGetLength3f() {
        final Vector3f vector3f = new Vector3f(1, 1, 1);
        final float result = vector3f.getLength();
        final float expectedResult = (float) Math.sqrt(3);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testNormalize3f() {
        final Vector3f vector3f = new Vector3f(0, 0, 0);
        vector3f.normalize();
        assertTrue(Math.abs(vector3f.x) < eps);
        assertTrue(Math.abs(vector3f.y) < eps);
        assertTrue(Math.abs(vector3f.z) < eps);
    }

    @Test
    public void testDotProduct3f() {
        final Vector3f vector3f = new Vector3f(4, 3, 1.3f);
        final float result = vector3f.dotProduct(new Vector3f(3, 5, 4));
        final float expectedResult = 32.2f;
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testCrossProduct3f() {
        final Vector3f vector3f = new Vector3f(3, 1, 7);
        final Vector3f result = vector3f.crossProduct(new Vector3f(5, 3, 1));
        final Vector3f expectedResult = new Vector3f(-20, 32, 4);
        assertTrue(result.equals(expectedResult));
    }
}
