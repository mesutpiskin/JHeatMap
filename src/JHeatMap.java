import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;


public class JHeatMap {
    private static final int HALFCIRCLEPICSIZE = 32;
    private static final String CIRCLEPIC = "bolilla.png";
    private static final String SPECTRUMPIC = "colors.png";
    private Map<Integer, List<Point>> map;
    private int maxOccurance = 1;
    private int maxXValue;
    private int maxYValue;
    private final BufferedImage lvlMap;

    public HeatMap(final List<Point> points,BufferedImage lvlMap) {
        this.lvlMap = lvlMap;
        initMap(points);
    }
    private void initMap(final List<Point> points) {
        map = new HashMap<Integer, List<Point>>();
        final BufferedImage mapPic = lvlMap;
        maxXValue = mapPic.getWidth();
        maxYValue = mapPic.getHeight();

        final int pointSize = points.size();
        for (int i = 0; i < pointSize; i++) {
            final Point point = points.get(i);
            // add point to correct list.
            final int hash = getkey(point);
            if (map.containsKey(hash)) {
                final List<Point> thisList = map.get(hash);
                thisList.add(point);
                if (thisList.size() > maxOccurance) {
                    maxOccurance = thisList.size();
                }
                // if list did not exist, create new one and add point.
            } else {
                final List<Point> newList = new LinkedList<Point>();
                newList.add(point);
                map.put(hash, newList);
            }
        }
    }

    /**
     * creates the heatmap.
     *
     * @param multiplier calculated opacity of every point will be multiplied by
     * this value. This leads to a HeatMap that is easier to read, especially
     * when there are not too many points or the points are too spread out. Pass
     * 1.0f for original.
     */
    public BufferedImage createHeatMap(final float multiplier) {

        final BufferedImage circle = loadImage(CIRCLEPIC);
        BufferedImage heatMap = new BufferedImage(maxXValue, maxYValue, 6);
        paintInColor(heatMap, Color.white);

        final Iterator<List<Point>> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            final List<Point> currentPoints = iterator.next();

            // calculate opaqueness
            // based on number of occurences of current point
            float opaque = currentPoints.size() / (float) maxOccurance;

            // adjust opacity so the heatmap is easier to read
            opaque = opaque * multiplier;
            if (opaque > 1) {
                opaque = 1;
            }

            final Point currentPoint = currentPoints.get(0);

            // draw a circle which gets transparent from middle to outside
            // (which opaqueness is set to "opaque")
            // at the position specified by the center of the currentPoint
            addImage(heatMap, circle, opaque,
                    (currentPoint.x - HALFCIRCLEPICSIZE),
                    (currentPoint.y - HALFCIRCLEPICSIZE));
        }
        // negate the image
        heatMap = negateImage(heatMap);

        // remap black/white with color spectrum from white over red, orange,
        // yellow, green to blue
        remap(heatMap);

        // blend image over lvlMap at opacity 40%
        final BufferedImage output = lvlMap;
        addImage(output, heatMap, 0.4f);

