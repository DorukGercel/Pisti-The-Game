package com.group7.server.api;

import com.group7.server.definitions.StatusCode;
import com.group7.server.dto.game.GameResponse;
import com.group7.server.dto.game.InitGameRequest;
import com.group7.server.dto.game.InitGameResponse;
import com.group7.server.service.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Responsible for game related requests of the players.
 * Deals with the creation of new game and making moves requests.
 *
 */
@RequiredArgsConstructor
@RequestMapping("/api/game")
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
    public GameResponse startGame(@RequestBody InitGameRequest initGameRequest){
        Long[] gameId = new Long[1];
        StatusCode statusCode = mGameService.initGame(initGameRequest.getSessionId(),gameId);
        if(statusCode.equals(StatusCode.SUCCESS)) {
            return new InitGameResponse(statusCode, null, gameId[0]);
        }
        return new GameResponse(statusCode, "New game creation failed!");
    }
}
