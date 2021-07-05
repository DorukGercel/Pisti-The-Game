package com.group7.server.definitions.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy of the game according to multiplayer level (for simulation)
 */
public class GameStrategyMultiplayer extends GameStrategyBase{
    @Override
    public List<GameEnvironment> simulateGame(Short cardNo, Game.MoveType moveType) {
        try {
            List<GameEnvironment> gameEnvironmentList = new ArrayList<>();

            // Simulate player movement and create game environment
            gameEnvironmentList.add(
                    playerPerformMove(cardNo, moveType)
            );

            return gameEnvironmentList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Simulate initial movement*/
    @Override
    protected List<GameEnvironment> simulateInitial() {
        // Move type of initialization
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        gameEnvironmentList.add(
                mGame.createMultiplayerEnvironment(mGame.getMTurn(), false, true, Game.MoveType.INITIAL)
        );
        return gameEnvironmentList;
    }

    /** Simulate redeal or restart*/
    @Override
    protected List<GameEnvironment> simulateRedeal() {
        // Move type of redeal and restart
        List<Short> mainDeck = mGame.getMainDeck();
        Game.MoveType sentMoveType;
        if(mainDeck.size() > 0) {
            // Redeal in a round
            mGame.dealCards();
            sentMoveType = Game.MoveType.REDEAL;
        } else {
            // Restart with another level
            //TODO: Remove print
            System.out.println("Restart");
            incrementScore(null, mGame.getMLastWin());
            mGame.initCards();
            sentMoveType = Game.MoveType.RESTART;
        }
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        gameEnvironmentList.add(
                mGame.createMultiplayerEnvironment(mGame.getMTurn(), false, true, sentMoveType)
        );
        return gameEnvironmentList;
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
            return mGame.createMultiplayerEnvironment(mGame.getMTurn(),
                    simulateChallengeMovement(mGame.getMTurn(), playerMoveType, successOfChallenge),
                    false,
                    successOfChallenge[0]
            );
        } else {
            // If not challenge move return a basic move environment
            return mGame.createMultiplayerEnvironment(mGame.getMTurn(),
                    simulateMovement(cardNo, mGame.getMTurn(), playerMoveType),
                    false,
                    playerMoveType
            );
        }
    }

    /** Helper function to decide if the challenge is successful.*/
    private boolean isChallengeSuccess(GameConfig.Card bluffedCard, GameConfig.Card bluffingCard) {
        // True if the player card is NOT jack or the ranks of the cards match
        // Challenge true when there is no match!!!
        return !(bluffingCard.getMRank().equals(GameConfig.Card.Rank.JACK) || bluffingCard.getMRank().equals(bluffedCard.getMRank()));
    }
}
