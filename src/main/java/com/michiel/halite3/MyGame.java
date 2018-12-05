package com.michiel.halite3;

import com.michiel.halite3.MyShip;
import com.michiel.halite3.hlt.*;
import java.util.ArrayList;
import java.util.Random;

public class MyGame {
    final long randomSeed;
    final Random rng;
    final Game game;

    public MyGame(long randomSeed) {
        this.randomSeed = randomSeed;
        rng = new Random(randomSeed);
        game = new Game();

        initialize();
    }

    public void initialize() {

        // At this point "game" variable is populated with initial map data.
        // This is a good place to do computationally expensive start-up pre-processing.
        // As soon as you call "ready" function below, the 2 second per turn timer will
        // start.
        game.ready("MyJavaBot");

        Log.log("Successfully created bot! My Player ID is " + game.myId + ". Bot rng seed is " + randomSeed + ".");
    }

    public void tick() {
        game.updateFrame();
        final Player me = game.me;
        final GameMap gameMap = game.gameMap;
        final MyMap world = new MyMap(gameMap);

        final ArrayList<Command> commandQueue = new ArrayList<>();

        for (final Ship ship : me.ships.values()) {
            final MyShip myShip = new MyShip(ship);
            commandQueue.add(myShip.tick(gameMap, me, world, rng, Constants.MAX_TURNS - game.turnNumber));
        }

        if (game.turnNumber <= 200 && me.halite >= Constants.SHIP_COST && !gameMap.at(me.shipyard).isOccupied() && gameMap.getTotalHalite() > 30000) {
            commandQueue.add(me.shipyard.spawn());
        }

        game.endTurn(commandQueue);
    }
}