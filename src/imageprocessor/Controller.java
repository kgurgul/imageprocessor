package imageprocessor;

import imageprocessor.effects.Effects;
import imageprocessor.effects.ImageHistogram;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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

    @FXML
    private Button mostCommonColorButton;

    @FXML
    private Button histogramButton;

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

    public void handleMostCommonColor(ActionEvent event) {
        colorsDistributionService.setImage(imageView.snapshot(new SnapshotParameters(), null));
        colorsDistributionService.setOnSucceeded((e) -> {
            colorsMap = colorsDistributionService.getValue();
            startStageWithMostCommonColor();
        });

        colorsDistributionService.restart();
    }

    public void handleHistogram(ActionEvent event) {
        Stage stage = new Stage();
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 550, 400);
        scene.getStylesheets().add("charts.css");

        // Create chart
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String, Number> chartHistogram
                = new LineChart<>(xAxis, yAxis);
        chartHistogram.setCreateSymbols(false);

        chartHistogram.getData().clear();

        ImageHistogram imageHistogram = new ImageHistogram(imageView.snapshot(new SnapshotParameters(), null));
        if (imageHistogram.isSuccess()) {
            chartHistogram.getData().addAll(
                    // imageHistogram.getSeriesAlpha(),
                    imageHistogram.getSeriesRed(),
                    imageHistogram.getSeriesGreen(),
                    imageHistogram.getSeriesBlue());
        }

        pane.getChildren().add(chartHistogram);
        stage.setScene(scene);
        stage.setTitle("Histogram");
        stage.setResizable(false);
        stage.show();
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
        //scrollImagePane.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
    }

    /**
     * Configure base ImageView settings
     */
    private void configureImageView() {
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setImage(new Image("image_placeholder.png"));
/*        imageView.translateXProperty().bind(scrollImagePane.widthProperty().subtract(imageView.getBoundsInParent().getWidth()).divide(2));
        imageView.translateYProperty().bind(scrollImagePane.heightProperty().subtract(imageView.getBoundsInParent().getHeight()).divide(2));*/
        imageView.setTranslateX(180);
        imageView.setTranslateY(150);
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
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        imageView.setImage(image);

        unlockControls();
    }

    /**
     * Unlock all functions after loading image
     */
    private void unlockControls() {
        effectsChoiceBox.setDisable(false);
        colorsChartsButton.setDisable(false);
        mostCommonColorButton.setDisable(false);
        histogramButton.setDisable(false);
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
        Scene scene = new Scene(box, 200, 200);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Rozkład kolorów");
        box.getChildren().addAll(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.setItems(colorAmountObservableList);

        listView.setCellFactory(list -> new ColorCell());

        stage.show();
    }

    private void startStageWithMostCommonColor() {
        Stage stage = new Stage();
        Pane pane = new Pane();
        Rectangle rectangle = new Rectangle(200, 200);
        Scene scene = new Scene(pane, 200, 200);
        pane.getChildren().add(rectangle);
        stage.setScene(scene);

        int[] rgb = ImageUtils.getMostCommonColor(colorsMap);
        rectangle.setFill(Color.rgb(rgb[0], rgb[1], rgb[2]));

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
        }
    }
}
