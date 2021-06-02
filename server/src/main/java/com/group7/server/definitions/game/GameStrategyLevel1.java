package com.group7.server.definitions.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy of the game according to level 1 difficulty
 */
public class GameStrategyLevel1 extends GameStrategyBase {

    @Override
    public List<GameEnvironment> simulateGame(Short cardNo, Game.MoveType moveType) {
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();

        // Simulate player movement and create game environment
        gameEnvironmentList.add(
                mGame.createPlayerEnvironment(simulateMovement(cardNo, mGame.getMTurn()), isGameFinished(Game.Side.PLAYER), Game.GameStatus.NORMAL,Game.MoveType.CARD)
        );
        // Simulate pc movement and create game environment
        gameEnvironmentList.add(
                mGame.createPcEnvironment(simulateMovement(pcDecideCard(), mGame.getMTurn()), isGameFinished(Game.Side.PC), Game.GameStatus.NORMAL,Game.MoveType.CARD)
        );

        return gameEnvironmentList;
    }

    /** The method that evaluates the moves of the player or the pc*/
    private boolean simulateMovement(Short cardNo, Game.Side side) {
        boolean isPisti = false;
        List<Short> middleDeck = mGame.getMiddleDeck();
        // Player's or PC' deck
        List<Short> currentPlayerDeck = mGame.getDeck(side);
        // Remove the card from players deck
        currentPlayerDeck.remove(cardNo);
        // Check if middle is empty or not
        if(middleDeck.size() > 0) {
            GameConfig.Card currentPlayerCard = mGame.getMCardTable().getCard(cardNo);
            if (isMatchedCard(currentPlayerCard)){
                // Increment score of player and set last win
                isPisti = incrementScore(currentPlayerCard, side, false);
                mGame.setMLastWin(side);
            } else {
                // Add player card to top of middle
                middleDeck.add(cardNo);
            }
        } else {
            // Add player card to top of middle
            middleDeck.add(cardNo);
        }
        // Change turn
        mGame.invertTurn();
        return isPisti;
    }

    private Short pcDecideCard() {
        List<Short> pcDeck = mGame.getDeck(Game.Side.PC);
        for (Short cardNo : pcDeck) {
            if(isMatchedCard(mGame.getMCardTable().getCard(cardNo))) {
                return cardNo;
            }
        }
        return mGame.getTopCardNo(pcDeck);
    }
}
