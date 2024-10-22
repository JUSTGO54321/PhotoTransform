package ca.ubc.ece.cpen221.ip.mp;

import ca.ubc.ece.cpen221.ip.core.Image;
import java.awt.Color;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Task4Tests {

    @Test
    public void test_GreenScreen() {
        Image originalImg = new Image("resources/tests/blue4x4.png");
        Image expectedImg = new Image("resources/tests/yellow4x4.png");
        Image tile = new Image("resources/tests/yellow2x2.png");

        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.greenScreen(new Color(0, 0, 255), tile);
        assertEquals(expectedImg, outputImg);
    }

    @Test
    public void test_GreenScreenOnePixel() {
        Image originalImg = new Image(1, 1);
        Image expectedImg = new Image(1, 1);
        originalImg.set(0, 0, new Color(0, 0, 0));
        expectedImg.set(0, 0, new Color(50, 100, 150));

        ImageTransformer t = new ImageTransformer(originalImg);
        Image backgroundImg = new Image(1, 1);
        backgroundImg.set(0, 0, new Color(50, 100, 150));
        Image outputImg = t.greenScreen(new Color(0, 0, 0), backgroundImg);
        assertEquals(expectedImg, outputImg);
    }

    @Test
    public void test_NotGreenScreenOnePixel() {
        Image originalImg = new Image(1, 1);
        Image expectedImg = new Image(1, 1);
        originalImg.set(0, 0, new Color(0, 0, 0));
        expectedImg.set(0, 0, new Color(50, 100, 150));

        ImageTransformer t = new ImageTransformer(originalImg);
        Image greenImg = new Image(1, 1);
        greenImg.set(0, 0, new Color(50, 100, 150));
        Image outputImg = t.greenScreen(new Color(1, 2, 3), greenImg);
        assertNotEquals(expectedImg, outputImg);
    }

    @Test
    public void test_secondBoxBigger() {
        Image originalImg = new Image("resources/tests/bigGreenBox5x5.png");
        Image expectedImg = new Image("resources/tests/bigWhiteBox5x5.png");
        Image tile = new Image(1, 2);
        tile.set(0, 0, new Color(255,255,255));
        tile.set(0, 1, new Color(255, 255, 255));
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.greenScreen(new Color(0, 255, 0), tile);
        assertEquals(expectedImg, outputImg);
    }

    @Test
    public void test_greenNoNeighboursInsideRectangle() {
        Image originalImg = new Image("resources/tests/green5x5.png");
        Image expectedImg = new Image("resources/tests/red5x5.png");
        Image tile = new Image(2, 2);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                tile.set(j, i, new Color(255, 0, 0));
            }
        }
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.greenScreen(new Color(0, 255, 0), tile);
        assertEquals(expectedImg, outputImg);
    }

}