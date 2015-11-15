package imageprocessor;

import imageprocessor.tasks.CountColorsTask;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane imageViewContainer;

    @FXML
    private ImageView imageView;

    @FXML
    private Label colorAmountLabel;

    private Stage stage;
    private File currentFile;
    private CountColorsTask countColorsTask = new CountColorsTask();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureImageView();
    }

    /**
     * Add possibility to control stage from Controller and init base tasks
     *
     * @param stage main stage
     */
    public void setStageAndSetupListeners(Stage stage) {
        this.stage = stage;
        configureTasks();
    }

    /**
     * Handle image opening
     *
     * @param event
     */
    public void handleOpenImageAction(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openImage(file);
            loadStatisticsInBackground();
        }
    }

    /**
     * Handle close application action
     *
     * @param event
     */
    public void handleCloseAction(ActionEvent event) {
        Platform.exit();
    }

    /**
     * @param event
     */
    public void handleCountColors(ActionEvent event) {
        if (currentFile != null) {

        }
    }

    /**
     * Configure base ImageView settings
     */
    private void configureImageView() {
        imageView.fitWidthProperty().bind(imageViewContainer.widthProperty());
        imageView.fitHeightProperty().bind(imageViewContainer.heightProperty());
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
    }

    /**
     * Configure FileChooser types
     *
     * @param fileChooser
     */
    private void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Wybierz zdjęcie");

        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );
    }

    /**
     * Bind tasks with UI wait cursor
     */
    private void configureTasks() {
        stage.getScene()
                .getRoot()
                .cursorProperty()
                .bind(Bindings
                        .when(countColorsTask.runningProperty())
                        .then(Cursor.WAIT)
                        .otherwise(Cursor.DEFAULT)
                );
    }

    /**
     * Open image from file
     *
     * @param file image file
     */
    private void openImage(File file) {
        System.out.println(file.getName() + " opened");

        this.currentFile = file;
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
    }

    /**
     * Count colors amount in background
     */
    private void loadStatisticsInBackground() {
        countColorsTask.setImageFile(currentFile);
        countColorsTask.setOnSucceeded((event) -> colorAmountLabel.setText(String.valueOf(countColorsTask.getValue())));

        new Thread(countColorsTask).start();
    }
}
