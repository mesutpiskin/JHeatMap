# JHeatMap

![jAVA](https://img.shields.io/badge/Language-Java-red.svg)

**EN:** A heat map is a two-dimensional representation of data in which values are represented by colors. A simple heat map provides an immediate visual summary of information. More elaborate heat maps allow the viewer to understand complex data sets. Heat map generation with java. The RunHeatMap class demonstrates how to use it. For this class (JHeatMap), list and background image are required as parameters. Set the parameters and run this class (RunHeatMap), that's all.

**TR:** Java ile yoğunluk haritası oluşturma. RunHeatMap sınıfı nasıl kullanacağını gösterir. Bu sınıf (JHeatMap) için parametre olarak liste ve arka plan resmi gereklidir. Parametreleri ayarla ve bu sınıfı (RunHeatMap) çalıştır, hepsi bu kadar.

## Usage

```java
public static void generateHeatmap() {
    //Generate heatmap 2d point (x,y) data
    List<Point> points = new ArrayList<Point>(); 
    for (int i = 0; i < 1000; i++) {
        int x = (int) (Math.random() * 200);
        int y = (int) (Math.random() * 200);
        Point p = new Point(x,y);
        points.add(p);
    }
    BufferedImage input = null;
    //import heatmap background image
    final String originalImage = "heatmapBackground.png";
    input = loadImage(originalImage);
    //Create a heatmap from the data and add it to the background
    final HeatMap myMap = new HeatMap(points, input);
    //Export heatmap image
    saveImage(myMap.createHeatMap(0.3f),"heatmap.png");
}
```

## Results

![heatmap](http://i.imgur.com/W0YCvkM.png)

![heatmap](http://mesutpiskin.com/blog/wp-content/uploads/2018/04/heatmap-640x480.jpeg)

## Thanks

Big thanks to [software-talk.org](https://software-talk.org) for their help !
