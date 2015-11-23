package imageprocessor;

import imageprocessor.effects.Effects;
import imageprocessor.services.ColorsDistributionService;
import imageprocessor.services.CountColorsService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
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

    @FXML
    private Button colorsChartsButton;

    private Stage stage;
    private File currentFile;
    private CountColorsService countColorsService = new CountColorsService();
    private ColorsDistributionService colorsDistributionService = new ColorsDistributionService();

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
    public void handleColorsCharts(ActionEvent event) {
        colorsDistributionService.setImage(imageView.snapshot(new SnapshotParameters(), null));
        //colorsDistributionService.setOnSucceeded((e) -> colorAmountLabel.setText(String.valueOf(countColorsService.getValue())));

        colorsDistributionService.restart();
    }

    /**
     * Open info screen
     *
     * @param event
     */
    public void handleInfoAction(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("info_view.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Image processor");
            stage.setScene(new Scene(root, 600, 400));
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

        stage.getScene()
                .getRoot()
                .cursorProperty()
                .bind(Bindings
                        .when(colorsDistributionService.runningProperty())
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

    /**
     * Unlock all functions after loading image
     */
    private void unlockControls() {
        effectsChoiceBox.setDisable(false);
        colorsChartsButton.setDisable(false);
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
