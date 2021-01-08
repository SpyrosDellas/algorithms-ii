/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 06/06/2020
 *
 * Description:
 * A mutable data type that resizes a W-by-H image using the seam-carving
 * technique.
 *
 * Seam-carving is a content-aware image resizing technique where the image is
 * reduced in size by one pixel of height (or width) at a time. A vertical seam
 * in an image is a path of pixels connected from the top to the bottom with
 * one pixel in each row; a horizontal seam is a path of pixels connected from
 * the left to the right with one pixel in each column.
 * Unlike standard content-agnostic resizing techniques (such as cropping and
 * scaling), seam carving preserves the most interest features (aspect ratio,
 * set of objects present, etc.) of the image.
 *
 * Corner cases.
 * Throws an IllegalArgumentException when a constructor or method is called
 * with an invalid argument, as documented below:
 * - By convention, the indices x and y are integers between 0 and width − 1
 * and between 0 and height − 1 respectively, where width is the width of the
 * current image and height is the height. Throws an IllegalArgumentException
 * if either x or y is outside its prescribed range.
 * - Throws an IllegalArgumentException if the constructor, removeVerticalSeam(),
 * or removeHorizontalSeam() is called with a null argument.
 * - Throws an IllegalArgumentException if removeVerticalSeam() or
 * removeHorizontalSeam() is called with an array of the wrong length or if the
 * array is not a valid seam (i.e., either an entry is outside its prescribed
 * range or two adjacent entries differ by more than 1).
 * - Throws an IllegalArgumentException if removeVerticalSeam() is called when
 * the width of the picture is less than or equal to 1 or if
 * removeHorizontalSeam() is called when the height of the picture is less than
 * or equal to 1.
 *
 * Performance requirements.
 * - The width(), height(), and energy() methods should take constant time in
 * the worst case. All other methods should run in time at most proportional
 * to width × height in the worst case.
 * - For faster performance, we do not construct explicit DirectedEdge and
 * EdgeWeightedDigraph objects.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;


public class SeamCarver {

    private static final double BORDER_ENERGY = 1000.0;
    private int width;       // current width of the picture stored in the array
    private int height;      // current height of the picture stored i nthe array
    private int[][] picture;       // internal picture representation as an array
    private double[][] energy;     // the energy of the pixels in picture
    private boolean isTransposed;  // is the picture transposed?


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {

        if (picture == null)
            throw new IllegalArgumentException();

        isTransposed = false;

        this.width = picture.width();
        this.height = picture.height();

        this.picture = new int[height][width];
        this.energy = new double[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.picture[i][j] = (picture.getRGB(j, i));
            }
        }

