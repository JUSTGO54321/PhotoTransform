package ca.ubc.ece.cpen221.ip.mp;

import ca.ubc.ece.cpen221.ip.core.Image;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Task2Tests {

    @Test
    public void test_Weathering() {
        Image originalImg = new Image("resources/95006.jpg");
        Image expectedImg = new Image("resources/tests/95006-weathered.png");
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImage = t.weather();
        assertEquals(expectedImg, outputImage);
    }

    @Test
    public void test_WeatheringOnePixel() {
        Image originalImg = new Image(1, 1);
        Image expectedImg = new Image(1, 1);
        originalImg.set(0, 0, new Color(50, 100, 150));
        expectedImg.set(0, 0, new Color(50, 100, 150));
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.weather();
        assertEquals(expectedImg, outputImg);
    }

    @Test
    public void test_denoiseSameColor() {
        Image originalImg = new Image(3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                originalImg.set(i, j, new Color(50, 100, 150));
            }
        }
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.denoise();
        assertEquals(originalImg, outputImg);
    }

    @Test
    public void test_denoiseOnePixel() {
        Image originalImg = new Image(1, 1);
        Image expectedImg = new Image(1, 1);
        originalImg.set(0, 0, new Color(50, 100, 150));
        expectedImg.set(0, 0, new Color(50, 100, 150));
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.weather();
        assertEquals(expectedImg, outputImg);
    }

    @Test
    public void test_blockPainting3x3() {
        Image originalImg = new Image("resources/216053.jpg");
        Image expectedImg = new Image("resources/tests/216053-seurat-3x3.png");
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.blockPaint(3);
        assertEquals(expectedImg, outputImg);
    }

    @Test
    public void test_blockPainting4x4() {
        Image originalImg = new Image("resources/95006.jpg");
        Image expectedImg = new Image("resources/tests/95006-seurat-4x4.png");
        ImageTransformer t = new ImageTransformer(originalImg);
        Image outputImg = t.blockPaint(4);
        assertEquals(expectedImg, outputImg);
    }

    @Test
    public void test_blockPaintBlockGreaterThanWH() {
        Image originalImg = new Image(2, 2);
        ImageTransformer t = new ImageTransformer(originalImg);
        assertThrows(IllegalArgumentException.class, () -> t.blockPaint(3));
    }

    @Test
    public void test_blockPaintBlockGreaterThanOneSide() {
        Image originalImg = new Image(50, 2);
        ImageTransformer t = new ImageTransformer(originalImg);
        assertThrows(IllegalArgumentException.class, () -> t.blockPaint(3));
    }

}
