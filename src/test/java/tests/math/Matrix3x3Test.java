package tests.math;

import com.cg.math.Matrix3x3;
import com.cg.math.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Matrix3x3Test {

    @Test
    public void testAddition3x3() {
        final Matrix3x3 matrix3x3 = new Matrix3x3();
        matrix3x3.createMatrix3x3(1, 7, 1.3f,
                                  4, 17, 9,
                                  17, 4.5f, 7);
        final Matrix3x3 other3x3 = new Matrix3x3();
        other3x3.createMatrix3x3(2, 3, 5,
                               4, 16, 1,
                               12.3f, 7, 4);
        final Matrix3x3 result = matrix3x3.addition(other3x3);
        final Matrix3x3 expectedResult = new Matrix3x3();
        expectedResult.createMatrix3x3(3, 10, 6.3f,
                                       8, 33, 10,
                                       29.3f, 11.5f, 11);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testSubtraction3x3() {
        final Matrix3x3 matrix3x3 = new Matrix3x3();
        matrix3x3.createMatrix3x3(1, 7, 1.3f,
                                  4, 17, 9,
                                  17, 4.5f, 7);
        final Matrix3x3 other3x3 = new Matrix3x3();
        other3x3.createMatrix3x3(2, 3, 5,
                                 4, 16, 1,
                                 12.3f, 7, 4);
        final Matrix3x3 result = matrix3x3.subtraction(other3x3);
        final Matrix3x3 expectedResult = new Matrix3x3();
        expectedResult.createMatrix3x3(-1, 4, -3.7f,
                                       0, 1, 8,
                                       4.7f, -2.5f, 3);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testMultiplyOnVector3x3() {
        final Matrix3x3 matrix3x3 = new Matrix3x3();
        matrix3x3.createMatrix3x3(1, 2, 3,
                                  4, 5, 6,
                                  7, 8, 9);
        final Vector3f result = matrix3x3.multiplyOnVector(new Vector3f(3, 1, 7));
        final Vector3f expectedResult = new Vector3f(26, 59, 92);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testMultiply3x3() {
        final Matrix3x3 matrix3x3 = new Matrix3x3();
        matrix3x3.createMatrix3x3(2, -1, 4,
                                  8, 5, -3,
                                  1, 6, 2);
        final Matrix3x3 other3x3 = new Matrix3x3();
        other3x3.createMatrix3x3(0, 2, 1,
                                 4, -4, 2,
                                 5, -2, 3);
        final Matrix3x3 result = matrix3x3.multiply(other3x3);
        final Matrix3x3 expectedResult = new Matrix3x3();
        expectedResult.createMatrix3x3(16, 0, 12,
                                       5, 2, 9,
                                       34, -26, 19);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testTransposition3x3() {
        final Matrix3x3 matrix3x3 = new Matrix3x3();
        matrix3x3.createMatrix3x3(1, 7, 1.3f,
                                  4, 17, 9,
                                  17, 4.5f, 7);
        final Matrix3x3 result = matrix3x3.transposition();
        final Matrix3x3 expectedResult = new Matrix3x3();
        expectedResult.createMatrix3x3(1, 4, 17,
                                       7, 17, 4.5f,
                                       1.3f, 9, 7);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testZeroMatrix3x3() {
        final Matrix3x3 result = Matrix3x3.zeroMatrix3x3();
        final Matrix3x3 expectedResult = new Matrix3x3();
        expectedResult.createMatrix3x3(0, 0, 0,
                                       0, 0, 0,
                                       0, 0, 0);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testIdentityMatrix3x3() {
        final Matrix3x3 result = Matrix3x3.identityMatrix3x3();
        final Matrix3x3 expectedResult = new Matrix3x3();
        expectedResult.createMatrix3x3(1, 0, 0,
                                       0, 1, 0,
                                       0, 0, 1
        );
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testDeterminant3x3() {
        final Matrix3x3 matrix3x3 = new Matrix3x3();
        matrix3x3.createMatrix3x3(4, -3, 2,
                                  5, -4, -8,
                                  1, -2, -5);
        final float result = matrix3x3.determinant3x3();
        final float expectedResult = -47;
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testReverseMatrix3x3() {
        final Matrix3x3 result = new Matrix3x3();
        result.createMatrix3x3(2, 3, 4,
                               4, 6, 8,
                               6, 9, 12);
        try {
            result.reverseMatrix();
        } catch (Exception e) {
            String expectedError = "Определитель не должен быть равен нулю";
            Assertions.assertEquals(expectedError, e.getMessage());
        }
    }

    @Test
    public void gauss3x3() {
        final Matrix3x3 matrix3x3 = new Matrix3x3();
        matrix3x3.createMatrix3x3(2, -1, 4,
                                  1, 2, -3,
                                  2, 5, 1);
        final Vector3f result = matrix3x3.gauss(new Vector3f(1, 8, 10));
        final Vector3f expectedResult = new Vector3f(3, 1, -1);
        Assertions.assertTrue(result.equals(expectedResult));
    }
}
