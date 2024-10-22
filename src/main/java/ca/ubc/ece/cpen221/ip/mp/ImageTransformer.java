package ca.ubc.ece.cpen221.ip.mp;

import ca.ubc.ece.cpen221.ip.core.Image;
import ca.ubc.ece.cpen221.ip.core.Rectangle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * This datatype (or class) provides operations for transforming an image.
 *
 * <p>The operations supported are:
 * <ul>
 *     <li>The {@code ImageTransformer} constructor generates an instance of an image that
 *     we would like to transform;</li>
 *     <li></li>
 * </ul>
 * </p>
 */

@SuppressWarnings({"checkstyle:CommentsIndentation", "checkstyle:TodoComment"})
public class ImageTransformer {

    private final Image image;
    private final int width;
    private final int height;
    private static final int DEFAULT_ALPHA = 255;
    private static final int POSTER_0_64 = 32;
    private static final int POSTER_65_128 = 96;
    private static final int POSTER_129_255 = 222;
    private static final int POSTER_LOW = 64;
    private static final int POSTER_MID = 128;
    private static final int BIT_SHIFT_ALPHA = 24;
    private static final int BIT_SHIFT_RED = 16;
    private static final int BIT_SHIFT_GREEN = 8;
    private static final int MAX_COLOUR_VALUE = 255;
    private static final int FOUR_BYTE_HEX = 0xFF;

    /**
     * Creates an ImageTransformer with an image. The provided image is
     * <strong>never</strong> changed by any of the operations.
     *
     * @param img is not null
     */
    public ImageTransformer(Image img) {
        if (img == null) {
            throw new NullPointerException("Image cannot be null!");
        }
        this.image = img;
        this.width = img.width();
        this.height = img.height();
    }

