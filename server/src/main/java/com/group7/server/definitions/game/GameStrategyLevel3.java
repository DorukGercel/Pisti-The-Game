package com.group7.server.definitions.game;

import java.util.*;

/**
 * Strategy of the game according to level 3 (Bluffing Pisti) difficulty
 */
public class GameStrategyLevel3 extends GameStrategyBase {
    /** Double possibility value for randomness*/
    private final double RANDOM_POSSIBILITY = 0.6;
    /** Indicates the first move of PC to apply a different strategy at the beginning*/
    private Boolean isFirstPcMove = true;

    @Override
    public List<GameEnvironment> simulateGame(Short cardNo, Game.MoveType moveType) {
        try {
            List<GameEnvironment> gameEnvironmentList = new ArrayList<>();

            // Simulate player movement and create game environment
            gameEnvironmentList.add(
                    playerPerformMove(cardNo, moveType)
            );

            // Simulate pc movement and create game environment
            gameEnvironmentList.add(
                    pcStrategicPerformMove(moveType)
            );

            return gameEnvironmentList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The method that evaluates the moves of the player or the pc
     *
     * @param cardNo of the card move
     * @param side who made the move
     * @param moveType choice of move
     * @return pisti achieved or not
     */
    private boolean simulateMovement(Short cardNo, Game.Side side, Game.MoveType moveType) {
        boolean isPisti = false;
        // TODO: Remove print
        System.out.println("Side: " + side + " Card: " + cardNo);
        if(!(Game.MoveType.isChallengeRelatedMove(moveType) || Game.MoveType.isPassMove(moveType))) {
            // Get middle deck
            List<Short> middleDeck = mGame.getMiddleDeck();
            // Player's or PC's deck
            List<Short> currentPlayerDeck = mGame.getDeck(side);
            // Remove the card from player's deck
            currentPlayerDeck.remove(cardNo);
            // Get the current card from its card no
            GameConfig.Card currentPlayerCard = mGame.getMCardTable().getCard(cardNo);

            if (moveType.equals(Game.MoveType.CARD)) {
                // Check if middle is empty or not
                if (middleDeck.size() > 0) {
                    if (isMatchedCard(currentPlayerCard)) {
                        // Increment score of player and set last win
                        isPisti = incrementScore(currentPlayerCard, side);
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
            } else if (moveType.equals(Game.MoveType.BLUFF)) {
                // Add player card to top of middle, this card won't be visible
                middleDeck.add(cardNo);
            }
        } // else pass move

        // Change turn
        mGame.invertTurn();
        return isPisti;

    }

    /**
     * Helper function to perform challenge related movements
     *
     * @param side who challenged or not challenged
     * @param moveType choice of move; challenge, not challenge
     * @param successOfChallenge result of the challenge; challenge_success, challenge_fail, not_challenge;
     *                          parameter is pre memory allocated
     * @return pisti achieved or not
     */
    private boolean simulateChallengeMovement(Game.Side side, Game.MoveType moveType, Game.MoveType[] successOfChallenge) {
        boolean isPisti = false;
        //Extract the challenge cards
        List<GameConfig.Card> challengeCards = mGame.getChallengeCards();
        GameConfig.Card bluffedCard = challengeCards.get(0);
        GameConfig.Card bluffingCard = challengeCards.get(1);
        if (moveType.equals(Game.MoveType.CHALLENGE)) {
            // opponent does not believe and challenge the player
            if (isChallengeSuccess(bluffedCard, bluffingCard)) {
                // opponent who challenged the player gets points, bluffer lying
                successOfChallenge[0] = Game.MoveType.CHALLENGE_SUCCESS;
                isPisti = incrementBluffingScore(side, bluffedCard, Game.MoveType.CHALLENGE, false);
            } else {
                // player who bluffed gets points, bluffer not lying
                successOfChallenge[0] = Game.MoveType.CHALLENGE_FAIL;
                isPisti = incrementBluffingScore(mGame.getOtherSide(side), bluffedCard, Game.MoveType.CHALLENGE, true);
            }
        } else if (moveType.equals(Game.MoveType.NOT_CHALLENGE)) { // Game.MoveType.NOT_CHALLENGE
            // Whatever the card is, it should be counted as Pisti because not challenged
            // if isDirectPisti, incrementScore does not compare ranks, directly assumes it is a Pisti
            successOfChallenge[0] = Game.MoveType.NOT_CHALLENGE;
            isPisti = incrementBluffingScore(mGame.getOtherSide(side), bluffedCard, Game.MoveType.NOT_CHALLENGE, true);
            mGame.setMLastWin(side);
        }

        // Change turn
        mGame.invertTurn();
        return isPisti;
    }

    /** Helper function to create game environment according to player's move*/
    private GameEnvironment playerPerformMove(Short cardNo, Game.MoveType playerMoveType) {
        if(Game.MoveType.isChallengeRelatedMove(playerMoveType)) {
            // If move was a challenge move, return with the success of the challenge
            Game.MoveType[] successOfChallenge = new Game.MoveType[1];
            return mGame.createPlayerEnvironment(
                    simulateChallengeMovement(mGame.getMTurn(), playerMoveType, successOfChallenge), successOfChallenge[0]
            );
        } else {
            // If not challenge move return a basic move environment
            return mGame.createPlayerEnvironment(
                    simulateMovement(cardNo, mGame.getMTurn(), playerMoveType), playerMoveType
            );
        }
    }

    /** Helper function to decide the next move of pc; attention it takes the players move as param to challenge or not challenge*/
    private GameEnvironment pcStrategicPerformMove(Game.MoveType playerMoveType) {
        if (Game.MoveType.isChallengeRelatedMove(playerMoveType)) {
            // If player made a challenge move; make a pass move
            // TODO: Remove print
            System.out.println("Pc Move: " + "PASS");
            return mGame.createPcEnvironment(
                    simulateMovement((short) -1, mGame.getMTurn(), Game.MoveType.PASS), Game.MoveType.PASS);
        } else if(!playerMoveType.equals(Game.MoveType.BLUFF)) {
            // Player didn't bluff; so no need to challenge
            if(mGame.getMiddleDeck().size() == 1 && Math.random() < RANDOM_POSSIBILITY) {
                // Decide to bluff; according to randomness
                // TODO: Remove print
                System.out.println("Pc Move: " + "BLUFF");
                return mGame.createPcEnvironment(
                        simulateMovement(pcStrategicDecideCard(), mGame.getMTurn(), Game.MoveType.BLUFF), Game.MoveType.BLUFF);
            }
            // TODO: Remove print
            System.out.println("Pc Move: " + "CARD");
            return mGame.createPcEnvironment(
                    simulateMovement(pcStrategicDecideCard(), mGame.getMTurn(), Game.MoveType.CARD), Game.MoveType.CARD);
        } else {
            // If player bluffed, choose to accept challenge or don't challenge
            // If move was a challenge move, return with the success of the challenge
            Game.MoveType[] successOfChallenge = new Game.MoveType[1];
            // Decide according to randomness
            if(Math.random() < RANDOM_POSSIBILITY) {
                // TODO: Remove print
                System.out.println("Pc Move: " + "CHALLENGE");
                return mGame.createPcEnvironment(
                        simulateChallengeMovement(mGame.getMTurn(), Game.MoveType.CHALLENGE, successOfChallenge), successOfChallenge[0]);
            }
            // TODO: Remove print
            System.out.println("Pc Move: " + "NOT CHALLENGE");
            return mGame.createPcEnvironment(
                    simulateChallengeMovement(mGame.getMTurn(), Game.MoveType.NOT_CHALLENGE, successOfChallenge), successOfChallenge[0]);
        }
    }

    private Short pcStrategicDecideCard() {
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
            // If there is no card with more than 1 frequency, return the top card of the deck
        } else {
            // Check the played cards
            List<Integer> playedCardsTmp = new ArrayList<>(mGame.getMPlayedCards());
            Integer mostFrequentRankCount;
            int mostFrequentRankIndex;
            while(true) {
                mostFrequentRankCount = Collections.max(playedCardsTmp);
                mostFrequentRankIndex = playedCardsTmp.indexOf(mostFrequentRankCount);
                if(mGame.isRankInDeck(mostFrequentRankIndex, pcDeck)) {
                    for (Short cardNo : pcDeck) {
                        if (mGame.getRankOfCard(cardNo) == mostFrequentRankIndex) {
                            mGame.addCardToPlayedCards(cardNo);
                            return cardNo;
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

    /** Helper function to decide if the challenge is successful.*/
    private boolean isChallengeSuccess(GameConfig.Card bluffedCard, GameConfig.Card bluffingCard) {
        // True if the player card is NOT jack or the ranks of the cards match
        // Challenge true when there is no match!!!
        return !(bluffingCard.getMRank().equals(GameConfig.Card.Rank.JACK) || bluffingCard.getMRank().equals(bluffedCard.getMRank()));
    }
}
