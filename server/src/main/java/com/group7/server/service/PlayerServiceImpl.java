package com.group7.server.service;

import com.group7.server.definitions.StatusCode;
import com.group7.server.model.ActivePlayer;
import com.group7.server.model.Player;
import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    private final AuthenticationService mAuthenticationService;
    private final PlayerRepository mPlayerRepository;
    private final ActivePlayerRepository mActivePlayerRepository;

    @Override
    public StatusCode register(Player player) {
        try {
            mAuthenticationService.register(player);
            return StatusCode.SUCCESS;
        }
        catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    @Override
    public StatusCode login(Player player, Object[] credentials) {
        try {
            String token;
            Long sessionId;
            if((token = mAuthenticationService.authenticate(player)) != null && (sessionId = initializeActivePlayer(player)) != null) {
                credentials[0] = token;
                credentials[1] = sessionId;
                return StatusCode.SUCCESS;
            }
            return StatusCode.FAIL;
        }
        catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    @Override
    public StatusCode logout(Long id) {
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(id);
            System.out.println(dbActivePlayer.get());
            mActivePlayerRepository.deleteById(id);
            return StatusCode.SUCCESS;
        }
        catch (Exception e) {
            return StatusCode.FAIL;
        }

    }

    private Long initializeActivePlayer(Player player) {
        Optional<Player> dbPlayer = mPlayerRepository.findByUsername(player.getUsername());
        if (dbPlayer.isPresent()) {
            System.out.println(dbPlayer.get());
            ActivePlayer activePlayer = mActivePlayerRepository.save(new ActivePlayer(dbPlayer.get()));
            return activePlayer.getId();
        }
        return null;
    }
}
