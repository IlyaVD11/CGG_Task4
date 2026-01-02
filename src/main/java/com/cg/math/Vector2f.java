package com.cg.math;

public class Vector2f {
    private static final float eps = 1e-7f;
    private static final float eps2 = 0.001f;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Vector2f other) {
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps;
    }

    public float x, y;

    public Vector2f addition(Vector2f other) {
        return new Vector2f(x + other.x, y + other.y);
    }

    public Vector2f subtraction(Vector2f other) {
        return new Vector2f(x - other.x, y - other.y);
    }

    public Vector2f multiply(float num) {
        return new Vector2f(x * num, y * num);
    }

    public Vector2f division(float num) throws Exception {
        if (num < eps2) {
            throw new Exception("Деление на ноль");
        }
        return new Vector2f(x / num, y / num);
    }

    public float getLength() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        float length = getLength();
        if (length < eps2) {
            x = 0.0f;
            y = 0.0f;
        } else {
            x /= length;
            y /= length;
        }
    }

    public float dotProduct(Vector2f other) {
        return x * other.x + y * other.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}