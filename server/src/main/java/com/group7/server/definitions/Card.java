package com.group7.server.definitions;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/** Definition of the card in the game*/
@Data
@RequiredArgsConstructor
public class Card {

    private final Suit    mSuit;
    private final Rank    mRank;

    /** Type definition of the suit of the card*/
    public enum Suit {
        CLUBS,
        DIAMONDS,
        HEARTS,
        SPADES
    }

    /** Type definition of the rank of the card*/
    public enum Rank {
        ACE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING
    }
}
