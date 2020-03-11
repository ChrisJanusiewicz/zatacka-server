package util;

public class Vector2Int {
    public int x;
    public int y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2Int subtract(Vector2Int v1, Vector2Int v2) {
        return new Vector2Int(v1.x - v2.x, v1.y - v2.y);
    }

    public static float distSq(Vector2Int v1, Vector2Int v2) {
        return (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
    }

    public Vector2Int subtract(Vector2Int v) {
        return new Vector2Int(this.x - v.x, this.y - v.y);
    }

    public Vector2 toVector2() {
        return new Vector2(this.x, this.y);
    }

    public Vector2 unit() {
        float m = (float) Math.sqrt(x * x + y * y);
        return new Vector2((x / m), (y / m));
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }
}