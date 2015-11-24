package imageprocessor.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

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
}
