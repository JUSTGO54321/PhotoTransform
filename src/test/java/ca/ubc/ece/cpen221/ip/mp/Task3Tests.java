package ca.ubc.ece.cpen221.ip.mp;

import ca.ubc.ece.cpen221.ip.core.Image;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task3Tests {

    @Test
    public void test_cosineSimilarity() {
        Image image1 = new Image(2, 2);
        Image image2 = new Image(2, 2);
        image1.set(0, 0, new Color(50, 50, 50));
        image1.set(0, 1, new Color(100, 100, 100));
        image1.set(1, 0, new Color(150, 150, 150));
        image1.set(1, 1, new Color(200, 200, 200));

        image2.set(0, 0, new Color(25, 255, 25));
        image2.set(0, 1, new Color(75, 75, 255));
        image2.set(1, 0, new Color(255, 255, 125));
        image2.set(1, 1, new Color(0, 0, 0));

        double expectedValue = 0.6438142;
        assertTrue(expectedValue - ImageProcessing.cosineSimilarity(image1, image2) < 0.0001);
    }

    @Test public void test_differentDimensions() {
        Image image1 = new Image("resources/15088.jpg");
        Image image2 = new Image("resources/2092.jpg");
        IllegalArgumentException t = assertThrows(IllegalArgumentException.class,
                () -> {ImageProcessing.cosineSimilarity(image1, image2);}
        );
    }

    @Test
    public void test_CosWhiteBlack() {
        Image image1 = new Image(1, 1);
        Image image2 = new Image(1, 1);
        image2.set(0, 0, new Color(255, 255, 255));
        double similarity = ImageProcessing.cosineSimilarity(image1, image2);
        assertEquals(similarity, 0);
    }

    @Test
    public void test_onePixelImages() {
        Image image1 = new Image(1, 1);
        Image image2 = new Image(1, 1);
        image1.set(0, 0, new Color(1, 1, 1));
        image2.set(0, 0, new Color(255, 255, 255));
        assertEquals(ImageProcessing.cosineSimilarity(image1, image2), 1);
    }

    @Test
    public void test_twoBlackImages() {
        Image image1 = new Image(1, 1);
        Image image2 = new Image(1, 1);
        assertEquals(ImageProcessing.cosineSimilarity(image1, image2), 1);
    }

    @Test
    public void test_oneBlackImages() {
        Image image1 = new Image(1, 1);
        Image image2 = new Image(1, 1);
        image2.set(0, 0, new Color(100,200,132));
        double expectedValue = 0;
        assertEquals(ImageProcessing.cosineSimilarity(image1, image2), expectedValue);
    }

    @Test
    public void test_twoPixelsVeryDifferent() {
        Image image1 = new Image(2, 1);
        image1.set(0, 0, new Color(1, 1, 1));
        image1.set(1, 0, new Color(1, 1, 1));
        Image image2 = new Image(2, 1);
        image2.set(0, 0, new Color(255, 255, 255));
        image2.set(1, 0, new Color(255, 255, 255));
        double expectedValue = 0.999999;
        assertTrue(Math.abs(expectedValue - ImageProcessing.cosineSimilarity(image1, image2)) < 0.000001);
    }

    @Test
    public void test_orthogonalImages() {
        Image image1 = new Image(2, 1);
        image1.set(1, 0, new Color(255, 255, 255));
        Image image2 = new Image(2, 1);
        image2.set(0, 0, new Color(255, 255, 255));
        double expectedValue = 0;
        assertEquals(ImageProcessing.cosineSimilarity(image1, image2), expectedValue);
    }

    @Test
    public void test_NullImage() {
        Image image1 = null;
        Image image2 = new Image("resources/tests/12003-r30.png");
        assertThrows(NullPointerException.class, () -> ImageProcessing.cosineSimilarity(image1, image2));
    }

    @Test
    public void test_bestMatches() {
        Image image1 = new Image(2, 2);
        Image image2 = new Image(2, 2);
        Image image3 = new Image(2, 2);
        Image image4 = new Image(2, 2);

        image1.set(0, 0, new Color(50, 50, 50));
        image1.set(1, 0, new Color(100, 100, 100));
        image1.set(0, 1, new Color(150, 150, 150));
        image1.set(1, 1, new Color(200, 200, 200));

        image2.set(0, 0, new Color(15, 15, 15)); // 0.841789
        image2.set(1, 0, new Color(63, 63, 63));
        image2.set(0, 1, new Color(5, 5, 5));
        image2.set(1, 1, new Color(78, 78, 78));

        image3.set(0, 0, new Color(56, 56, 56)); // 0.581022
        image3.set(1, 0, new Color(5, 5, 5));
        image3.set(0, 1, new Color(255, 255, 255));
        image3.set(1, 1, new Color(0, 0, 0));

        image4.set(0, 0, new Color(50, 50, 50)); // 0.999999
        image4.set(1, 0, new Color(100, 100, 100));
        image4.set(0, 1, new Color(150, 150, 150));
        image4.set(1, 1, new Color(200, 200, 200));

        List<Image> candidateImages = new ArrayList<>();
        List<Image> expectedImages = new ArrayList<>();

        candidateImages.add(image2);
        candidateImages.add(image3);
        candidateImages.add(image4);

        expectedImages.add(image4);
        expectedImages.add(image2);
        expectedImages.add(image3);

        List<Image> outputList = ImageProcessing.bestMatch(image1, candidateImages);
        assertEquals(outputList, expectedImages);
    }

}
