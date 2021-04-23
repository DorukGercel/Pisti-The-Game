package com.group7.server.service;

import com.group7.server.model.Player;

public interface AuthenticationService {
    String register(final Player player);
    String authenticate(final Player player);
}
