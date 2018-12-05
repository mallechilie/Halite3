package com.michiel.halite3;

import com.michiel.halite3.hlt.GameMap;
import com.michiel.halite3.hlt.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Region {
    public final GameMap gameMap;
    /**
     * The children are mapped in the following way:
     * 1 | 2
     * 3 | 4
     * The borders are @xSplit and @ySplit.
     */
    public final List<Region> children;
    public final int xStart, xSplit, xEnd, yStart, ySplit, yEnd;
    public int halite;


    public Region(GameMap gameMap, int xStart, int xEnd, int yStart, int yEnd) {
        {
            this.gameMap = gameMap;
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
        }
        xSplit = xStart + getWidth() / 2;
        ySplit = yStart + getHeight() / 2;
        if (getSize() > 1) {
            children = new ArrayList<>();
            initializeChildren();
        } else
            children = null;

        if (hasChildren()) {
            for (final Region temp : children)
                if (temp != null) halite += temp.halite;
        } else
            halite = gameMap.at(new Position(xStart, yStart)).halite;
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

    private Region constructChild(int xStart, int xEnd, int yStart, int yEnd) {
        return xEnd - xStart > 1 && yEnd - yStart > 1
                ? new Region(gameMap, xStart, xEnd, yStart, yEnd)
                : null;
    }

    public int getSize() {
        return getWidth() * getHeight();
    }

    public float getAverageHalite() {
        return (float) halite / getSize();
    }

    public int getAverageDistance(Position position) {
        return gameMap.calculateDistance(position, getPosition());
    }

    public boolean positionInsideRegion(Position position) {
        return position.x >= xStart
                && position.x < xEnd
                && position.y >= yStart
                && position.y < yEnd;
    }

    public int getClosestDistance(Position position) {
        if (positionInsideRegion(position))
            return 0;
        ArrayList<Integer> distances = new ArrayList<>();
        distances.add(gameMap.calculateDistance(position, new Position(xStart, yStart)));
        distances.add(gameMap.calculateDistance(position, new Position(xEnd - 1, yStart)));
        distances.add(gameMap.calculateDistance(position, new Position(xStart, yEnd - 1)));
        distances.add(gameMap.calculateDistance(position, new Position(xEnd - 1, yEnd - 1)));
        return Collections.min(distances);
    }

    public int getWidth() {
        return xEnd - 1 - xStart;
    }

    public int getHeight() {
        return yEnd - 1 - yStart;
    }

    public Position getPosition() {
        return new Position(xStart + getWidth() / 2, yStart + getHeight() / 2);
    }

    public boolean hasChildren() {
        return children != null;
    }
}
