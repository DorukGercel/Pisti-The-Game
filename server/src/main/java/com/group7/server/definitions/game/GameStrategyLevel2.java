package com.group7.server.definitions.game;

import java.util.*;

/**
 * Strategy of the game according to level 2 difficulty
 */
public class GameStrategyLevel2 extends GameStrategyBase {
    /**Indicates the first move of PC to apply a different strategy at the beginning*/
    private Boolean isFirstPcMove = true;

    @Override
    public List<GameEnvironment> simulateGame(Short cardNo, Game.MoveType moveType) {
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();

        // Simulate player movement and create game environment
        gameEnvironmentList.add(
            mGame.createPlayerEnvironment(
                    simulateMovement(cardNo, mGame.getMTurn()), moveType)
        );

        // Simulate pc movement and create game environment
        gameEnvironmentList.add(
                mGame.createPcEnvironment(
                        simulateMovement(pcStrategicDecideCard(isFirstPcMove), mGame.getMTurn()), moveType)
        );

        return gameEnvironmentList;
    }

    /** The method that evaluates the moves of the player or the pc*/
    private boolean simulateMovement(Short cardNo, Game.Side side) {
        boolean isPisti = false;
        List<Short> middleDeck = mGame.getMiddleDeck();
        // Player's or PC's deck
        List<Short> currentPlayerDeck = mGame.getDeck(side);
        // Remove the card from player's deck
        currentPlayerDeck.remove(cardNo);
        // Check if middle is empty or not
        if(middleDeck.size() > 0) {
            GameConfig.Card currentPlayerCard = mGame.getMCardTable().getCard(cardNo);
            if(isMatchedCard(currentPlayerCard)) {
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
        // move simulated, the card was played
        mGame.addCardToPlayedCards(cardNo);
        // Change turn
        mGame.invertTurn();
        return isPisti;
    }

    private Short pcStrategicDecideCard(boolean isFirstPcMove) {
        List<Short> pcDeck = mGame.getDeck(Game.Side.PC);
        // If it is a match, no need to apply a strategy
        for(Short cardNo : pcDeck) {
            if(isMatchedCard(mGame.getMCardTable().getCard(cardNo))) {
                mGame.addCardToPlayedCards(cardNo);
                return cardNo;
            }
        }
        // Apply a strategy
        if(isFirstPcMove) {
            // There are no played cards
            // If PC does not have the card same as the one on the top of the table,
            // it should play the card with more than one frequency if exists.
            // Because the chance of the same card existing in the opponent's card is lower
            Set<Short> pcDeckSet = new HashSet<>(pcDeck);
            HashMap<Short, Short> cardFrequencies = new HashMap<Short, Short>();
            for(Short cardNo: pcDeckSet) {
                cardFrequencies.put(cardNo, (short) Collections.frequency(pcDeck, cardNo));
            }
            for (Map.Entry card : cardFrequencies.entrySet()) {
                Short freq = (Short) card.getValue(); // value is frequency of the card
                this.isFirstPcMove = false;
                if(freq >= 2) {
                    mGame.addCardToPlayedCards((Short) card.getKey());
                    return (Short) card.getKey(); // key is card no
                }

            }
        } else {
            // Check the played cards
            // we should play most frequently used card, to lower the opponent's pisti chance
            List<Integer> playedCardsTmp = new ArrayList<>(mGame.getMPlayedCards());
            Integer mostFrequentRankCount;
            int mostFrequentRankIndex;
            while(true) {
                mostFrequentRankCount = Collections.max(playedCardsTmp);
                mostFrequentRankIndex = playedCardsTmp.indexOf(mostFrequentRankCount);
                if(mGame.isRankInDeck(mostFrequentRankIndex, pcDeck)) {
                    for(int i = 0; i < pcDeck.size(); i++) {
                        if(mGame.getRankOfCard(pcDeck.get(i)) == mostFrequentRankIndex) {
                            mGame.addCardToPlayedCards(pcDeck.get(i));
                            return pcDeck.get(i);
                        }
                    }
                } else {
                    // Set the rank with current max frequency to -1
                    // Look other ranks that can exist in the deck
                    playedCardsTmp.set(mostFrequentRankIndex, -1);

                }
            }
        }
        mGame.addCardToPlayedCards(mGame.getTopCardNo(pcDeck));
        return mGame.getTopCardNo(pcDeck);
    }

}
