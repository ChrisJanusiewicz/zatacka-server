package game;

import util.Vector2;
import util.Vector2Int;

public class Physics {


    public static void putCircularCollider(byte[][] map, int x_centre, int y_centre, byte id) {
        int radius = 4;

        if (!(isInBounds(x_centre + radius, y_centre) && isInBounds(x_centre - radius, y_centre)
                && isInBounds(x_centre, y_centre + radius) && isInBounds(x_centre, y_centre - radius))) {
            return;
        }

        int x = 4, y = 0;
        map[x + x_centre][y + y_centre] = id;
        map[x + x_centre][-y + y_centre] = id;
        for (int i = -x + x_centre; i <= x + x_centre; ++i) {
            map[i][y + y_centre] = id;
        }
        map[y + x_centre][x + y_centre] = id;
        map[-y + x_centre][x + y_centre] = id;

        int P = 1 - radius;
        while (x > y) {
            y++;

            if (P <= 0)
                P = P + 2 * y + 1;
            else {
                x--;
                P = P + 2 * y - 2 * x + 1;
            }

            if (x < y)
                break;

            for (int i = -x + x_centre; i < x + x_centre; ++i) {
                map[i][y + y_centre] = id;
            }

            for (int i = -x + x_centre; i < x + x_centre; ++i) {
                map[i][-y + y_centre] = id;
            }

            for (int i = -y + x_centre; i < y + x_centre; ++i) {
                map[i][x + y_centre] = id;
            }

            for (int i = -y + x_centre; i < y + x_centre; ++i) {
                map[i][-x + y_centre] = id;
            }
        }
    }

    public static byte didCollide(byte[][] map, Vector2Int p1, Vector2Int p2, byte id, byte empty, byte wall) {

        int radius = 10;
        Vector2 directionVector = (p2.subtract(p1)).unit();
        Vector2 currentPosition = new Vector2(p2.x, p2.y);
        Vector2 positionAhead = currentPosition.add(directionVector.mult(radius));
        Vector2Int positionInt = positionAhead.toVector2Int();
        if (isInBounds(positionInt.x, positionInt.y) && map[positionInt.x][positionInt.y] == empty)
            return empty;
        else {

            System.out.println(String.format("Current position: {%d, %d}", positionInt.x, positionInt.y));
            if (isInBounds(positionInt.x, positionInt.y)) {
                System.out.println(String.format("Player %d collided with: %d", id, map[positionInt.x][positionInt.y]));
                return map[positionInt.x][positionInt.y];
            } else {
                System.out.println(String.format("Player %d collided with wall", id));
                return wall;
            }

        }

    }

    public static boolean isInBounds(int x, int y) {
        if (x < 0 || y < 0)
            return false;
        else if (x >= 1136 || y >= 640)
            return false;
        else
            return true;
    }

}