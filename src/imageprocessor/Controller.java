package imageprocessor;

import imageprocessor.effects.Effects;
import imageprocessor.model.ColorAmount;
import imageprocessor.services.ColorsDistributionService;
import imageprocessor.services.CountColorsService;
import imageprocessor.utils.ImageUtils;
import imageprocessor.utils.Utils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
    private Map<Integer, Integer> colorsMap;

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
        colorsDistributionService.setOnSucceeded((e) -> {
            colorsMap = colorsDistributionService.getValue();
            startStageWithChart();
        });

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

    private void startStageWithChart() {
        List<ColorAmount> colorAmountList = Utils.convertMapToColorObjects(colorsMap);
        ObservableList<ColorAmount> colorAmountObservableList = FXCollections.observableArrayList(colorAmountList);

        ListView<ColorAmount> listView = new ListView<>();
        VBox box = new VBox();
        Scene scene = new Scene(box, 200, 400);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Rozkład kolorów");
        box.getChildren().addAll(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.setItems(colorAmountObservableList);

        listView.setCellFactory(list -> new ColorCell());

        stage.show();
    }


    static class ColorCell extends ListCell<ColorAmount> {
        HBox hbox = new HBox();
        Rectangle rectangle = new Rectangle(20, 20);
        Label label = new Label();
        Pane pane = new Pane();
        ColorAmount lastItem;

        public ColorCell() {
            super();
            hbox.getChildren().addAll(rectangle, label, pane);
            HBox.setHgrow(pane, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(ColorAmount item, boolean empty) {
            super.updateItem(item, empty);

            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;

                int[] rgb = ImageUtils.getRGBFromInteger(item.integerColor);
                rectangle.setFill(Color.rgb(rgb[0], rgb[1], rgb[2]));
                label.setText(" - " + item.amount);

                setGraphic(hbox);
            }

          /*  Rectangle rect = new Rectangle(20, 20);
            if (item != null) {
                int[] rgb = ImageUtils.getRGBFromInteger(item.integerColor);
                rect.setFill(Color.rgb(rgb[0], rgb[1], rgb[2]));
                setGraphic(rect);
            }*/
        }
    }
}
