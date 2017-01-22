import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class RunHeatMap {

    public static void main(final String[] args) {
        List<Point> points = new ArrayList<Point>();

        for (int i = 0; i < 1000; i++) {
            int x = (int) (Math.random() * 200);
            int y = (int) (Math.random() * 200);
            Point p = new Point(x,y);
            points.add(p);
        }
        BufferedImage input = null;
        final String originalImage = "C:\\Users\\mesutpiskin\\Desktop\\background.png";
        input = loadImage(originalImage);
        final HeatMap myMap = new HeatMap(points, input);
        
        saveImage(myMap.createHeatMap(0.3f),"C:\\Users\\mesutpiskin\\Desktop\\output.png");
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
            System.out.println(e);
        }
    }
}
