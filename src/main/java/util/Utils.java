package util;

import java.util.Random;

public class Utils {

    private static Random rand = new Random();

    public static String printByteArrayX(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result = String.format("%s%X", b, b[i]);
        }
        return result;
    }


    private Vector2Int[] genSpacedPositions(int numPlayers, int width, int height, int minSqDist, float f) {
        Vector2Int[] ret = new Vector2Int[numPlayers];

        for (int i = 0; i < numPlayers; i++) {

            boolean spaced;
            do {
                spaced = true;
                ret[i] = getRandomPosition(width, height, f);
                for (int j = 0; j < i - 1; j++) {
                    float distSq = Vector2Int.distSq(ret[i], ret[j]);
                    if (distSq < minSqDist) {
                        spaced = false;
                    }
                }

            } while (!spaced);
        }

        return ret;
    }

    // use f to control how close positions can be to edges
    // 0.9  90% of map in X and Y dimensions respectively is spawnable space
    // 0.1  only a strip of 10% of the map is spawnable
    // recommended range: 0.7-0.9
    private synchronized Vector2Int getRandomPosition(int width, int height, float f) {
        // TODO: check if this must be synchronised (rand accessed by multiple threads simulataneously?)
        int x = Math.round((rand.nextFloat() * f + (1 - f) * 0.5f) * width);
        int y = Math.round((rand.nextFloat() * f + (1 - f) * 0.5f) * height);
        return new Vector2Int(x, y);
    }
}
