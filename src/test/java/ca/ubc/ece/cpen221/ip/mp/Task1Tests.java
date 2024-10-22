package ca.ubc.ece.cpen221.ip.mp;

import ca.ubc.ece.cpen221.ip.core.Image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Task1Tests {

    @Test
    public void test_Red() {
        Image originalImg = new Image(5, 5);
        Color testColour = new Color(100, 200, 30);
        originalImg.set(2, 2, testColour);
        Image expectedImg = new Image(5, 5);
        expectedImg.set(2, 2, new Color(100, 0, 0));
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImage = t.red();
        assertEquals(expectedImg, outputImage);
    }

    @Test
    public void test_Mirroring() {
        Image originalImg = new Image("resources/15088.jpg");
        Image expectedImg = new Image("resources/tests/15088-mirror.png");
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImage = t.mirror();
        assertEquals(expectedImg, outputImage);
    }

    @Test
    public void test_NullImage() {
        NullPointerException t = assertThrows(NullPointerException.class,
                () -> {new ImageTransformer(null);}
        );
    }

    @Test
    public void test_Negative() {
        Image originalImg = new Image("resources/15088.jpg");
        Image expectedImg = new Image("resources/tests/15088-negative.png");
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImage = t.negative();
        assertEquals(expectedImg, outputImage);
    }

    @Test
    public void test_Posterize() {
        Image originalImg = new Image("resources/15088.jpg");
        Image expectedImg = new Image("resources/tests/15088-poster.png");
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImage = t.posterize();
        assertEquals(expectedImg, outputImage);
    }

    @Test
    public void test_PosterizeBoundaries() {
        Image originalImg = new Image(1, 2);
        Image expectedImg = new Image(1, 2);
        originalImg.set(0, 0, new Color(0, 64, 65));
        originalImg.set(0, 1, new Color(128, 129, 255));
        expectedImg.set(0, 0, new Color(32, 32, 96));
        expectedImg.set(0, 1, new Color(96, 222, 222));
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImage = t.posterize();
        assertEquals(expectedImg, outputImage);
    }

}