        for (int j = 0; j < width; j++) {
            energy[0][j] = BORDER_ENERGY;
        }
        for (int i = 1; i < height - 1; i++) {
            energy[i][0] = BORDER_ENERGY;
            for (int j = 1; j < width - 1; j++) {
                energy[i][j] = energyFast(j, i);
            }
            energy[i][width - 1] = BORDER_ENERGY;
        }
        for (int j = 0; j < width; j++) {
            energy[height - 1][j] = BORDER_ENERGY;
        }
    }

    // build and return current picture
    public Picture picture() {

        if (!isTransposed)
            return buildFromPicture();

        return buildFromTranspose();
    }

    private Picture buildFromPicture() {
        Picture result = new Picture(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.setRGB(j, i, picture[i][j]);
            }
        }
        return result;
    }

    private Picture buildFromTranspose() {
        Picture result = new Picture(height, width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.setRGB(i, j, picture[i][j]);
            }
        }
        return result;
    }


    // width of current picture
    public int width() {
        if (!isTransposed)
            return width;
        return height;
    }


    // height of current picture
    public int height() {
        if (!isTransposed)
            return height;
        return width;
    }


    // energy of pixel at column x and row y
    public double energy(int x, int y) {

        if (x < 0 || y < 0 || x >= width() || y >= height())
            throw new IllegalArgumentException();

        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
            return BORDER_ENERGY;

        if (!isTransposed) {
            return energyFast(x, y);
        }
        else {
            return energyFast(y, x);
        }
    }


    // energy of pixel at column x and row y
    // * No validity, transpose or border checks *
    private double energyFast(int col, int row) {
        return Math.sqrt(dx2(col, row) + dy2(col, row));
    }


    private int dx2(int col, int row) {

        int left = picture[row][col - 1];
        int right = picture[row][col + 1];

        int dr2 = ((right >> 16) & 0xFF) - ((left >> 16) & 0xFF);
        dr2 = dr2 * dr2;

        int dg2 = ((right >> 8) & 0xFF) - ((left >> 8) & 0xFF);
        dg2 = dg2 * dg2;

        int db2 = (right & 0xFF) - (left & 0xFF);
        db2 = db2 * db2;

        return dr2 + dg2 + db2;
    }


    private int dy2(int col, int row) {

        int up = picture[row - 1][col];
        int down = picture[row + 1][col];

        int dr2 = ((down >> 16) & 0xFF) - ((up >> 16) & 0xFF);
        dr2 = dr2 * dr2;

        int dg2 = ((down >> 8) & 0xFF) - ((up >> 8) & 0xFF);
        dg2 = dg2 * dg2;

        int db2 = (down & 0xFF) - (up & 0xFF);
        db2 = db2 * db2;

        return dr2 + dg2 + db2;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        if (!isTransposed)
            transpose();

        return findSeam();
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        if (isTransposed)
            transpose();

        return findSeam();
    }

    // calculate the vertical seam of minimum energy
    private int[] findSeam() {

        int[] seam = new int[height];

        if (width == 1)
            return seam;

        if (width == 2 || width == 3) {
            for (int i = 0; i < height; i++) {
                seam[i] = 1;
            }
            return seam;
        }

        byte[][] edgeTo = new byte[height][width];
        double[][] distTo = new double[height][width];

        for (int row = 1; row < height; row++) {

            distTo[row][0] = distTo[row - 1][1] + BORDER_ENERGY;
            edgeTo[row][0] = 1;

            distTo[row][width - 1] = distTo[row - 1][width - 2] + BORDER_ENERGY;
            edgeTo[row][width - 1] = -1;

            for (int col = 1; col < width - 1; col++) {
                double distance1 = distTo[row - 1][col - 1];
                double minDistance = distTo[row - 1][col];
                double distance3 = distTo[row - 1][col + 1];
                byte offset = 0;
                if (distance1 < minDistance && distance1 <= distance3) {
                    offset = -1;
                    minDistance = distance1;
                }
                else if (distance1 > distance3 && minDistance > distance3) {
                    offset = 1;
                    minDistance = distance3;
                }
                distTo[row][col] = minDistance + energy[row][col];
                edgeTo[row][col] = offset;
            }
        }

        double minDistance = distTo[height - 1][1];
        int minPos = 1;
        for (int col = 2; col < width - 1; col++) {
            double distance = distTo[height - 1][col];
            if (distance < minDistance) {
                minDistance = distance;
                minPos = col;
            }
        }

        int index = minPos;
        seam[height - 1] = index;
        for (int row = height - 2; row >= 0; row--) {
            index += edgeTo[row + 1][index];
            seam[row] = index;
        }

        return seam;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {

        if (seam == null || height() <= 1)
            throw new IllegalArgumentException();

        if (!isHorizontalSeam(seam))
            throw new IllegalArgumentException();

        if (!isTransposed) {
            transpose();
        }

        removeSeam(seam);
    }


    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

        if (seam == null || width() <= 1)
            throw new IllegalArgumentException();

        if (!isVerticalSeam(seam))
            throw new IllegalArgumentException();

        if (isTransposed) {
            transpose();
        }

        removeSeam(seam);
    }


    // remove a vertical seam from the picture (or its transpose)
    // No argument validity or transpose checks
    private void removeSeam(int[] seam) {

        if (width == 3 && height > 2) {
            for (int i = 1; i < height - 1; i++) {
                energy[i][1] = BORDER_ENERGY;
            }
        }

        for (int i = 0; i < height; i++) {
            int start = seam[i];
            if (start == width - 1)
                continue;
            System.arraycopy(picture[i], start + 1, picture[i], start, width - 1 - start);
        }

        if (width > 3 && height > 2) {
            for (int i = 1; i < height - 1; i++) {
                int start = seam[i];
                if (start == 0) {
                    System.arraycopy(energy[i], 2, energy[i], 1, width - 2);
                }
                else if (start == 1) {
                    energy[i][1] = energyFast(1, i);
                    System.arraycopy(energy[i], 3, energy[i], 2, width - 3);
                }
                else if (start == width - 1) {
                    energy[i][width - 2] = BORDER_ENERGY;
                }
                else if (start == width - 2) {
                    energy[i][width - 3] = energyFast(width - 3, i);
                    energy[i][width - 2] = BORDER_ENERGY;
                }
                else {
                    energy[i][start - 1] = energyFast(start - 1, i);
                    energy[i][start] = energyFast(start, i);
                    System.arraycopy(energy[i], start + 2, energy[i], start + 1, width - start - 2);
                }
            }
        }

        width--;
    }


    // transpose picture and energy arrays
    private void transpose() {

        int[][] tPicture = new int[width][height];
        double[][] tEnergy = new double[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tPicture[i][j] = picture[j][i];
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tEnergy[i][j] = energy[j][i];
            }
        }

        picture = tPicture;
        energy = tEnergy;
        height = picture.length;
        width = picture[0].length;
        isTransposed = !isTransposed;
    }


    // verify argument to removeHorizontalSeam() is valid
    private boolean isHorizontalSeam(int[] seam) {

        if (seam.length != width())
            return false;

        if (seam[0] < 0 || seam[0] >= height())
            return false;

        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height())
                return false;
            int diff = seam[i] - seam[i - 1];
            if (diff > 1 || diff < -1)
                return false;
        }
        return true;
    }


    // verify argument to removeVerticalSeam() is valid
    private boolean isVerticalSeam(int[] seam) {

        if (seam.length != height())
            return false;

        if (seam[0] < 0 || seam[0] >= width())
            return false;

        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width())
                return false;
            int diff = seam[i] - seam[i - 1];
            if (diff > 1 || diff < -1)
                return false;
        }
        return true;
    }


    //  unit testing (optional)
    public static void main(String[] args) {

    }
}
