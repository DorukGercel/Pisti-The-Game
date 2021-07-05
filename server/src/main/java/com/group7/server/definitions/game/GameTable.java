package com.group7.server.definitions.game;

import com.group7.server.definitions.game.Game;
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
    private final ApplicationContext            mApplicationContext;
    /** Map that holds the entries as key value pair*/
    private final Map<Long, Game>               mGameMap;
    /** Map that holds the entries for multiplayer games as key value pair*/
    private final Map<Long, MultiplayerGame>    mMultiplayerGameMap;
    /** Id of the new game to be created*/
    private       Long                          mGameId;
    /** Id of the new multiplayer game to be created*/
    private       Long                          mMultiplayerGameId;

    /** Constructor of the table; called when app starts*/
    @Autowired
    public GameTable(ApplicationContext applicationContext){
        this.mApplicationContext = applicationContext;
        this.mGameMap = new HashMap<>();
        this.mMultiplayerGameMap = new HashMap<>();
        this.mGameId = 1L;
        this.mMultiplayerGameId = 1L;
    }

    /** Adds a new game entry to the table*/
    public Long addNewGame(){
        Long newGameId = mGameId;
        Game newGame = mApplicationContext.getBean("Game", Game.class);
        mGameMap.put(newGameId, newGame);
        mGameId++;
        return newGameId;
    }

    /** Assigns a new multiplayer game entry to the table*/
    public Long assignToNewMultiplayerGame(Long activePlayerId) {
        for (Map.Entry<Long, MultiplayerGame> multiplayerGameEntry : mMultiplayerGameMap.entrySet()) {
            MultiplayerGame multiplayerGame = multiplayerGameEntry.getValue();
            if (!multiplayerGame.isGameRunning()) {
                // TODO: Remove print
                System.out.println("ASSIGNED GAME ID: " + multiplayerGameEntry.getKey());
                // Set the game running
                multiplayerGame.startToRun(activePlayerId);
                // If one player waiting game found return that id
                return multiplayerGameEntry.getKey();
            }
        }
        // If can't find game, create new game
        return addNewMultiplayerGame(activePlayerId);
    }

    /** Adds a new multiplayer game entry to the table*/
    private Long addNewMultiplayerGame(Long activePlayerId){
        Long newMultiplayerGameId = mMultiplayerGameId;
        MultiplayerGame newMultiplayerGame = mApplicationContext.getBean("MultiplayerGame", MultiplayerGame.class);
        mMultiplayerGameMap.put(newMultiplayerGameId, newMultiplayerGame);
        mMultiplayerGameId++;
        // TODO: Remove print
        System.out.println("NEW GAME ID: " + newMultiplayerGameId);
        // Wait for game to start
        newMultiplayerGame.waitToRun(activePlayerId);
        // Return the game id
        return newMultiplayerGameId;
    }

    /** Return the game entry from the table with given game id*/
    public Game getGame(Long gameId){
        return mGameMap.get(gameId);
    }

    /** Return the multiplayer game entry from the table with given game id*/
    public MultiplayerGame getMultiplayerGame(Long gameId){
        return mMultiplayerGameMap.get(gameId);
    }

    /** Delete the game entry*/
    public boolean deleteGame(Long gameId){
        return mGameMap.remove(gameId) != null;
    }

    /** Delete the multiplayer game entry*/
    public boolean deleteMultiplayerGame(Long gameId){
        return mMultiplayerGameMap.remove(gameId) != null;
    }

}
