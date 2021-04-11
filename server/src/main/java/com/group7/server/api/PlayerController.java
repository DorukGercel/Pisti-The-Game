package com.group7.server.api;

import com.group7.server.model.Player;
import com.group7.server.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/player")
@RestController
public class PlayerController {
    private final PlayerService playerService;

    //TODO: Implement DTOs
    @PostMapping("/add")
    public ResponseEntity<Player> addStudent(@RequestBody Player player){
        return ResponseEntity.ok().body(this.playerService.addPlayer(new Player()));
    }
}
