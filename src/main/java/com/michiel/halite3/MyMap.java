package com.michiel.halite3;

import com.michiel.halite3.hlt.GameMap;
import com.michiel.halite3.hlt.Position;
import com.michiel.halite3.hlt.Ship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyMap {
    public final Region world;

    public MyMap(GameMap gameMap) {
        world = new Region(gameMap, 0, gameMap.width - 1, 0, gameMap.height - 1);
    }

    public Position getHarvestLocationByRegion(Ship ship) {
        Region efficientRegion = world;
        while(regionIsClose(ship, efficientRegion) && efficientRegion.hasChildren())
            efficientRegion = efficientChild(ship, efficientRegion);
        return efficientRegion.getPosition();
    }

    public Position getHarvestLocationBruteForce(Ship ship) {
        Position efficientPosition = ship.position;
        float efficiency = positionEfficiency(ship, world.gameMap, ship.position);
        for(int x = 0; x < world.gameMap.width; x++)
            for(int y = 0; y < world.gameMap.height; y++)
            {
                final Position temp = new Position(x, y);
                final float tempEfficiency = positionEfficiency(ship, world.gameMap, temp);
                if(tempEfficiency > efficiency && world.gameMap.at(temp).isEmpty())
                {
                    efficiency = tempEfficiency;
                    efficientPosition = temp;
                }
            }
        return efficientPosition;
    }

    private static Region efficientChild(Ship ship, Region parent){
        ArrayList<Region> children = new ArrayList<>();
        for(Region child : parent.children)
            if(child != null)
                children.add(child);
        Collections.sort(children, Comparator.comparingInt(o -> -o.halite));
        return children.get(0);
    }

    private static float regionEfficiency(Ship ship, Region region) {
        return region.getAverageHalite() / (region.getClosestDistance(ship.position) + 1);
    }

    private static float positionEfficiency(Ship ship, GameMap gameMap, Position position) {
        return (float)gameMap.at(position).halite / (gameMap.calculateDistance(ship.position, position) + 1);
    }

    private static boolean regionIsClose(Ship ship, Region region){
        return region.getClosestDistance(ship.position) < region.getWidth()+ region.getHeight();
    }
}
