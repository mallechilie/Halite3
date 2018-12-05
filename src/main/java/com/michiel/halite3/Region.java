package com.michiel.halite3;

import com.michiel.halite3.hlt.GameMap;
import com.michiel.halite3.hlt.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RecursiveMap {
    public final GameMap gameMap;
    /**
     * The children are mapped in the following way:
     * 1 | 2
     * 3 | 4
     * The borders are @xSplit and @ySplit.
     */
    public final List<RecursiveMap> children;
    public final int xStart, xSplit, xEnd, yStart, ySplit, yEnd;
    public int halite;


    public RecursiveMap(GameMap gameMap, int xStart, int xEnd, int yStart, int yEnd) {
        this.gameMap = gameMap;
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.yStart = yStart;
        this.yEnd = yEnd;
        xSplit = xStart + (xEnd - xStart) / 2;
        ySplit = yStart + (yEnd - yStart) / 2;
        if (xEnd - xStart != 1 || yEnd - yStart != 1) {
            children = new ArrayList<>();
            initializeChildren();
        } else
            children = null;

        for (final RecursiveMap temp : children)
            if (temp != null) halite += temp.halite;
    }

    /**
     * Populates the list of children in the standard order. (Leaves null children in case there is no space for them.)
     */
    private void initializeChildren() {
        children.add(constructChild(xStart, xSplit, yStart, ySplit));
        children.add(constructChild(xSplit, xEnd, yStart, ySplit));
        children.add(constructChild(xStart, xSplit, ySplit, yEnd));
        children.add(constructChild(xSplit, xEnd, ySplit, yEnd));
    }

    private RecursiveMap constructChild(int xStart, int xEnd, int yStart, int yEnd) {
        return xStart - xEnd != 0 && yStart - yEnd != 0
                ? new RecursiveMap(gameMap, xStart, xEnd, yStart, yEnd)
                : null;
    }

    public Position getPosition() {
        return new Position(xStart + (xEnd - xStart) / 2, yStart + (yEnd - yStart) / 2);
    }

    public int getSize() {
        return (xEnd - xStart) * (yEnd - yStart);
    }

    public float getAvarageHalite() {
        return halite / getSize();
    }
}
