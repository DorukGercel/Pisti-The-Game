package com.group7.server.api;

import com.group7.server.dto.*;
import com.group7.server.definitions.StatusCode;
import com.group7.server.model.Player;
import com.group7.server.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/player")
@RestController
public class PlayerController {

    private final PlayerService mPlayerService;

    //TODO: Implement DTOs
    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest authRequest){
        StatusCode statusCode = mPlayerService.register(new Player(authRequest.getUsername(), authRequest.getPassword(), authRequest.getEmail()));

        if (statusCode.equals(StatusCode.SUCCESS)) {
            return new AuthResponse(StatusCode.SUCCESS, null);
        }
        return new AuthResponse(StatusCode.FAIL, "Register attempt failed!");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest){
        Object[] credentials = new Object[2];
        StatusCode statusCode = mPlayerService.login(new Player(authRequest.getUsername(), authRequest.getPassword(), authRequest.getEmail()), credentials);

        if(statusCode.equals(StatusCode.SUCCESS)) {
            String token = (String) credentials[0];
            Long sessionId = (Long) credentials[1];
            return new LoginResponse(StatusCode.SUCCESS, null, token, sessionId);
        }
        return new AuthResponse(StatusCode.FAIL, "Login attempt failed!");
    }

    @DeleteMapping("/logout")
    public AuthResponse logout(@RequestBody DeleteRequest deleteRequest){
        StatusCode statusCode = mPlayerService.logout(deleteRequest.getSessionId());

        if (statusCode.equals(StatusCode.SUCCESS)) {
            return new DeleteResponse(StatusCode.SUCCESS, null);
        }
        return new AuthResponse(StatusCode.FAIL, "Logout attempt failed!");
    }
}
