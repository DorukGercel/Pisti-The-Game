package com.group7.server.definitions.game;

import java.util.List;

/**
 * Current strategy of the game according to level
 * Responsible for controlling states of the game
 */
public abstract class GameStrategyBase implements GameStrategy {
    /** Game strategy is bound to*/
    protected Game mGame;

    /** Called by the game to register strategy*/
    public void registerGame(Game game) {
        this.mGame = game;
    }

    /** Interact with the game according to move type*/
    @Override
    public List<GameEnvironment> interact(Game.MoveType moveType, Short cardNo) {
        if (moveType.equals(Game.MoveType.INITIAL)) {
            // Move type of initialization
            return simulateInitial();
        } else if (Game.MoveType.isSimulateMoveType(moveType)) {
            // Move type of card, bluff, challenge, not challenge
            // TODO: Remove print
            System.out.println("Player Move: " + moveType);
            // TODO: Implement game over
            return simulateGame(cardNo, moveType);
        } else {
            return simulateRedeal();
        }
    }

    /** Simulate game according to the strategy*/
    abstract public List<GameEnvironment> simulateGame(Short cardNo, Game.MoveType moveType);

    /** Simulate initial movement*/
    protected List<GameEnvironment> simulateInitial() {
        // Move type of initialization
        return mGame.createEnvironment(mGame.createPlayerEnvironment(false, Game.MoveType.INITIAL),
                mGame.createPcEnvironment(false, Game.MoveType.INITIAL)
        );
    }

