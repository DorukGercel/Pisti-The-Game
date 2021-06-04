package com.group7.server.api;

import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameEnvironment;
import com.group7.server.definitions.common.StatusCode;
import com.group7.server.dto.game.*;
import com.group7.server.service.game.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for game related requests of the players.
 * Deals with the creation of new game and making moves requests.
 *
 */
@RequiredArgsConstructor
@RequestMapping("/api/game")
@Api(value = "Game API", tags = {"Game API"})
@RestController
public class GameController {

    private final GameService mGameService;

    /**
     * Handles creation of a new game request from the player. Utilizes GameService's method to deal with the request.
     *
     * @param initGameRequest the request which includes the necessary information of the active player to start a new game.
     *                        sessionId of the active player is necessary.
     *
     * @return  the game response according to the success of the operation.
     *                      If operation is successful; returns success status code and game id
     *                                                ; error message is null.
     *                      If operation is not successful; returns fail status code and the error message.
     */
    @PutMapping("/startGame")
    @ApiOperation(value = "Creates a new game and connects the player to the game. Login required.")
    public GameResponse startGame(@RequestBody InitGameRequest initGameRequest){
        Long[] gameId = new Long[1];
        StatusCode statusCode = mGameService.initGame(initGameRequest.getSessionId(),gameId);
        if(statusCode.equals(StatusCode.SUCCESS)) {
            return new InitGameResponse(statusCode, null, gameId[0]);
        }
        return new GameResponse(statusCode, "New game creation failed!");
    }

    @PutMapping("/interactGame")
    @ApiOperation(value = "Player interacts with the game. The player can perform initial, card or redeal movements. Login required.")
    public GameResponse interactGame(@RequestBody InteractRequest interactRequest){
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        List<Object> gameStatus = new ArrayList<>();

        System.out.println(interactRequest);

        StatusCode statusCode = mGameService.interactGame(interactRequest.getSessionId(),
                interactRequest.getGameId(),
                interactRequest.getCardNo(),
                decodeMoveType(interactRequest.getMoveType()),
                decodeGameStatusCode(interactRequest.getGameStatusCode()),
                gameEnvironmentList,
                gameStatus);
        if(statusCode.equals(StatusCode.SUCCESS)){
            if (!isGameLevelSwitching(getGameStatusCode(gameStatus))) {
                // Return response after normal game operation
                System.out.println(new InteractResponse(statusCode, null, gameEnvironmentList.get(0), gameEnvironmentList.get(1), (Game.GameStatusCode) gameStatus.get(0)));
                return new InteractResponse(statusCode, null, gameEnvironmentList.get(0), gameEnvironmentList.get(1), (Game.GameStatusCode) gameStatus.get(0));
            } else {
                // Return response after level up
                System.out.println(new InteractResponse(statusCode, null, null, null, (Game.GameStatusCode) gameStatus.get(0)));
                return new InteractResponse(statusCode, null, null, null, (Game.GameStatusCode) gameStatus.get(0));
            }
        }
        System.out.println(new GameResponse(statusCode, "Interaction with the game failed!"));
        return new GameResponse(statusCode, "Interaction with the game failed!");
    }

    /**
     * Removes the game.
     *
     * @param sessionId of the active player.
     * @param gameId of the game.
     *
     * @return the game response according to the success of the operation.
     */
    @DeleteMapping("/removeGame/{sessionId}/{gameId}")
    @ApiOperation(value = "Remove the game.")
    public GameResponse removeGame(@PathVariable Long sessionId, @PathVariable Long gameId) {
        if(mGameService.removeGame(sessionId, gameId).equals(StatusCode.SUCCESS)) {
            return new GameResponse(StatusCode.SUCCESS, null);
        }
        return new GameResponse(StatusCode.FAIL, "Remove game creation failed!");
    }

    /** Helper function to decode string to enum*/
    private Game.MoveType decodeMoveType(String moveTypeStr) {
        if(moveTypeStr.equals("INITIAL")){
            return Game.MoveType.INITIAL;
        } else if(moveTypeStr.equals("CARD")){
            return Game.MoveType.CARD;
        } else if(moveTypeStr.equals("REDEAL")){
            return Game.MoveType.REDEAL;
        }
        return null;
    }

    /** Helper function to convert game status from string*/
    private Game.GameStatusCode decodeGameStatusCode(String gameStatusCodeStr) {
        if (gameStatusCodeStr.equals("NORMAL")) {
            return Game.GameStatusCode.NORMAL;
        } else if (gameStatusCodeStr.equals("LEVEL_UP")) {
            return Game.GameStatusCode.LEVEL_UP;
        } else if (gameStatusCodeStr.equals("CHEAT_LEVEL_UP")) {
            return Game.GameStatusCode.CHEAT_LEVEL_UP;
        }
        return null;
    }

    /** Helper function to check if game is level switching or game over*/
    private boolean isGameLevelSwitching(Game.GameStatusCode gameStatusCode) {
        return gameStatusCode.equals(Game.GameStatusCode.LEVEL_UP) || gameStatusCode.equals(Game.GameStatusCode.CHEAT_LEVEL_UP) || gameStatusCode.equals(Game.GameStatusCode.GAME_OVER_WIN);
    }

    /** Helper function to get game status code from the game status*/
    private Game.GameStatusCode getGameStatusCode(List<Object> gameStatus) {
        return (Game.GameStatusCode) gameStatus.get(0);
    }
}
