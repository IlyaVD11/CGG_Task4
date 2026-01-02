package mathematics.linAlg;

import java.util.Objects;

public class Matrix3x3 {
    public float[][] matrix3x3 = new float[3][3];

    public Matrix3x3() {

    }

    public void createMatrix3x3(float a11, float a12, float a13,
                                float a21, float a22, float a23,
                                float a31, float a32, float a33) {
        matrix3x3[0][0] = a11; matrix3x3[0][1] = a12; matrix3x3[0][2] = a13;
        matrix3x3[1][0] = a21; matrix3x3[1][1] = a22; matrix3x3[1][2] = a23;
        matrix3x3[2][0] = a31; matrix3x3[2][1] = a32; matrix3x3[2][2] = a33;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matrix3x3 matrix3x31)) return false;
        return Objects.deepEquals(matrix3x3, matrix3x31.matrix3x3);
    }

    public Matrix3x3 addition(Matrix3x3 other) {
        Matrix3x3 result = new Matrix3x3();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                result.matrix3x3[row][col] = this.matrix3x3[row][col] + other.matrix3x3[row][col];
            }
        }
        return result;
    }

    public Matrix3x3 subtraction(Matrix3x3 other) {
        Matrix3x3 result = new Matrix3x3();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                result.matrix3x3[row][col] = this.matrix3x3[row][col] - other.matrix3x3[row][col];
            }
        }
        return result;
    }

    public Vector3f multiplyOnVector(Vector3f vector) {
        float[] vecMatrix = new float[]{vector.x, vector.y, vector.z};
        float[] result = new float[3];
        for (int row = 0; row < 3; row++) {
            float sum = 0;
            for (int col = 0; col < 3; col++) {
                sum += matrix3x3[row][col] * vecMatrix[col];
            }
            result[row] = sum;
        }
        return new Vector3f(result[0], result[1], result[2]);
    }

    public Matrix3x3 multiply(Matrix3x3 other) {
        Matrix3x3 result = new Matrix3x3();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                float sum = 0;
                for (int index = 0; index < 3; index++) {
                    sum += this.matrix3x3[row][index] * other.matrix3x3[index][col];
                }
                result.matrix3x3[row][col] = sum;
            }
        }
        return result;
    }

    public Matrix3x3 transposition() {
        Matrix3x3 result = new Matrix3x3();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                result.matrix3x3[row][col] = this.matrix3x3[col][row];
            }
        }
        return result;
    }

    public static Matrix3x3 zeroMatrix3x3() {
        Matrix3x3 result = new Matrix3x3();
        result.createMatrix3x3(0, 0, 0,
                               0, 0, 0,
                               0, 0, 0);
        return result;
    }

    public static Matrix3x3 identityMatrix3x3() {
        Matrix3x3 result = new Matrix3x3();
        result.createMatrix3x3(1, 0, 0,
                               0, 1, 0,
                               0, 0, 1);
        return result;
    }

    public float determinant3x3() {
        return matrix3x3[0][0] * matrix3x3[1][1] * matrix3x3[2][2] +
                matrix3x3[1][0] * matrix3x3[2][1] * matrix3x3[0][2] +
                matrix3x3[0][1] * matrix3x3[1][2] * matrix3x3[2][0] -
                matrix3x3[0][2] * matrix3x3[1][1] * matrix3x3[2][0] -
                matrix3x3[0][0] * matrix3x3[2][1] * matrix3x3[1][2] -
                matrix3x3[2][2] * matrix3x3[0][1] * matrix3x3[1][0];
    }

    public Matrix3x3 reverseMatrix() throws Exception {
        float determinant = determinant3x3();
        if (determinant == 0) {
            throw new Exception("Определитель не должен быть равен нулю");
        }
        float reverseDeterminant = 1 / determinant;
        Matrix3x3 adjMatrix = Matrix3x3.zeroMatrix3x3();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                float[][] matrix2x2 = new float[2][2];
                int row2 = 0;
                for (int row3 = 0; row3 < 3; row3++) {
                    if (row3 == row) {
                        continue;
                    }
                    int col2 = 0;
                    for (int col3 = 0; col3 < 3; col3++) {
                        if (col3 == col) {
                            continue;
                        }
                        matrix2x2[row2][col2] = matrix3x3[row3][col3];
                        col2++;
                    }
                    row2++;
                }
                float minor = matrix2x2[0][0] * matrix2x2[1][1] - matrix2x2[0][1] * matrix2x2[1][0];
                adjMatrix.matrix3x3[row][col] = (float) (Math.pow(-1, row + col) * minor);
            }
        }
        Matrix3x3 adjMatrixT = adjMatrix.transposition();
        Matrix3x3 reversedMatrix = new Matrix3x3();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                reversedMatrix.matrix3x3[row][col] = reverseDeterminant * adjMatrixT.matrix3x3[row][col];
            }
        }
        return reversedMatrix;
    }

    public Vector3f gauss(Vector3f vector) {
        float[][] extendedMatrix = new float[3][4];
        float[] freeCoefficients = new float[]{vector.x, vector.y, vector.z};
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                extendedMatrix[row][col] = this.matrix3x3[row][col];
            }
            extendedMatrix[row][3] = freeCoefficients[row];
        }
        for (int row = 0; row < 3; row++) {
            if (extendedMatrix[row][row] == 0) {
                for (int row2 = row + 1; row2 < 3; row2++) {
                    if (extendedMatrix[row2][row] != 0) {
                        float[] temp = extendedMatrix[row];
                        extendedMatrix[row] = extendedMatrix[row2];
                        extendedMatrix[row2] = temp;
                        break;
                    }
                }
            }
            for (int row2 = row + 1; row2 < 3; row2++) {
                if (extendedMatrix[row2][row] != 0) {
                    float coefficient = extendedMatrix[row2][row] / extendedMatrix[row][row];
                    for (int col = row; col < 4; col++) {
                        extendedMatrix[row2][col] -= extendedMatrix[row][col] * coefficient;
                    }
                }
            }
        }
        float[] result = new float[3];
        for (int row = 2; row >= 0; row--) {
            result[row] = extendedMatrix[row][3];
            for (int col = row + 1; col < 3; col++) {
                result[row] -= extendedMatrix[row][col] * result[col];
            }
            result[row] /= extendedMatrix[row][row];
        }
        return new Vector3f(result[0], result[1], result[2]);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                stringBuilder.append(matrix3x3[row][col]).append("\t");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
