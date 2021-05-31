package com.group7.server.api;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.dto.authentication.*;
import com.group7.server.model.Player;
import com.group7.server.service.authentication.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Responsible for authentication requests of the players.
 * Deals with the register, login and logout requests.
 *
 */
@RequiredArgsConstructor
@RequestMapping("/api/player")
@Api(value = "Player API", tags = {"Player API"})
@RestController
public class PlayerController {

    private final PlayerService mPlayerService;

    /**
     * Handles player's register request. Utilizes PlayerService's method to deal with the request.
     *
     * @param authRequest the request which includes the necessary credentials of the player to register to the system;
     *                    username, password and email cannot be null.
     * @return the authentication response according to the success of the operation.
     *                    If operation is successful; returns success status code;
     *                                              ; error message is null.
     *                    If operation is not successful; returns fail status code and the error message.
     *
     */
    @PostMapping("/register")
    @ApiOperation(value = "Handles player's register request.")
    public AuthResponse register(@RequestBody AuthRequest authRequest){
        StatusCode statusCode = mPlayerService.register(
                new Player(
                        authRequest.getUsername(),
                        authRequest.getPassword(),
                        authRequest.getEmail())
        );

        if (statusCode.equals(StatusCode.SUCCESS)) {
            return new AuthResponse(StatusCode.SUCCESS, null);
        }
        return new AuthResponse(StatusCode.FAIL, "Register attempt failed!");
    }

    /**
     * Handles player's login request. Utilizes PlayerService's method to deal with the request.
     *
     * @param authRequest the request which includes the necessary credentials of the player to login to the system;
     *                    username, password and email cannot be null.
     * @return the authentication response according to the success of the operation.
     *                    If operation is successful; returns success status code, JWT token and session id of the user
     *                                              ; error message is null.
     *                    If operation is not successful; returns fail status code and the error message.
     */
    @PostMapping("/login")
    @ApiOperation(value = "Handles player's login request. Register required.")
    public AuthResponse login(@RequestBody AuthRequest authRequest){
        Object[] credentials = new Object[2];
        StatusCode statusCode = mPlayerService.login(
                new Player(
                        authRequest.getUsername(),
                        authRequest.getPassword(),
                        authRequest.getEmail()),
                credentials
        );

        if(statusCode.equals(StatusCode.SUCCESS)) {
            String token = (String) credentials[0];
            Long sessionId = (Long) credentials[1];

            return new LoginResponse(StatusCode.SUCCESS, null, token, sessionId);
        }
        return new AuthResponse(StatusCode.FAIL, "Login attempt failed!");
    }

    /**
     * Handles player's reset password request. Utilizes PlayerService's method to deal with the request.
     *
     * @param forgotPasswordRequest the request which includes the necessary credentials of the player to reset password;
     *                             only email is required
     *
     * @return the authentication response according to the success of the operation.
     *                          If operation is successful; returns success status code
     *                                                    ; error message is null.
     *                          If operation is not successful; returns fail status code and the error message.
     */
    @PostMapping("/forgotPassword")
    @ApiOperation(value = "Handles player's forgot password request. Register required.")
    public AuthResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        StatusCode statusCode = mPlayerService.handleForgotPassword(
                new Player(
                        null,
                        null,
                        forgotPasswordRequest.getEmail())
        );

        if (statusCode.equals(StatusCode.SUCCESS)) {
            return new AuthResponse(StatusCode.SUCCESS, null);
        }
        return new AuthResponse(StatusCode.FAIL, "Reset password attempt failed!");
    }

    /**
     * Handles player's reset password request. Utilizes PlayerService's method to deal with the request.
     *
     * @param playerId the id of the player with reset password request.
     *
     * @return the authentication response according to the success of the operation.
     *                          If operation is successful; returns success status code, new password of the user
     *                                                    ; error message is null.
     *                          If operation is not successful; returns fail status code and the error message.
     */
    @GetMapping("/resetPassword/{playerId}")
    @ApiOperation(value = "Handles player's reset password request. Register required.")
    public AuthResponse resetPassword(@PathVariable Long playerId){
        Object[] credentials = new Object[1];
        StatusCode statusCode = mPlayerService.resetPassword(
                new Player(playerId),
                credentials
        );
        if(statusCode.equals(StatusCode.SUCCESS)) {
            String password = (String) credentials[0];
            return new ResetPasswordResponse(StatusCode.SUCCESS, null, password);
        }
        return new AuthResponse(StatusCode.FAIL, "Reset password attempt failed!");
    }

    /**
     * Handles player's logout request. Utilizes PlayerService's method to deal with the request.
     *
     * @param logoutRequest the request which includes the session id of the player who sends the request.
     * @return the authentication response according to the success of the operation.
     *                    If operation is successful; returns success status code;
     *                                              ; error message is null.
     *                    If operation is not successful; returns fail status code and the error message.
     */
    @DeleteMapping("/logout")
    @ApiOperation(value = "Handles player's logout request. Login required.")
    public AuthResponse logout(@RequestBody LogoutRequest logoutRequest){
        StatusCode statusCode = mPlayerService.logout(logoutRequest.getSessionId());

        if (statusCode.equals(StatusCode.SUCCESS)) {
            return new LogoutResponse(StatusCode.SUCCESS, null);
        }
        return new AuthResponse(StatusCode.FAIL, "Logout attempt failed!");
    }
}
