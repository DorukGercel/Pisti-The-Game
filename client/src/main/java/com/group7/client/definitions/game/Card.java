package com.group7.client.definitions.game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/** Definition of a single card in the game*/
@Data
public class Card {
    private Short cardNo;
    @FXML private Rectangle card;

    public Card(Short cardNo, String cardUrl, Image cardImage) {
        this.cardNo = cardNo;
        setCard(cardUrl, cardImage);
    }

    private void setCard(String cardUrl, Image cardImage) {
        try {
            // Load the card layout from fxml
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(cardUrl);
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
            card = fxmlLoader.load();
            // Fill the face of the card with image
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
