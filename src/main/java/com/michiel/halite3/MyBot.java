package com.michiel.halite3;

import com.michiel.halite3.MyGame;

public class MyBot {
    public static void main(final String[] args) {
        
        final long randomSeed = args.length > 1 ? Integer.parseInt(args[1]) : System.nanoTime();

        MyGame myGame = new MyGame(randomSeed);

        for (;;) 
            myGame.tick();
    }
}
