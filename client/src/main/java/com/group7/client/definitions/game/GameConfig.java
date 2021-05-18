package com.group7.client.definitions.game;

import com.group7.client.definitions.image.ImageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/** Configuration class of the game related components*/
@Configuration
public class GameConfig {

    /** Definition of the card in the game*/
    @Data
    public static class Card {
        private Short cardNo;
        @FXML private Rectangle card;

        public Card(Short cardNo, String cardUrl, Image cardImage) {
            this.cardNo = cardNo;
            setCard(cardUrl, cardImage);
        }

        private void setCard(String cardUrl, Image cardImage) {
            try {
                ResourceLoader resourceLoader = new DefaultResourceLoader();
                Resource resource = resourceLoader.getResource(cardUrl);
                FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
                card = fxmlLoader.load();
                card.setFill(new ImagePattern(cardImage));
            }
            catch (IOException e) {
                throw new RuntimeException();
            }
        }

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

    /**
     * Table holds the card number and the card itself as key value pair.
     * Attention: Only created once and shared among games.
     */
    @Component
    public static class CardTable {
        private final Map<Short, Card> mCardMap;
        private       ImageManager     mImageManager;

        /** Constructor of the table; called when app starts*/
        @Autowired
        public CardTable(ImageManager imageManager,
                         @Value("${spring.application.screen.card.url}") String cardUrl) {
            this.mImageManager = imageManager;
            mCardMap = new HashMap<>();
            initCards(cardUrl);
        }

        /** Return the card itself with that card number*/
        public Card getCard(short cardNo){
            return mCardMap.get(cardNo);
        }

        /** Return the list of cards with that card number*/
        public List<Card> getAllCards(List<Short> cardList) {
            List<Card> cards = new ArrayList<>();
            for(Short cardNo : cardList) {
                cards.add(getCard(cardNo));
            }
            return cards;
        }

        /** Forms the map, helper function of the constructor*/
        private void initCards(String cardUrl){
            for(int i = 0; i < Card.Suit.values().length; i++) {
                for (int j = 0; j < Card.Rank.values().length; j++) {
                    short cardNo = (short) (i*Card.Rank.values().length + j);
                    Image cardImage = mImageManager.getCardImage(cardNo);
                    mCardMap.put(cardNo, new Card(cardNo, cardUrl, cardImage));
                }
            }
        }

    }
}
