package com.group7.server.service;

import com.group7.server.model.ActivePlayer;
import com.group7.server.model.Player;
import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository mPlayerRepository;
    private final ActivePlayerRepository mActivePlayerRepository;

    @Override
    public Player register(Player player) {
        return mPlayerRepository.save(player);
    }

    @Override
    public ActivePlayer login(Player player) {
        ActivePlayer activePlayer = initializeActivePlayer(player);
        if(activePlayer != null) {
            return mActivePlayerRepository.save(activePlayer);
        }
        return null;
    }

    private ActivePlayer initializeActivePlayer(Player player) {
        Optional<Player> repoPlayer = mPlayerRepository.findPlayerById(player.getId());
        if(repoPlayer.isPresent()) {
            return new ActivePlayer(repoPlayer.get());
        }
        return null;
    }
}
