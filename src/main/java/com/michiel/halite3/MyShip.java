package com.michiel.halite3;

import com.michiel.halite3.hlt.*;

import java.util.*;

public class MyShip {
    public Ship ship;
    private GameMap gameMap;
    public boolean hadTurn = false;

    public MyShip(Ship ship) {
        this.ship = ship;
    }

    public void prepareTick(GameMap gameMap){
        hadTurn = false;
        this.gameMap = gameMap;
    }

    public Command tick(Player me, MyMap world) {
        hadTurn = true;
        int upper = 800;
        int lower = 100;

        // Only move if the ship can actually move
        if (!canMove())
            return ship.stayStill();

        if (ship.halite > upper && gameMap.at(ship).halite < Constants.MAX_HALITE / 3 || ship.halite > Constants.MAX_HALITE * 0.95)
            return ship.move(gameMap.navigate(ship, me.shipyard.position));
        if (ship.halite < upper && ship.halite > lower)
            if (gameMap.at(ship).halite < Constants.MAX_HALITE / 10) return ship.move(navigateHaliteBruteForce(world));
            else return ship.stayStill();
        if (ship.halite < lower) return ship.move(navigateHaliteBruteForce(world));
        else return ship.stayStill();
    }

    public boolean canMove() {
        return gameMap.at(ship).halite / Constants.MOVE_COST_RATIO <= ship.halite;
    }

    public Direction randomMove() {
        // getUnsafeMoves normalizes for us
        ArrayList<Direction> all_cardinals = Direction.ALL_CARDINALS;
        for (int i = 0; i < all_cardinals.size() * 2; i++) {
            Direction direction = all_cardinals.get(MyGame.rng.nextInt(all_cardinals.size()));
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
        return gameMap.navigate(ship, destination);
    }

    public Direction navigateHaliteBruteForce(MyMap world) {
        Position destination = world.getHarvestLocationBruteForce(ship);
        if (destination.equals(ship.position))
            return Direction.STILL;
        Log.log("Ship " + ship.id + " thinks there is much halite at " + destination);
        Direction direction = gameMap.navigate(ship, destination);
        if (direction == Direction.STILL)
            direction = randomMove();
        return direction;

    }

    public Direction moveToBase(Ship ship, Player me, Random rng) {
        List<Entity> dropoffs = new ArrayList<>();
        for (Entity entity : me.dropoffs.values())
            dropoffs.add(entity);
        dropoffs.add(me.shipyard);
        Collections.sort(dropoffs, Comparator.comparingInt(o -> getDistanceTo(o.position)));
        return gameMap.navigate(ship, dropoffs.get(0).position);
    }

    private int getDistanceTo(Position position) {
        return gameMap.calculateDistance(ship.position, position);
    }
}