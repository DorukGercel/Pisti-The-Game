package com.group7.server.service;

import com.group7.server.model.ActivePlayer;
import com.group7.server.model.Player;

public interface PlayerService {
    Player register(Player player);
    ActivePlayer login(Player player);
}
