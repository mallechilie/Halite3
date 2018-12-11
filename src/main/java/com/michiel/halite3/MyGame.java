package com.michiel.halite3;

import com.michiel.halite3.hlt.*;

import java.util.*;
import java.util.stream.Collectors;

public class MyGame {
    final long randomSeed;
    public static Random rng;
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

        tickShips(me, gameMap, world, commandQueue);

        if (game.turnNumber <= 200 && me.halite >= Constants.SHIP_COST && !gameMap.at(me.shipyard).isOccupied() && gameMap.getTotalHalite() > 30000) {
            commandQueue.add(me.shipyard.spawn());
        }

        game.endTurn(commandQueue);
    }

    private void tickShips(Player me, GameMap gameMap, MyMap world, ArrayList<Command> commandQueue) {
//                Convert Ships to MyShips.
        List<MyShip> allShips = me.ships.values().stream().map(MyShip::new).collect(Collectors.toList());

//                Reset the turnCounters.
        allShips.forEach(ship -> ship.prepareTick(gameMap));

        Log.log("");

        switch (Constants.NAVIGATION_BEHAVIOUR) {
            case NAIVE:
                tickAllShips(me, world, commandQueue, allShips);
                break;
            case AGGRESSIVE: {
//                Get all ships that are standing still.
                List<MyShip> stillStandingShips = allShips.stream().filter(s -> !s.canMove()).collect(Collectors.toList());
//                Get all ships at structures.
                List<MyShip> shipsAtStructures = allShips.stream().filter(s -> gameMap.at(s.ship).structure != null).collect(Collectors.toList());
//                Get all ships that are full.
                List<MyShip> fullShips = allShips.stream().filter(s -> s.ship.halite > Constants.MAX_HALITE * 0.9).collect(Collectors.toList());
//                Get all other ships. (Looping over ships multiple times should not pose a problem.)
                List<MyShip> otherShips = allShips;

//                Let all the ships tick in order of priority.
                Log.log("stillStandingShips\t" + stillStandingShips.size());
                Log.log("shipsAtStructures\t" + shipsAtStructures.size());
                Log.log("fullShips\t" + fullShips.size());
                Log.log("otherShips\t" + otherShips.size());

                Log.log("");

                tickAllShips(me, world, commandQueue, stillStandingShips);
                tickAllShips(me, world, commandQueue, shipsAtStructures);
                break;
            }
        }
        Log.log("");
    }

    private void tickAllShips(Player me, MyMap world, ArrayList<Command> commandQueue, List<MyShip> ships) {
        for (final MyShip ship : ships)
//            if (!ship.hadTurn)
        {
            Log.log("Moving ship " + ship.ship.id);
            commandQueue.add(ship.tick(me, world));
        }
    }

}