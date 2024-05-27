package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class locationUtil {
    // player coords contain only x and z
    public static boolean coordsIsInCoordinates(Integer[] playerCoords, Integer[] topCoords, Integer[] bottomCoords) {
        return (playerCoords[0] >= Math.min(topCoords[0], bottomCoords[0]) && playerCoords[0] <= Math.max(topCoords[0], bottomCoords[0]))
                &&
                (playerCoords[1] >= Math.min(topCoords[1], bottomCoords[1]) && playerCoords[0] <= Math.max(topCoords[1], bottomCoords[1]));
    }

    public static boolean yInIsYPoints(Integer y, Integer topY, Integer bottomY) {
        return y <= Math.max(topY, bottomY) && y >= Math.min(topY, bottomY);
    }
}