        // save image
        return output;

    }

    /**
     * remaps black and white picture with colors. It uses the colors from
     * SPECTRUMPIC. The whiter a pixel is, the more it will get a color from the
     * bottom of it. Black will stay black.
     *
     * @param heatMapBW black and white heat map
     */
    private void remap(final BufferedImage heatMapBW) {
        final BufferedImage colorGradiant = loadImage(SPECTRUMPIC);
        final int width = heatMapBW.getWidth();
        final int height = heatMapBW.getHeight();
        final int gradientHight = colorGradiant.getHeight() - 1;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                // get heatMapBW color values:
                final int rGB = heatMapBW.getRGB(i, j);

                // calculate multiplier to be applied to height of gradiant.
                float multiplier = rGB & 0xff; // blue
                multiplier *= ((rGB >>> 8)) & 0xff; // green
                multiplier *= (rGB >>> 16) & 0xff; // red
                multiplier /= 16581375; // 255f * 255f * 255f

                // apply multiplier
                final int y = (int) (multiplier * gradientHight);

                // remap values
                // calculate new value based on whitenes of heatMap
                // (the whiter, the more a color from the top of colorGradiant
                // will be chosen.
                final int mapedRGB = colorGradiant.getRGB(0, y);
                // set new value
                heatMapBW.setRGB(i, j, mapedRGB);
            }
        }
    }

    /**
     * returns a negated version of this image.
     *
     * @param img buffer to negate
     * @return negated buffer
     */
    private BufferedImage negateImage(final BufferedImage img) {
        final int width = img.getWidth();
        final int height = img.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                final int rGB = img.getRGB(x, y);

                // Swaps values
                // i.e. 255, 255, 255 (white)
                // becomes 0, 0, 0 (black)
                final int r = Math.abs(((rGB >>> 16) & 0xff) - 255); // red
                // inverted
                final int g = Math.abs(((rGB >>> 8) & 0xff) - 255); // green
                // inverted
                final int b = Math.abs((rGB & 0xff) - 255); // blue inverted

                // transform back to pixel value and set it
                img.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return img;
    }

    /**
     * changes all pixel in the buffer to the provided color.
     *
     * @param buff buffer
     * @param color color
     */
    private void paintInColor(final BufferedImage buff, final Color color) {
        final Graphics2D g2 = buff.createGraphics();
        g2.setColor(color);
        g2.fillRect(0, 0, buff.getWidth(), buff.getHeight());
        g2.dispose();
    }

    /**
     * changes the opacity of the image.
     *
     * @param buff1 buffer to change opacity
     * @param opaque new opacity
     */
    private void makeTransparent(final BufferedImage buff1, final float opaque) {
        final Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, opaque));
        g2d.drawImage(buff1, 0, 0, null);
        g2d.dispose();
    }

    /**
     * prints the contents of buff2 on buff1 with the given opaque value
     * starting at position 0, 0.
     *
     * @param buff1 buffer
     * @param buff2 buffer to add to buff1
     * @param opaque opacity
     */
    private void addImage(final BufferedImage buff1, final BufferedImage buff2,
            final float opaque) {
        addImage(buff1, buff2, opaque, 0, 0);
    }

    /**
     * prints the contents of buff2 on buff1 with the given opaque value.
     *
     * @param buff1 buffer
     * @param buff2 buffer
     * @param opaque how opaque the second buffer should be drawn
     * @param x x position where the second buffer should be drawn
     * @param y y position where the second buffer should be drawn
     */
    private void addImage(final BufferedImage buff1, final BufferedImage buff2,
            final float opaque, final int x, final int y) {
        final Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                opaque));
        g2d.drawImage(buff2, x, y, null);
        g2d.dispose();
    }

    /**
     * saves the image in the provided buffer to the destination.
     *
     * @param buff buffer to be saved
     * @param dest destination to save at
     */
    /*  private void saveImage(final BufferedImage buff, final String dest) {
        try {
            final File outputfile = new File(dest);
            ImageIO.write(buff, "png", outputfile);
        } catch (final IOException e) {
            print("error saving the image: " + dest + ": " + e);
        }
    }*/
    /**
     * returns a BufferedImage from the Image provided.
     *
     * @param ref path to image
     * @return loaded image
     */
    private BufferedImage loadImage(final String ref) {
        BufferedImage b1 = null;
        try {
            b1 = ImageIO.read(new File(ref));
        } catch (final IOException e) {
            System.out.println("error loading the image: " + ref + " : " + e);
        }
        return b1;
    }

    /**
     * returns a hash calculated by the given point.
     *
     * @param p a point
     * @return hash value
     */
    private int getkey(final Point p) {
        return ((p.x << 19) | (p.y << 7));
    }

    /**
     * prints string to sto.
     *
     * @param s string to print
     */
    private void print(final String s) {
        System.out.println(s);
    }
}
