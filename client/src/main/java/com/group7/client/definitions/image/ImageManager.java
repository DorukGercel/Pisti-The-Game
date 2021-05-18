package com.group7.client.definitions.image;

import com.group7.client.definitions.game.GameConfig;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for creating the card images
 */
@Component
public class ImageManager {
    /** List that holds the card images*/
    List<Image> mCardImageList;
    /** Source image (atlas) of all the cards*/
    Image mCardAtlas;

    /** Constructor*/
    public ImageManager(@Value("${spring.application.ui.cardPool}") String poolSource,
                        @Value("${spring.application.ui.cardWidth}") Integer cardWidth,
                        @Value("${spring.application.ui.cardHeight}") Integer cardHeight) {
        mCardAtlas = new Image(poolSource);
        mCardImageList = new ArrayList<>();
        initCardImages(cardWidth, cardHeight);
    }

    /** Returns the image of the card with given no*/
    public Image getCardImage(short cardNo) {
        return mCardImageList.get(cardNo);
    }

    /** Initializes the card table of images*/
    private void initCardImages(int width, int height) {
        //Reading the color of the image
        int x_cord = 0, y_cord = 0;
        for(int y = 0; y < GameConfig.Card.Suit.values().length; y++) {
            for(int x = 0; x < GameConfig.Card.Rank.values().length; x++) {
                Image cardImage = initSingleCardImage(x_cord, y_cord, width, height);
                x_cord += width + x%2;
                mCardImageList.add(cardImage);
            }
            x_cord = 0;
            y_cord += height;
        }
    }

    /** Initializes single card image*/
    private Image initSingleCardImage(int initial_x_cord, int initial_y_cord, int width, int height) {
        //Creating a writable image
        WritableImage wImage = new WritableImage(width, height);
        //Reading color from the loaded image
        PixelReader pixelReader = mCardAtlas.getPixelReader();
        //getting the pixel writer
        PixelWriter writer = wImage.getPixelWriter();

        //Reading the color of the image
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                //Retrieving the color of the pixel of the loaded image
                Color color = pixelReader.getColor(x + initial_x_cord, y + initial_y_cord);
                //Setting the color to the writable image
                writer.setColor(x, y, color.darker());
            }
        }
        return wImage;
    }

}
