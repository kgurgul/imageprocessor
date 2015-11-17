package imageprocessor;

import imageprocessor.effects.Effects;
import imageprocessor.services.CountColorsService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ScrollPane scrollImagePane;

    @FXML
    private ImageView imageView;

    @FXML
    private Label colorAmountLabel;

    @FXML
    private ChoiceBox effectsChoiceBox;

    private Stage stage;
    private File currentFile;
    private CountColorsService countColorsService = new CountColorsService();

    private Effects effects;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureImagePane();
        configureImageView();
        configureChoiceBox();
    }

    /**
     * Add possibility to control stage from Controller and init base tasks
     *
     * @param stage main stage
     */
    public void setStageAndSetupListeners(Stage stage) {
        this.stage = stage;
        configureServices();
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

    private void configureImagePane() {
        scrollImagePane.setStyle("-fx-background-color:transparent;");
    }

    /**
     * Configure base ImageView settings
     */
    private void configureImageView() {
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
    }

    private void configureChoiceBox() {
        effects = new Effects();
        effectsChoiceBox.setItems(effects.getEffectsList());
        effectsChoiceBox.getSelectionModel().selectFirst();
        effectsChoiceBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> observable,
                              Number oldValue, Number newValue) -> {
                    imageView.setEffect(effects.getEffect(newValue.intValue()));
                    loadStatisticsInBackground();
                });
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
                new FileChooser.ExtensionFilter("JPG, PNG", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );
    }

    /**
     * Bind tasks with UI wait cursor
     */
    private void configureServices() {
        stage.getScene()
                .getRoot()
                .cursorProperty()
                .bind(Bindings
                        .when(countColorsService.runningProperty())
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

        unlockControls();
    }

    private void unlockControls() {
        effectsChoiceBox.setDisable(false);
    }

    /**
     * Count colors amount in background
     */
    private void loadStatisticsInBackground() {
        countColorsService.setImage(imageView.snapshot(new SnapshotParameters(), null));
        countColorsService.setOnSucceeded((event) -> colorAmountLabel.setText(String.valueOf(countColorsService.getValue())));

        countColorsService.restart();
    }
}
