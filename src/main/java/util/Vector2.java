package util;

public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2 subtract(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }

    public static Vector2 mult(Vector2 v, float f) {
        return new Vector2(v.x * f, v.y * f);
    }

    public Vector2 add(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    public Vector2 add(Vector2 v) {
        return new Vector2(this.x + v.x, this.y + v.y);
    }

    public Vector2 subtract(Vector2 v) {
        return new Vector2(this.x - v.x, this.y - v.y);
    }

    public Vector2 mult(float f) {
        return new Vector2(this.x * f, this.y * f);
    }

    public Vector2 unit() {
        float m = (float) Math.sqrt(x * x + y * y);
        return new Vector2((x / m), (y / m));
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2Int toVector2Int() {
        return new Vector2Int(Math.round(x), Math.round(y));
    }

}