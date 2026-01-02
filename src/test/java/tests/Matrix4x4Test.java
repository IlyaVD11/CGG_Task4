package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import mathematics.linAlg.Matrix4x4;
import mathematics.linAlg.Vector4f;

public class Matrix4x4Test {

    @Test
    public void testAddition4x4() {
        final Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.createMatrix4x4(1, 7, 1.3f, 3,
                                  4, 17, 9, 7,
                                  17, 4.5f, 7, 4,
                                  9, 1, 3, 2);
        final Matrix4x4 other4x4 = new Matrix4x4();
        other4x4.createMatrix4x4(2, 3, 5, 1,
                                 4, 16, 1, 3,
                                 12.3f, 7, 4, 9,
                                 7, 5, 3, 6);
        final Matrix4x4 result = matrix4x4.addition(other4x4);
        final Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(3, 10, 6.3f, 4,
                                       8, 33, 10, 10,
                                       29.3f, 11.5f, 11, 13,
                                       16, 6, 6, 8);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testSubtraction4x4() {
        final Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.createMatrix4x4(1, 7, 1.3f, 3,
                                  4, 17, 9, 7,
                                  17, 4.5f, 7, 4,
                                  9, 1, 3, 2);
        final Matrix4x4 other4x4 = new Matrix4x4();
        other4x4.createMatrix4x4(2, 3, 5, 1,
                                 4, 16, 1, 3,
                                 12.3f, 7, 4, 9,
                                 7, 5, 3, 6);
        final Matrix4x4 result = matrix4x4.subtraction(other4x4);
        final Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(-1, 4, -3.7f, 2,
                                       0, 1, 8, 4,
                                       4.7f, -2.5f, 3, -5,
                                       2, -4, 0, -4);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testMultiplyOnVector4x4() {
        final Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.createMatrix4x4(1, 2, 3, 1,
                                  4, 5, 6, 2,
                                  7, 8, 9, 3,
                                  4, 7, 6, 4);
        final Vector4f result = matrix4x4.multiplyOnVector(new Vector4f(3, 1, 7, 4));
        final Vector4f expectedResult = new Vector4f(30, 67, 104, 77);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testMultiply4x4() {
        final Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.createMatrix4x4(2, -1, 4, 3,
                                  8, 5, -3, 1,
                                  1, 6, 2, 1,
                                  3, 4, 3, 1);
        final Matrix4x4 other4x4 = new Matrix4x4();
        other4x4.createMatrix4x4(0, 2, 1, 1,
                                 4, -4, 2, 3,
                                 5, -2, 3, 4,
                                 3, 1, 1, 5);
        final Matrix4x4 result = matrix4x4.multiply(other4x4);
        final Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(25, 3, 15, 30,
                                       8, 3, 10, 16,
                                       37, -25, 20, 32,
                                       34, -15, 21, 32);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testTransposition4x4() {
        final Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.createMatrix4x4(1, 7, 1.3f, 7,
                                  4, 17, 9, 1,
                                  17, 4.5f, 7, 17,
                                  7, 1.3f, 7, 3);
        final Matrix4x4 result = matrix4x4.transposition();
        final Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(1, 4, 17, 7,
                                       7, 17, 4.5f, 1.3f,
                                       1.3f, 9, 7, 7,
                                       7, 1, 17, 3);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testZeroMatrix4x4() {
        final Matrix4x4 result = Matrix4x4.zeroMatrix4x4();
        final Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(0, 0, 0, 0,
                                       0, 0, 0, 0,
                                       0, 0, 0, 0,
                                       0, 0, 0, 0);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testIdentityMatrix4x4() {
        final Matrix4x4 result = Matrix4x4.identityMatrix4x4();
        final Matrix4x4 expectedResult = new Matrix4x4();
        expectedResult.createMatrix4x4(1, 0, 0, 0,
                                       0, 1, 0, 0,
                                       0, 0, 1, 0,
                                       0, 0, 0, 1
        );
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testDeterminant4x4() {
        final Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.createMatrix4x4(-3, 1, 5, -8,
                                  1, 4, 1, -1,
                                  2, 2, -5, 1,
                                  -2, -7, 1, 3);
        final float result = matrix4x4.determinant4x4();
        final float expectedResult = 15;
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testReverseMatrix4x4() {
        final Matrix4x4 result = new Matrix4x4();
        result.createMatrix4x4(2, 3, 4, 5,
                               4, 6, 8, 10,
                               6, 9, 12, 15,
                               8, 12, 16, 20);
        try {
            result.reverseMatrix();
        } catch (Exception e) {
            String expectedError = "Определитель не должен быть равен нулю";
            Assertions.assertEquals(expectedError, e.getMessage());
        }
    }

    @Test
    public void gauss4x4() {
        final Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.createMatrix4x4(1, 1, -1, 4,
                                  3, 2, -1, 2,
                                  -1, -1, 2, 2,
                                  2, 1, -2, -1);
        final Vector4f result = matrix4x4.gauss(new Vector4f(2, 0, 7, -7));
        final Vector4f expectedResult = new Vector4f(-1, 2, 3, 1);
        Assertions.assertTrue(result.equals(expectedResult));
    }
}
