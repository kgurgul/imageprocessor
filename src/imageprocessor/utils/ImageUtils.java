package imageprocessor.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kgurgul on 2015-11-15.
 */
public class ImageUtils {

    public static int getColorsAmount(File file) {
        Set<Integer> colors = new HashSet<>();
        try {
            BufferedImage image = ImageIO.read(file);
            int w = image.getWidth();
            int h = image.getHeight();
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int pixel = image.getRGB(x, y);
                    colors.add(pixel);
                }
            }
        } catch (IOException e) {
            System.out.println("Error with colors counting");
            e.printStackTrace();
        }

        return colors.size();
    }
}
