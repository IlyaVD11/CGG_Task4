package tests.math;

import com.cg.math.Vector2f;
import com.cg.math.Vector4f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Vector4fTest {
    private static final float eps = 1e-7f;

    @Test
    public void testAddition4f() {
        final Vector4f vector4f = new Vector4f(1.5f, 3, 4.2f, 1);
        final Vector4f result = vector4f.addition(new Vector4f(5, 3, 1.9f, 3));
        final Vector4f expectedResult = new Vector4f(6.5f, 6, 6.1f, 4);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testSubtraction4f() {
        final Vector4f vector4f = new Vector4f(1.5f, 3, 10, 17);
        final Vector4f result = vector4f.subtraction(new Vector4f(1, 1.5f, 7.9f, 9));
        final Vector4f expectedResult = new Vector4f(0.5f, 1.5f, 2.1f, 8);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testMultiply4f() {
        final Vector4f vector4f = new Vector4f(1.5f, 3, 4, 6);
        final Vector4f result = vector4f.multiply(1.5f);
        final Vector4f expectedResult = new Vector4f(2.25f, 4.5f, 6, 9);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testDivision4f() {
        final Vector4f vector4f = new Vector4f(1.5f, 3, 17.3f, 7);
        try {
            vector4f.division(0);
            Assertions.fail();
        } catch (Exception e) {
            String expectedError = "Деление на ноль";
            Assertions.assertEquals(expectedError, e.getMessage());
        }
    }

    @Test
    public void testGetLength4f() {
        final Vector4f vector4f = new Vector4f(4, 3, 1, 5);
        final float result = vector4f.getLength();
        final float expectedResult = (float) Math.sqrt(51);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testNormalize4f() {
        final Vector4f vector4f = new Vector4f(0, 0, 0, 0);
        vector4f.normalize();
        assertTrue(Math.abs(vector4f.x) < eps);
        assertTrue(Math.abs(vector4f.y) < eps);
        assertTrue(Math.abs(vector4f.z) < eps);
        assertTrue(Math.abs(vector4f.w) < eps);
    }

    @Test
    public void testDotProduct4f() {
        final Vector4f vector4f = new Vector4f(4, 3, 1.5f, 7);
        final float result = vector4f.dotProduct(new Vector4f(3, 5, 4, 3));
        final float expectedResult = 54;
        Assertions.assertEquals(expectedResult, result);
    }
}
