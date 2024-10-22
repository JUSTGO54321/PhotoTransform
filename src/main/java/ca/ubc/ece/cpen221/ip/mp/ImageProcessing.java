package ca.ubc.ece.cpen221.ip.mp;

import ca.ubc.ece.cpen221.ip.core.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides some simple operations involving
 * more than one image.
 */
public class ImageProcessing {
    
    private static final int FOUR_BYTE_HEX = 0xFF;
    
    /* ===== TASK 3 ===== */

    /**
     * Collapses a 2-D array into a 1-D vector.
     *
     * @param image the 2-D array of pixels
     * @return a 2-D array collapsed into a vector
     */
    private static List<Integer> collapseMatrix(Image image) {
        List<Integer> vector = new ArrayList<>();
        for (int i = 0; i < image.height(); i++) {
            for (int j = 0; j < image.width(); j++) {
                int gray = image.getRGB(j, i) & FOUR_BYTE_HEX;
                vector.add(gray);
            }
        }
        return vector;
    }

    /**
     * Calculates the cosine similarity values between two images.
     * The two images must the exact same dimensions and must not be null or black.
     *
     * @param img1 the first image, must not be null or black
     * @param img2 the second image, must not be null or black and must have same dimensions as img1
     * @return the cosine similarity value between two images
     */
    public static double cosineSimilarity(Image img1, Image img2) {

        if (img1 == null || img2 == null) {
            throw new NullPointerException("Images cannot be null!");
        }

        if (img1.width() != img2.width() || img1.height() != img2.height()) {
            throw new IllegalArgumentException("Images are not the same dimensions!");
        }

        ImageTransformer transform1 = new ImageTransformer(img1);
        ImageTransformer transform2 = new ImageTransformer(img2);
        Image gray1 = transform1.grayscale();
        Image gray2 = transform2.grayscale();
        List<Integer> vec1 = collapseMatrix(gray1);
        List<Integer> vec2 = collapseMatrix(gray2);

        double vec1Mag = 0;
        double vec2Mag = 0;
        long dot = 0;
        for (int i = 0; i < vec1.size(); i++) {
            long vec1Element = vec1.get(i);
            long vec2Element = vec2.get(i);
            dot += vec1Element * vec2Element;
            vec1Mag += vec1Element * vec1Element;
            vec2Mag += vec2Element * vec2Element;
        }

        if (vec1Mag == 0 && vec2Mag == 0) {
            return 1;
        } else if (vec1Mag == 0 || vec2Mag == 0) {
            return 0;
        } else {
            return dot / (Math.sqrt(vec1Mag) * Math.sqrt(vec2Mag));
        }
    }

    /**
     * Returns a list of images sorted by cosine similarity values to a given image.
     * List is ordered in descending order starting with the highest similarity picture first.
     *
     * @param img                the image to be compared with
     * @param matchingCandidates a list of images to be compared with img
     * @return a sorted list of images based on cosine similarity values
     */
    public static List<Image> bestMatch(Image img, List<Image> matchingCandidates) {
        List<Double> similarities = new ArrayList<>();

        for (Image image : matchingCandidates) {
            double similarity = cosineSimilarity(img, image);
            similarities.add(similarity);
        }

        int matchSize = matchingCandidates.size();
        QuickSort.qSortMapAsTwoLists(similarities, matchingCandidates, 0, matchSize - 1);
        Collections.reverse(matchingCandidates);
        return matchingCandidates;
    }

    /**
     * This class allows for sorting two related lists according to values in one list
     */
    private static class QuickSort {

        static int partition(List<Double> values, List<Image> images, int low, int high) {
            double pivot = values.get(high);
            int i = low - 1;
            for (int j = low; j <= high - 1; j++) {
                if (values.get(j) <= pivot) {
                    i++;
                    double swapValueI = values.get(i);
                    double swapValueJ = values.get(j);
                    values.set(i, swapValueJ);
                    values.set(j, swapValueI);
                    Image swapImageI = images.get(i);
                    Image swapImageJ = images.get(j);
                    images.set(i, swapImageJ);
                    images.set(j, swapImageI);
                }
            }

            double swapValueI = values.get(i + 1);
            double swapValueHigh = values.get(high);
            values.set(i + 1, swapValueHigh);
            values.set(high, swapValueI);
            Image swapImageI = images.get(i + 1);
            Image swapImageHigh = images.get(high);
            images.set(i + 1, swapImageHigh);
            images.set(high, swapImageI);
            return i + 1;
        }

        static void qSortMapAsTwoLists(List<Double> values, List<Image> images, int low, int high) {
            if (low < high) {
                int partitionIndex = partition(values, images, low, high);
                qSortMapAsTwoLists(values, images, low, partitionIndex - 1);
                qSortMapAsTwoLists(values, images, partitionIndex + 1, high);
            }
        }
    }
}
