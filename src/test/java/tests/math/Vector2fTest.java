package tests.math;

import com.cg.math.Vector2f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Vector2fTest {
    private static final float eps = 1e-7f;

    @Test
    public void testAddition2f() {
        final Vector2f vector2f = new Vector2f(1.5f, 3);
        final Vector2f result = vector2f.addition(new Vector2f(5, 3));
        final Vector2f expectedResult = new Vector2f(6.5f, 6);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testSubtraction2f() {
        final Vector2f vector2f = new Vector2f(1.5f, 3);
        final Vector2f result = vector2f.subtraction(new Vector2f(1, 1.5f));
        final Vector2f expectedResult = new Vector2f(0.5f, 1.5f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testMultiply2f() {
        final Vector2f vector2f = new Vector2f(1.5f, 3);
        final Vector2f result = vector2f.multiply(1.5f);
        final Vector2f expectedResult = new Vector2f(2.25f, 4.5f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testDivision2f() {
        final Vector2f vector2f = new Vector2f(1.5f, 3);
        try {
            vector2f.division(0);
            Assertions.fail();
        } catch (Exception e) {
            String expectedError = "Деление на ноль";
            Assertions.assertEquals(expectedError, e.getMessage());
        }
    }

    @Test
    public void testGetLength2f() {
        final Vector2f vector2f = new Vector2f(4, 3);
        final float result = vector2f.getLength();
        final float expectedResult = 5;
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testNormalize2f() {
        final Vector2f vector2f = new Vector2f(0, 0);
        vector2f.normalize();
        assertTrue(Math.abs(vector2f.x) < eps);
        assertTrue(Math.abs(vector2f.y) < eps);
    }

    @Test
    public void testDotProduct2f() {
        final Vector2f vector2f = new Vector2f(4, 3);
        final float result = vector2f.dotProduct(new Vector2f(3, 5));
        final float expectedResult = 27;
        Assertions.assertEquals(expectedResult, result);
    }
}
