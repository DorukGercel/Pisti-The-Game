package com.group7.client.definitions.player;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Responsible for holding active player information
 * */
@Data
@Component
public class PlayerManager {
    /** Session id of the active player*/
    private Long sessionId;
}
