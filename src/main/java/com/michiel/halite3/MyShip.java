package com.michiel.halite3;

import com.michiel.halite3.hlt.*;
import java.util.ArrayList;
import java.util.Random;

public class MyShip{
    public Ship ship;

    public MyShip(Ship ship)
    {
        this.ship = ship;
    }

    public Command tick(GameMap gameMap, Player me, Random rng)
    {
        int upper = 900;
        int lower = 100;
        if(ship.halite > upper){
            return ship.move(gameMap.naiveNavigate(ship, me.shipyard.position));
        }
        if(ship.halite < upper && ship.halite > lower){
            if (gameMap.at(ship).halite < Constants.MAX_HALITE / 10){
                // TODO: move towards more halite close-by.
                final Direction randomDirection = Direction.ALL_CARDINALS.get(rng.nextInt(4));
                return ship.move(randomDirection);
            }
            else{
                return ship.stayStill();
            }
        }
        if (ship.halite < lower) {
            // TODO: navigate towards more halite.
            final Direction randomDirection = Direction.ALL_CARDINALS.get(rng.nextInt(4));
            return ship.move(randomDirection);
        } else {
            return ship.stayStill();
        }
    }
}