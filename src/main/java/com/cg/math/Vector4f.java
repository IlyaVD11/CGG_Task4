package com.cg.math;

public class Vector4f {
    private static final float eps = 1e-7f;
    private static final float eps2 = 0.001f;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public boolean equals(Vector4f other) {
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps && Math.abs(w - other.w) < eps;
    }

    public float x, y, z, w;

    public Vector4f addition(Vector4f other) {
        return new Vector4f(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Vector4f subtraction(Vector4f other) {
        return new Vector4f(x - other.x, y - other.y, z - other.z, w - other.w);
    }

    public Vector4f multiply(float num) {
        return new Vector4f(x * num, y * num, z * num, w * num);
    }

    public Vector4f division(float num) throws Exception {
        if (num < eps2) {
            throw new Exception("Деление на ноль");
        }
        return new Vector4f(x / num, y / num, z / num, w / num);
    }

    public float getLength() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public void normalize() throws Exception {
        float length = getLength();
        if (length < eps2) {
            throw new Exception("Деление на ноль");
        }
        x /= length;
        y /= length;
        z /= length;
        w /= length;
    }

    public float dotProduct(Vector4f other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
