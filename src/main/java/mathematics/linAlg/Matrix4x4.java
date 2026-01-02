package mathematics.linAlg;

import java.util.Objects;

public class Matrix4x4 {
    public float[][] matrix4x4 = new float[4][4];

    public Matrix4x4() {

    }

    public void createMatrix4x4(float a11, float a12, float a13, float a14,
                                float a21, float a22, float a23, float a24,
                                float a31, float a32, float a33, float a34,
                                float a41, float a42, float a43, float a44) {
        matrix4x4[0][0] = a11; matrix4x4[0][1] = a12; matrix4x4[0][2] = a13; matrix4x4[0][3] = a14;
        matrix4x4[1][0] = a21; matrix4x4[1][1] = a22; matrix4x4[1][2] = a23; matrix4x4[1][3] = a24;
        matrix4x4[2][0] = a31; matrix4x4[2][1] = a32; matrix4x4[2][2] = a33; matrix4x4[2][3] = a34;
        matrix4x4[3][0] = a41; matrix4x4[3][1] = a42; matrix4x4[3][2] = a43; matrix4x4[3][3] = a44;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matrix4x4 matrix4x41)) return false;
        return Objects.deepEquals(matrix4x4, matrix4x41.matrix4x4);
    }

    public Matrix4x4 addition(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result.matrix4x4[row][col] = this.matrix4x4[row][col] + other.matrix4x4[row][col];
            }
        }
        return result;
    }

    public Matrix4x4 subtraction(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result.matrix4x4[row][col] = this.matrix4x4[row][col] - other.matrix4x4[row][col];
            }
        }
        return result;
    }

    public Vector4f multiplyOnVector(Vector4f vector) {
        float[] vecMatrix = new float[]{vector.x, vector.y, vector.z, vector.w};
        float[] result = new float[4];
        for (int row = 0; row < 4; row++) {
            float sum = 0;
            for (int col = 0; col < 4; col++) {
                sum += matrix4x4[row][col] * vecMatrix[col];
            }
            result[row] = sum;
        }
        return new Vector4f(result[0], result[1], result[2], result[3]);
    }

    public Matrix4x4 multiply(Matrix4x4 other) {
        Matrix4x4 result = new  Matrix4x4();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                float sum = 0;
                for (int index = 0; index < 4; index++) {
                    sum += this.matrix4x4[row][index] * other.matrix4x4[index][col];
                }
                result.matrix4x4[row][col] = sum;
            }
        }
        return result;
    }

    public Matrix4x4 transposition() {
        Matrix4x4 result = new Matrix4x4();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result.matrix4x4[row][col] = this.matrix4x4[col][row];
            }
        }
        return result;
    }

    public static Matrix4x4 zeroMatrix4x4() {
        Matrix4x4 result = new Matrix4x4();
        result.createMatrix4x4(0, 0, 0, 0,
                               0, 0, 0, 0,
                               0, 0, 0, 0,
                               0, 0, 0, 0
                               );
        return result;
    }

    public static Matrix4x4 identityMatrix4x4() {
        Matrix4x4 result = new Matrix4x4();
        result.createMatrix4x4(1, 0, 0, 0,
                               0, 1, 0, 0,
                               0, 0, 1, 0,
                               0, 0, 0, 1
                               );
        return result;
    }

    public float determinant4x4() {
        float determinant = 0;
        for (int colOter = 0; colOter < 4; colOter++) {
            Matrix3x3 minorMatrix = new Matrix3x3();
            for (int row = 1; row < 4; row++) {
                for (int colInner = 0; colInner < 4; colInner++) {
                    if (colInner < colOter) {
                        minorMatrix.matrix3x3[row - 1][colInner] = this.matrix4x4[row][colInner];
                    } else if (colInner > colOter) {
                        minorMatrix.matrix3x3[row - 1][colInner - 1] = this.matrix4x4[row][colInner];
                    }
                }
            }
            determinant += (float) (this.matrix4x4[0][colOter] * Math.pow(-1, colOter) * minorMatrix.determinant3x3());
        }
        return determinant;
    }

    public Matrix4x4 reverseMatrix() throws Exception {
        float determinant = determinant4x4();
        if (determinant == 0) {
            throw new Exception("Определитель не должен быть равен нулю");
        }
        float reverseDeterminant = 1 / determinant;
        Matrix4x4 adjMatrix = new  Matrix4x4();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Matrix3x3 matrix3x3 = new Matrix3x3();
                int row2 = 0;
                for (int row3 = 0; row3 < 4; row3++) {
                    if (row3 == row) {
                        continue;
                    }
                    int col2 = 0;
                    for (int col3 = 0; col3 < 4; col3++) {
                        if (col3 == col) {
                            continue;
                        }
                        matrix3x3.matrix3x3[row2][col2] = this.matrix4x4[row3][col3];
                        col2++;
                    }
                    row2++;
                }
                float minor = matrix3x3.determinant3x3();
                adjMatrix.matrix4x4[row][col] = (float) (Math.pow(-1, row + col) * minor);
            }
        }
        Matrix4x4 adjMatrixT = adjMatrix.transposition();
        Matrix4x4 reversedMatrix = new Matrix4x4();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                reversedMatrix.matrix4x4[row][col] = reverseDeterminant * adjMatrixT.matrix4x4[row][col];
            }
        }
        return reversedMatrix;
    }

    public Vector4f gauss(Vector4f vector) {
        float[][] extendedMatrix = new float[4][5];
        float[] freeCoefficients = new float[]{vector.x, vector.y, vector.z, vector.w};
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                extendedMatrix[row][col] = this.matrix4x4[row][col];
            }
            extendedMatrix[row][4] = freeCoefficients[row];
        }
        for (int row = 0; row < 4; row++) {
            if (extendedMatrix[row][row] == 0) {
                for (int row2 = row + 1; row2 < 4; row2++) {
                    if (extendedMatrix[row2][row] != 0) {
                        float[] temp = extendedMatrix[row];
                        extendedMatrix[row] = extendedMatrix[row2];
                        extendedMatrix[row2] = temp;
                        break;
                    }
                }
            }
            for (int row2 = row + 1; row2 < 4; row2++) {
                if (extendedMatrix[row2][row] != 0) {
                    float coefficient = extendedMatrix[row2][row] / extendedMatrix[row][row];
                    for (int col = row; col < 5; col++) {
                        extendedMatrix[row2][col] -= extendedMatrix[row][col] * coefficient;
                    }
                }
            }
        }
        float[] result = new float[4];
        for (int row = 3; row >= 0; row--) {
            result[row] = extendedMatrix[row][4];
            for (int col = row + 1; col < 4; col++) {
                result[row] -= extendedMatrix[row][col] * result[col];
            }
            result[row] /= extendedMatrix[row][row];
        }
        return new Vector4f(result[0], result[1], result[2], result[3]);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                stringBuilder.append(matrix4x4[row][col]).append("\t");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
