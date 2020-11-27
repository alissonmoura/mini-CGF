package util;

import java.util.Random;

public class RandomCoordinate {

    private static Random random = new Random();

    public static int getX() {
        return random.nextInt(1778 - 20) + 10;
    }

    public static int getY() {
        return random.nextInt(980 - 20) + 10;
    }
}