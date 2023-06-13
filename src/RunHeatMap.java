import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class RunHeatMap {

    public static final String INPUT_PATH = "C:\\Users\\mesutpiskin\\Desktop\\background.png";
    public static final String OUTPUT_PATH = "C:\\Users\\mesutpiskin\\Desktop\\output.png";

    public static void main(final String[] args) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            int x = (int) (Math.random() * 200);
            int y = (int) (Math.random() * 200);
            Point p = new Point(x,y);
            points.add(p);
        }

        BufferedImage input = loadImage(INPUT_PATH);
        final JHeatMap myMap = new JHeatMap(points, input);
        
        saveImage(myMap.createHeatMap(0.3f), OUTPUT_PATH);
    }

    public static BufferedImage loadImage(String ref) {
        BufferedImage b1 = null;
        try {
            b1 = ImageIO.read(new File(ref));
        } catch (final IOException e) {
            System.out.println("error loading the image: " + ref + " : " + e);
        }
        return b1;
    }

    private static void saveImage(final BufferedImage buff, final String dest) {
        try {
            final File outputfile = new File(dest);
            ImageIO.write(buff, "png", outputfile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