    /** Simulate redeal or restart*/
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
        return mGame.createEnvironment(mGame.createPlayerEnvironment(false, sentMoveType),
                mGame.createPcEnvironment(false, sentMoveType)
        );
    }

    /** Helper function to decide if there is a takeover.*/
    protected boolean isMatchedCard(GameConfig.Card playerCard) {
        //Extract the face up card if middle is not empty
        List<Short> middleDeck = mGame.getMiddleDeck();
        if(middleDeck.isEmpty()){
            return false;
        }
        GameConfig.Card faceUpCard = mGame.getTopCard(middleDeck);

        // True if the player card is jack or the ranks of the cards match
        return (playerCard.getMRank().equals(GameConfig.Card.Rank.JACK) || playerCard.getMRank().equals(faceUpCard.getMRank()));
    }

    /**
     * Helper function to increment the score of the side who achieved takeover.
     *
     * @param playerCard is the card played by player or pc
     * @param side the player or pc to gain points
     * @return if there is pisti
     */
    // TODO: Q: When pisti achieved does user count the values of the cards? No in this implementation.
    protected boolean incrementScore(GameConfig.Card playerCard, Game.Side side) {
        boolean isPisti = false;
        short pointsReceived = 0;
        short cardsReceived = 0;

        List<Short> middleDeck = mGame.getMiddleDeck();
        GameConfig.Card faceUpCard = mGame.getTopCard(middleDeck);

        // Decide the takeover type
        TakeoverType takeoverType = GameStrategyBase.TakeoverType.getTakeoverType(faceUpCard, middleDeck.size());

        if(playerCard != null) {
            // May be last points so check nullity of player card
            if (takeoverType.equals(TakeoverType.PISTI) || takeoverType.equals(TakeoverType.DOUBLE_PISTI)) {
                // Take takeover special points by player card and top middle cards
                short takeoverPoint = SpecialPoint.takeTakeoverPoint(takeoverType);
                mGame.removeTopCard(middleDeck);
                pointsReceived = (short) (pointsReceived + takeoverPoint);
                cardsReceived = (short) (cardsReceived + 2);
                isPisti = true;
            } else {
                // Take special points of the player card and top middle cards
                short playerCardPoint = SpecialPoint.takeCardPoint(playerCard);
                short faceUpCardPoint = SpecialPoint.takeCardPoint(faceUpCard);
                mGame.removeTopCard(middleDeck);
                pointsReceived = (short) (pointsReceived + playerCardPoint + faceUpCardPoint);
                cardsReceived = (short) (cardsReceived + 2);
            }
        }

        // Count the points of the cards received
        for(Short cardNo : middleDeck) {
            GameConfig.Card middleCard = mGame.getMCardTable().getCard(cardNo);
            short middleCardPoint = SpecialPoint.takeCardPoint(middleCard);
            pointsReceived = (short) (pointsReceived + middleCardPoint);
            cardsReceived = (short) (cardsReceived + 1);
        }
        // Flush the middle deck
        middleDeck.clear();

        // Set the scores (point, card received) of the player (decide as who made takeover)
        List<Short> scores = mGame.getScores(side);
        scores.set(0, (short) (scores.get(0) + pointsReceived));
        scores.set(1, (short) (scores.get(1) + cardsReceived));

        return isPisti;
    }

    /**
     * Helper function to increment the score of the side who won the bluff or challenge.
     *
     * @param pointReceivingSide the side to recieve points
     * @param bluffedCard the card which is bluffed
     * @param challengeMoveType the move type; challenge, not challenge
     * @param isPistiMove whether the cards in the middle shall be removed or not
     * @return if there is pisti; return isPistiMove
     */
    protected boolean incrementBluffingScore(Game.Side pointReceivingSide, GameConfig.Card bluffedCard, Game.MoveType challengeMoveType, boolean isPistiMove) {
        // Get the scores (point, card received) of the player (decide as who made takeover)
        List<Short> scores = mGame.getScores(pointReceivingSide);
        if(isPistiMove) {
            // If pisti move, clear middle deck
            List<Short> middleDeck = mGame.getMiddleDeck();
            middleDeck.clear();
            // Add two cards to the recieving side
            scores.set(1, (short) (scores.get(1) + 2));
        }
        // Decide the takeover type
        TakeoverType takeoverType = GameStrategyBase.TakeoverType.getBluffingTakeoverType(bluffedCard, challengeMoveType);
        // Set the scores (point, card received) of the player (decide as who made takeover)
        scores.set(0, (short) (scores.get(0) + SpecialPoint.takeTakeoverPoint(takeoverType)));
        return isPistiMove;
    }

    /** Helper function to decide whether level is finished and set game status*/
    protected void handleLevelFinished(Game.Side side) {
        if (mGame.getScores(side).get(0) >= Game.WIN_SCORE) {
            if (side.equals(Game.Side.PLAYER)) {
                // Set the game status
                mGame.setMGameStatusCode(Game.GameStatusCode.WIN);
                // Set the level x score
                // TODO: why we subtract?
                mGame.setMLevelXScore((short) (mGame.getScores(Game.Side.PLAYER).get(0) - mGame.getScores(Game.Side.PC).get(0)));
            } else {
                mGame.setMGameStatusCode(Game.GameStatusCode.LOST);
                // TODO: What shell we do about levelx score in lose
            }
        }
    }

    // TODO: Implement get game status
    protected Game.GameStatusCode getGameStatus() {
        /*if ()
        mGame.getScores().get(0) >= (short) 151;*/
        return null;
    }

    /** Type definition of how player took the cards*/
    private enum TakeoverType {
        DOUBLE_PISTI,
        BLUFFING_DOUBLE_PISTI,
        PISTI,
        BLUFFING_PISTI,
        REGULAR;

        public static TakeoverType getTakeoverType(GameConfig.Card faceUpCard, int noMiddleCards) {
            if (noMiddleCards == 1 && faceUpCard.getMRank().equals(GameConfig.Card.Rank.JACK)) {
                return DOUBLE_PISTI;
            } else if (noMiddleCards == 1) { //TODO: If Jack is it still Pisti
                return PISTI;
            } else {
                return REGULAR;
            }
        }

        public static TakeoverType getBluffingTakeoverType(GameConfig.Card blueffedCard, Game.MoveType challengeMoveType) {
            if (blueffedCard.getMRank().equals(GameConfig.Card.Rank.JACK) && challengeMoveType.equals(Game.MoveType.NOT_CHALLENGE)) {
                // Bluffed card is jack but not challenged
                return DOUBLE_PISTI;
            } else if (blueffedCard.getMRank().equals(GameConfig.Card.Rank.JACK) && !challengeMoveType.equals(Game.MoveType.NOT_CHALLENGE)) {
                // Bluffed card is jack and challenged
                return BLUFFING_DOUBLE_PISTI;
            } else if (challengeMoveType.equals(Game.MoveType.NOT_CHALLENGE)) {
                // Not challenged
                return PISTI;
            } else {
                // Challenged
                return BLUFFING_PISTI;
            }
        }
    }

    /** Special points in the game*/
    private enum SpecialPoint {
        DOUBLE_PISTI((short) 20),
        BLUFFING_DOUBLE_PISTI((short) 40),
        PISTI((short) 10),
        BLUFFING_PISTI((short) 20),
        DIAMOND_TEN((short) 3),
        CLUB_TWO((short) 2),
        ACE((short) 1),
        JACK((short) 1);

        private final Short point;

        public Short getPoint() {
            return this.point;
        }

        public static Short takeTakeoverPoint(TakeoverType type) {
            if (type.equals(TakeoverType.DOUBLE_PISTI)) {
                return DOUBLE_PISTI.getPoint();
            } else if (type.equals(TakeoverType.PISTI)) {
                return PISTI.getPoint();
            } else if (type.equals(TakeoverType.BLUFFING_DOUBLE_PISTI)) {
                return BLUFFING_DOUBLE_PISTI.getPoint();
            } else if (type.equals(TakeoverType.BLUFFING_PISTI)) {
                return BLUFFING_PISTI.getPoint();
            }
            return 0;
        }

        public static Short takeCardPoint(GameConfig.Card card){
            if (card.getMRank().equals(GameConfig.Card.Rank.ACE)) {
                return ACE.getPoint();
            } else if (card.getMRank().equals(GameConfig.Card.Rank.JACK)) {
                return JACK.getPoint();
            } else if (card.getMSuit().equals(GameConfig.Card.Suit.CLUBS) && card.getMRank().equals(GameConfig.Card.Rank.TWO)) {
                return CLUB_TWO.getPoint();
            } else if (card.getMSuit().equals(GameConfig.Card.Suit.DIAMONDS) && card.getMRank().equals(GameConfig.Card.Rank.TEN)) {
                return DIAMOND_TEN.getPoint();
            }
            return 0;
        }

        SpecialPoint(Short point) {
            this.point = point;
        }
    }
}
