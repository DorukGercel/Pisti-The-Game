package com.group7.server.definitions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Table holds the game id and the game itself as key value pair.
 * Attention: Only created once.
 */
@Component
public class GameTable  {
    /** Application context of the running app*/
    private final ApplicationContext mApplicationContext;
    /** Map that holds the entries as key value pair*/
    private final Map<Long, Game>    mGameMap;
    /** Id of the new game to be created*/
    private       Long               mGameId;

    /** Constructor of the table; called when app starts*/
    @Autowired
    public GameTable(ApplicationContext applicationContext){
        this.mApplicationContext = applicationContext;
        this.mGameMap = new HashMap<>();
        this.mGameId = 0L;
    }

    /** Adds a new game entry to the table*/
    public Long addNewGame(){
        Long newGameId = mGameId;
        Game newGame = mApplicationContext.getBean("Game", Game.class);
        mGameMap.put(newGameId, newGame);
        mGameId++;
        return newGameId;
    }

    /** Return the game entry from the table with given game id*/
    public Game getGame(Long gameId){
        return mGameMap.get(gameId);
    }
}
