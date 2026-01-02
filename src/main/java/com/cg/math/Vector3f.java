package com.cg.math;

public class Vector3f {
    private static final float eps = 1e-7f;
    private static final float eps2 = 0.001f;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vector3f other) {
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public float x, y, z;

    public Vector3f addition(Vector3f other) {
        return new Vector3f(x + other.x, y + other.y, z + other.z);
    }

    public Vector3f subtraction(Vector3f other) {
        return new Vector3f(x - other.x, y - other.y, z - other.z);
    }

    public Vector3f multiply(float num) {
        return new Vector3f(x * num, y * num, z * num);
    }

    public Vector3f division(float num) throws Exception {
        if (num < eps2) {
            throw new Exception("Деление на ноль");
        }
        return new Vector3f(x / num, y / num, z / num);
    }

    public float getLength() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() throws Exception {
        float length = getLength();
        if (length < eps2) {
            throw new Exception("Деление на ноль");
        }
        x /= length;
        y /= length;
        z /= length;
    }

    public float dotProduct(Vector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3f crossProduct(Vector3f other) {
        return new Vector3f(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}