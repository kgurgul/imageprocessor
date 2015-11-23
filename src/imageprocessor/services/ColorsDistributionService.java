package imageprocessor.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by kgurgul on 2015-11-23.
 */
public class ColorsDistributionService extends Service<Map<Integer, Integer>> {

    private Image image;

    @Override
    protected Task<Map<Integer, Integer>> createTask() {
        return new Task<Map<Integer, Integer>>() {
            @Override
            protected Map<Integer, Integer> call() throws Exception {
                if (image != null) {
                    return getColorsMap(SwingFXUtils.fromFXImage(image, null));
                } else {
                    return null;
                }
            }
        };
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Map<Integer, Integer> getColorsMap(BufferedImage bufferedImage) {
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        Map m = new HashMap();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = bufferedImage.getRGB(i, j);
                int[] rgbArr = getRGBArr(rgb);
                // Filter out grays....
                //     if (!isGray(rgbArr)) {
                Integer counter = (Integer) m.get(rgb);
                if (counter == null)
                    counter = 0;
                counter++;
                m.put(rgb, counter);
                //      }
            }
        }

        m = sortByValue(m);

        return m;
    }

    public String getMostCommonColour(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));
        Map.Entry me = (Map.Entry) list.get(list.size() - 1);
        int[] rgb = getRGBArr((Integer) me.getKey());
        return Integer.toHexString(rgb[0]) + " " + Integer.toHexString(rgb[1]) + " " + Integer.toHexString(rgb[2]);
    }

    public int[] getRGBArr(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red, green, blue};
    }

    public boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        return true;
    }

    public Map<Integer, Integer> sortByValue(Map<Integer, Integer> map) {
        Map<Integer, Integer> result = new LinkedHashMap<>();
        Stream<Map.Entry<Integer, Integer>> st = map.entrySet().stream();

        st.sorted((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()))
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }
}
