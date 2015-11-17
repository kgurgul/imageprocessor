package imageprocessor.services;

import imageprocessor.utils.ImageUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 * Created by kgurgul on 2015-11-17.
 */
public class CountColorsService extends Service<Integer> {

    private Image image;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            protected Integer call() {
                return ImageUtils.getColorsAmount(image);
            }
        };
    }
}
