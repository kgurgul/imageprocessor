package imageprocessor.tasks;

import imageprocessor.utils.ImageUtils;
import javafx.concurrent.Task;

import java.io.File;

/**
 * Created by kgurgul on 2015-11-15.
 */
public class CountColorsTask extends Task<Integer> {

    private File imageFile;

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    protected Integer call() throws Exception {
        return ImageUtils.getColorsAmount(imageFile);
    }
}
