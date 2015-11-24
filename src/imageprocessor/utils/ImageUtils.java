package imageprocessor.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by kgurgul on 2015-11-15.
 */
public class ImageUtils {

    public static int getColorsAmount(Image image) {
        Set<Integer> colors = new HashSet<>();

        BufferedImage img = SwingFXUtils.fromFXImage(image, null);
        int w = img.getWidth();
        int h = img.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = img.getRGB(x, y);
                colors.add(pixel);
            }
        }

        return colors.size();
    }

    public static int[] getRGBFromInteger(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red, green, blue};
    }

    public static int[] getMostCommonColor(Map<Integer, Integer> map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));
        Map.Entry me = (Map.Entry) list.get(list.size() - 1);
        return ImageUtils.getRGBFromInteger((Integer) me.getKey());
    }
}
