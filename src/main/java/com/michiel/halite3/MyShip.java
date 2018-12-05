package com.michiel.halite3;

import com.michiel.halite3.hlt.*;

import java.util.ArrayList;
import java.util.Random;

public class MyShip {
    public Ship ship;

    public MyShip(Ship ship) {
        this.ship = ship;
    }

    public Command tick(GameMap gameMap, Player me, MyMap world, Random rng, int turnsLeft) {
        int upper = 800;
        int lower = 100;

//        if(turnsLeft < 20)
//            return ship.move(gameMap.unsafeNavigate(ship, me.shipyard.position, rng));
        if (ship.halite > upper && gameMap.at(ship).halite < Constants.MAX_HALITE / 10 || ship.halite > Constants.MAX_HALITE * 0.95) {
            // Move to shipyard if almost full and not much halite  here
            return ship.move(gameMap.naiveNavigate(ship, me.shipyard.position, rng));
        }
        if (ship.halite < upper && ship.halite > lower) {
            if (gameMap.at(ship).halite < Constants.MAX_HALITE / 10) {
                final Direction haliteDirection = navigateHaliteBruteForce(world, rng);
                return ship.move(haliteDirection);
            } else {
                return ship.stayStill();
            }
        }
        if (ship.halite < lower) {
            final Direction randomDirection = navigateHaliteBruteForce(world, rng);
            return ship.move(randomDirection);
        } else {
            return ship.stayStill();
        }
    }

    public Direction randomMove(GameMap gameMap, Random rng) {
        // getUnsafeMoves normalizes for us
        ArrayList<Direction> all_cardinals = Direction.ALL_CARDINALS;
        for (int i = 0; i < all_cardinals.size() * 2; i++) {
            Direction direction = all_cardinals.get(rng.nextInt(all_cardinals.size()));
            final Position targetPos = ship.position.directionalOffset(direction);
            if (!gameMap.at(targetPos).isOccupied()) {
                gameMap.at(targetPos).markUnsafe(ship);
                return direction;
            }
        }

        return Direction.STILL;
    }
    public Direction navigateHaliteRegions(MyMap world, Random rng) {
        Position destination = world.getHarvestLocationByRegion(ship);
        Log.log("Ship " + ship.id + " thinks there is much halite around " + destination);
        return world.world.gameMap.naiveNavigate(ship, destination, rng);
    }
    public Direction navigateHaliteBruteForce(MyMap world, Random rng){
        Position destination = world.getHarvestLocationBruteForce(ship);
        if(destination.equals(ship.position))
            return Direction.STILL;
        Log.log("Ship " + ship.id + " thinks there is much halite at " + destination);
        Direction direction = world.world.gameMap.naiveNavigate(ship, destination, rng);
        if(direction==Direction.STILL)
            direction = randomMove(world.world.gameMap, rng);
        return direction;

    }
}