    /**
     * Obtain the grayscale version of the image.
     *
     * @return the grayscale version of the instance.
     */
    public Image grayscale() {
        Image gsImage = new Image(width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                Color color = image.get(col, row);
                Color gray = Image.toGray(color);
                gsImage.set(col, row, gray);
            }
        }
        return gsImage;
    }

    /**
     * Obtain a version of the image with only the red colours.
     *
     * @return a reds-only version of the instance.
     */
    public Image red() {
        Image redImage = new Image(width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                int originalPixel = image.getRGB(col, row);
                int alpha = (originalPixel >> BIT_SHIFT_ALPHA) & FOUR_BYTE_HEX;
                int red = (originalPixel >> BIT_SHIFT_RED) & FOUR_BYTE_HEX;
                int desiredColor = (alpha << BIT_SHIFT_ALPHA) | (red << BIT_SHIFT_RED) | (0);
                redImage.setRGB(col, row, desiredColor);
            }
        }
        return redImage;
    }

    /* ===== TASK 1 ===== */

    /**
     * Returns the mirror image of an instance.
     *
     * @return the mirror image of the instance.
     */
    public Image mirror() {
        Image mirrorImage = new Image(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 2 + 1; j++) {
                int pixelRight = image.getRGB(width - j - 1, i);
                int pixelLeft = image.getRGB(j, i);
                mirrorImage.setRGB(j, i, pixelRight);
                mirrorImage.setRGB(width - j - 1, i, pixelLeft);
            }
        }
        return mirrorImage;
    }

    /**
     * <p>Returns the negative version of an instance.<br />
     * If the colour of a pixel is (r, g, b) then the colours of the same pixel
     * in the negative of the image are (255-r, 255-g, 255-b).</p>
     *
     * @return the negative of the instance.
     */
    public Image negative() {
        Image negativeImage = new Image(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int originalPixel = image.getRGB(j, i);
                int alpha = (originalPixel >> BIT_SHIFT_ALPHA) & FOUR_BYTE_HEX;
                int red = MAX_COLOUR_VALUE - ((originalPixel >> BIT_SHIFT_RED) & FOUR_BYTE_HEX);
                int green = MAX_COLOUR_VALUE - ((originalPixel >> BIT_SHIFT_GREEN) & FOUR_BYTE_HEX);
                int blue = MAX_COLOUR_VALUE - (originalPixel & FOUR_BYTE_HEX);
                int negativeRGB = (alpha << BIT_SHIFT_ALPHA) | (red << BIT_SHIFT_RED)
                        | (green << BIT_SHIFT_GREEN) | (blue);
                negativeImage.setRGB(j, i, negativeRGB);
            }
        }
        return negativeImage;
    }

    /**
     * Returns the posterized colour value for an individual colour component given the original component value
     *
     * @param colour the integer value of a colour component
     * @return the posterized value of the component colour
     */
    private int calculatePosterizeColour(int colour) {

        if (colour <= POSTER_LOW) {
            return POSTER_0_64;
        } else if (colour <= POSTER_MID) {
            return POSTER_65_128;
        } else {
            return POSTER_129_255;
        }
    }

    /**
     * Returns the posterized version of an instance.
     *
     * @return the posterized version of the instance
     */
    public Image posterize() {
        Image posterizedImage = new Image(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int originalPixel = image.getRGB(j, i);
                int red = calculatePosterizeColour((originalPixel >> BIT_SHIFT_RED) & FOUR_BYTE_HEX);
                int green = calculatePosterizeColour((originalPixel >> BIT_SHIFT_GREEN) & FOUR_BYTE_HEX);
                int blue = calculatePosterizeColour(originalPixel & FOUR_BYTE_HEX);
                int colour = (DEFAULT_ALPHA << BIT_SHIFT_ALPHA) | (red << BIT_SHIFT_RED)
                        | (green << BIT_SHIFT_GREEN) | (blue);
                posterizedImage.setRGB(j, i, colour);
            }
        }
        return posterizedImage;
    }


    /* ===== TASK 2 ===== */

    /**
     * Returns the median value of a list of integers
     *
     * @param pixels a list of the integer representation of colour components
     * @return the median value of a list of integers
     */
    private int median(List<Integer> pixels) {
        Collections.sort(pixels);
        int numPixels = pixels.size();

        if (pixels.size() % 2 != 0) {
            return pixels.get(numPixels / 2);
        }

        return (pixels.get((numPixels - 1) / 2) + pixels.get(numPixels / 2)) / 2;
    }

    /**
     * Returns a list of Colors surrounding the given pixel, including the pixel itself.
     *
     * @param row the row of the input pixel
     * @param col the column of the input pixel
     * @return a list of Colors surrounding the input pixel, including the pixel itself.
     */
    private List<Color> getPixelNeighbourhood(int row, int col) {
        List<Color> pixelNeighbourhood = new ArrayList<Color>();
        int[][] directions = {{0, 0}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] k : directions) {
            if (row + k[0] >= 0 && row + k[0] < height && col + k[1] >= 0 && col + k[1] < width) {
                Color neighbouringPixel = image.get(col + k[1], row + k[0]);
                pixelNeighbourhood.add(neighbouringPixel);
            }
        }
        return pixelNeighbourhood;
    }

    /**
     * Returns a denoised version of an instance.
     *
     * @return the denoised version of the image
     */
    public Image denoise() {
        Image denoiseImage = new Image(width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                List<Color> pixelNeighbourhood = getPixelNeighbourhood(i, j);
                List<Integer> redList = new ArrayList<Integer>();
                List<Integer> greenList = new ArrayList<Integer>();
                List<Integer> blueList = new ArrayList<Integer>();
                for (Color pixel : pixelNeighbourhood) {
                    int red = pixel.getRed();
                    int green = pixel.getGreen();
                    int blue = pixel.getBlue();
                    redList.add(red);
                    greenList.add(green);
                    blueList.add(blue);
                }
                int medianRed = median(redList);
                int medianGreen = median(greenList);
                int medianBlue = median(blueList);
                int medianColour = (DEFAULT_ALPHA << BIT_SHIFT_ALPHA) | (medianRed << BIT_SHIFT_RED)
                        | (medianGreen << BIT_SHIFT_GREEN) | medianBlue;
                denoiseImage.setRGB(j, i, medianColour);
            }
        }
        return denoiseImage;
    }

    /**
     * @return a weathered version of the image.
     */
    public Image weather() {
        Image weatheredImage = new Image(width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                List<Color> pixelNeighbourhood = getPixelNeighbourhood(i, j);
                List<Integer> redList = new ArrayList<Integer>();
                List<Integer> greenList = new ArrayList<Integer>();
                List<Integer> blueList = new ArrayList<Integer>();
                for (Color pixel : pixelNeighbourhood) {
                    int red = pixel.getRed();
                    int green = pixel.getGreen();
                    int blue = pixel.getBlue();
                    redList.add(red);
                    greenList.add(green);
                    blueList.add(blue);
                }
                int minRed = Collections.min(redList);
                int minGreen = Collections.min(greenList);
                int minBlue = Collections.min(blueList);
                int minColour = (DEFAULT_ALPHA << BIT_SHIFT_ALPHA) | (minRed << BIT_SHIFT_RED)
                        | (minGreen << BIT_SHIFT_GREEN) | minBlue;
                weatheredImage.setRGB(j, i, minColour);
            }
        }
        return weatheredImage;
    }

    /**
     * Returns the mean Color object of the pixels within a blockHeight by blockWidth rectangle of the original pixel
     *
     * @param currentRow  the row of the input pixel
     * @param currentCol  the column of the input pixel
     * @param blockHeight the height of the block being painted
     * @param blockWidth  the width of the block being painted
     * @return a Color with the mean component values of each pixel in the pixel neighbourhood
     */
    private Color getBlockRGBAverage(int currentRow, int currentCol, int blockHeight, int blockWidth) {
        int red = 0;
        int green = 0;
        int blue = 0;
        for (int i = currentRow; i < currentRow + blockHeight; i++) {
            for (int j = currentCol; j < currentCol + blockWidth; j++) {
                int pixel = image.getRGB(j, i);
                red += (pixel >> BIT_SHIFT_RED) & FOUR_BYTE_HEX;
                green += (pixel >> BIT_SHIFT_GREEN) & FOUR_BYTE_HEX;
                blue += pixel & FOUR_BYTE_HEX;
            }
        }
        red /= (blockHeight * blockWidth);
        green /= (blockHeight * blockWidth);
        blue /= (blockHeight * blockWidth);
        return new Color(red, green, blue);
    }

    /**
     * Returns an image as a sequence of m by m blocks of pixels.
     *
     * @param blockSize the size of the block, must be less than width or height
     * @return an image as a sequence of m by m blocks of pixels
     */
    public Image blockPaint(int blockSize) {

        if (blockSize > width || blockSize > height) {
            throw new IllegalArgumentException("Block Size is greater than width or height!");
        }

        Image blockPaintImage = new Image(width, height);
        for (int i = 0; i < height; i += blockSize) {
            for (int j = 0; j < width; j += blockSize) {
                int blockHeight = Math.min(blockSize, height - i);
                int blockWidth = Math.min(blockSize, width - j);
                Color avgRGB = getBlockRGBAverage(i, j, blockHeight, blockWidth);
                for (int blockRow = i; blockRow < i + blockHeight; blockRow++) {
                    for (int blockCol = j; blockCol < j + blockWidth; blockCol++) {
                        blockPaintImage.set(blockCol, blockRow, avgRGB);
                    }
                }
            }
        }
        return blockPaintImage;
    }


    /* ===== TASK 4 ===== */

    /**
     * Returns a list of the largest region of connected pixels
     *
     * @param screenColour the target pixel colour
     * @return a list of the largest region of connected pixels
     */
    private List<Integer[]> findLargestConnectedRegion(Color screenColour) {
        boolean[][] visited = new boolean[height][width];
        List<Integer[]> largestRegion = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color pixel = image.get(j, i);
                if (!visited[i][j] && isMatchingColour(screenColour, pixel)) {
                    List<Integer[]> region = findConnectedRegion(i, j, screenColour, visited);
                    if (region.size() > largestRegion.size()) {
                        largestRegion = region;
                    }
                }
            }
        }
        return largestRegion;
    }

    /**
     * Returns a list of coordinates of connected pixels
     *
     * @param row          the row of the current pixel
     * @param col          the column of the current pixel
     * @param screenColour the target colour being replaced
     * @param visited      a 2-D boolean array containing pixels that have already been visited
     * @return a list of coordinates of connected pixels
     */
    private List<Integer[]> findConnectedRegion(int row, int col, Color screenColour, boolean[][] visited) {
        List<Integer[]> region = new ArrayList<>();
        Stack<Integer[]> stack = new Stack<>();
        Integer[] point = new Integer[]{row, col};
        stack.push(point);

        while (!stack.isEmpty()) {
            point = stack.pop();
            int x = point[0];
            int y = point[1];

            if (x >= 0 && x < height && y >= 0 && y < width && !visited[x][y]) {
                Color currentColour = image.get(y, x);
                if (isMatchingColour(screenColour, currentColour)) {
                    visited[x][y] = true;
                    region.add(new Integer[]{x, y});
                    stack.push(new Integer[]{x, y + 1});
                    stack.push(new Integer[]{x, y - 1});
                    stack.push(new Integer[]{x + 1, y});
                    stack.push(new Integer[]{x - 1, y});
                    stack.push(new Integer[]{x + 1, y + 1});
                    stack.push(new Integer[]{x + 1, y - 1});
                    stack.push(new Integer[]{x - 1, y + 1});
                    stack.push(new Integer[]{x - 1, y - 1});
                }
            }
        }
        return region;
    }

    /**
     * Returns the smallest rectangle containing a region of pixels
     *
     * @param region a list of pixel coordinates
     * @return the smallest rectangle containing a region of pixels
     */
    private Rectangle findRectangle(List<Integer[]> region) {
        int minRow = height;
        int minCol = width;
        int maxRow = 0;
        int maxCol = 0;
        for (Integer[] point : region) {
            if (point[0] < minRow) {
                minRow = point[0];
            }
            if (point[0] > maxRow) {
                maxRow = point[0];
            }
            if (point[1] < minCol) {
                minCol = point[1];
            }
            if (point[1] > maxCol) {
                maxCol = point[1];
            }
        }
        return new Rectangle(minRow, minCol, maxRow, maxCol);
    }

    /**
     * Overlays the background image onto the original image
     *
     * @param greenScreenImage the original image
     * @param screenColour     the given colour being replaced
     * @param rectangle        the smallest rectangle containing the largest connection region of screenColour
     * @param backgroundImage  the image being overlayed
     */
    private void setGreenScreen(Image greenScreenImage, Color screenColour,
                                Rectangle rectangle, Image backgroundImage) {
        for (int i = rectangle.xTopLeft; i <= rectangle.xBottomRight; i++) {
            for (int j = rectangle.yTopLeft; j <= rectangle.yBottomRight; j++) {
                Color currentPixel = greenScreenImage.get(j, i);
                if (currentPixel.equals(screenColour)) {
                    int backWidth = backgroundImage.width();
                    int backHeight = backgroundImage.height();
                    Color backColour = backgroundImage.get(j % backWidth, i % backHeight);
                    greenScreenImage.set(j, i, backColour);
                }
            }
        }
    }

    /**
     * Returns an image with a background image overlayed onto the largest connected area of a given pixel colour.
     *
     * @param screenColour    the pixel colour targeted for overlaying
     * @param backgroundImage the image being overlayed onto the original image
     * @return the instance image with an overlayed background image
     */
    public Image greenScreen(Color screenColour, Image backgroundImage) {
        Image greenScreenImage = new Image(image);
        List<Integer[]> region = findLargestConnectedRegion(screenColour);
        if (!region.isEmpty()) {
            Rectangle rectangle = findRectangle(region);
            setGreenScreen(greenScreenImage, screenColour, rectangle, backgroundImage);
        }
        return greenScreenImage;
    }

    private boolean isMatchingColour(Color targetColor, Color color) {
        return color.getRGB() == targetColor.getRGB();
    }

    /* ===== TASK 5 ===== */
    /*
    private double calculateAngle(Image img) {
        double[][] pixels = new double[img.height()][img.width()];
        for (int i = 0; i < img.height(); i++) {
            for (int j = 0; j < img.width(); j++) {
                Color pixelColour = img.get(j, i);
                pixels[i][j] = pixelColour.getRed();
            }
        }

        double[][] magnitude = new double[img.height()][img.width()];
        double[][] phase = new double[img.height()][img.width()];
        for (int u = 0; u < img.height(); u++) {
            for (int v = 0; v < img.width(); v++) {
                double real = 0;
                double imaginary = 0;

                for (int x = 0; x < img.height(); x++) {
                    for (int y = 0; y < img.width(); y++) {
                        double angle = -2 * Math.PI * ((u * x)
                                / (double) img.height() + (v * y) / (double) img.width());
                        real += pixels[x][y] * Math.cos(angle);
                        imaginary += pixels[x][y] * Math.sin(angle);
                    }
                }

                magnitude[u][v] = Math.log(Math.sqrt(Math.pow(real, 2) + Math.pow(imaginary, 2)));
                phase[u][v] = Math.atan2(imaginary, real);
                //System.out.println("magnitude[u][v] = " + magnitude[u][v]);
            }
        }
        System.out.println(magnitude[0][0]);
        DFTOutput dft = new DFTOutput(magnitude, phase);
        Image output = new Image(img.width(), img.height());
        for (int i = 0; i < img.height(); i++) {
            for (int j = 0; j < img.width(); j++) {
                //if(magnitude[i][j] > 11){
                int scaledMagnitude = (int) Math.floor(256 * (magnitude[i][j] / 18));
                //System.out.println(scaledMagnitude);
                output.set(j, i, new Color(scaledMagnitude, scaledMagnitude, scaledMagnitude));
                //}
            }
        }
        output.show();

        List<Double> angles = new ArrayList<>();
        for (int u = 0; u < img.height(); u++) {
            for (int v = 0; v < img.width(); v++) {
                if (magnitude[u][v] > 10) {
                    angles.add(phase[u][v]);
                }
            }
        }
        double avgAngle = 0;
        for (double angle : angles) {
            avgAngle += angle;
        }
        //System.out.println("avgAngle = " + avgAngle);
        avgAngle /= angles.size();
        return Math.toDegrees(avgAngle);
    }

    public Image rotateImage(double angle) {
        angle = Math.toRadians(angle);
        int newWidth = (int) Math.round(Math.abs(width * Math.cos(angle)) + Math.abs(height * Math.sin(angle)));
        int newHeight = (int) Math.round(Math.abs(width * Math.sin(angle)) + Math.abs(height * Math.cos(angle)));
        Image rotatedImage = new Image(newWidth, newHeight);

        for (int col = 0; col < newWidth; col++) {
            for (int row = 0; row < newHeight; row++) {
                int originalX = (int) ((col - newWidth / 2) * Math.cos(angle)
                        + (row - newHeight / 2) * Math.sin(angle) + width / 2);
                int originalY = (int) (-(col - newWidth / 2) * Math.sin(angle)
                        + (row - newHeight / 2) * Math.cos(angle) + height / 2);
                if (originalX >= 0 && originalY >= 0 && originalX < width && originalY < height) {
                    rotatedImage.set(col, row, image.get(originalX, originalY));
                } else {
                    rotatedImage.set(col, row, Color.white);
                }
            }
        }
        return rotatedImage;
    }

    public Image alignTextImage() {
        ImageTransformer t = new ImageTransformer(image);
        Image alignedImage = t.grayscale();
        Image downscaleAlignedImage = downscale(alignedImage, 4);
        double angle = calculateAngle(downscaleAlignedImage);
        Image rotatedImage = rotateImage(angle);
        System.out.println(angle);
        return rotatedImage;
    }

    public Image downscale(Image img, int scale) {

        int newWidth = img.width() / scale;
        int newHeight = img.height() / scale;
        Image downscaledImage = new Image(newWidth, newHeight);

        // Calculate the scale factor for width and height


        // Iterate over each pixel in the downscaled image
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                // Determine the region in the original image to average
                int xStart = x * scale;
                int yStart = y * scale;
                int xEnd = Math.min((x + 1) * scale, img.width());
                int yEnd = Math.min((y + 1) * scale, img.height());

                // Accumulate the color values
                long redSum = 0, greenSum = 0, blueSum = 0;
                int count = 0;

                for (int i = yStart; i < yEnd; i++) {
                    for (int j = xStart; j < xEnd; j++) {
                        int rgb = img.getRGB(j, i);
                        int red = (rgb >> BIT_SHIFT_RED) & FOUR_BYTE_HEX;
                        int green = (rgb >> BIT_SHIFT_GREEN) & FOUR_BYTE_HEX;
                        int blue = rgb & FOUR_BYTE_HEX;

                        redSum += red;
                        greenSum += green;
                        blueSum += blue;
                        count++;
                    }
                }

                // Calculate the average color
                int avgRed = (int) (redSum / count);
                int avgGreen = (int) (greenSum / count);
                int avgBlue = (int) (blueSum / count);
                int avgRgb = (avgRed << BIT_SHIFT_RED) | (avgGreen << BIT_SHIFT_GREEN) | avgBlue;

                // Set the averaged color to the downscaled image
                downscaledImage.setRGB(x, y, avgRgb);
            }
        }

        return downscaledImage;

    }
    */
}
