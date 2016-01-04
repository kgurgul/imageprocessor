package imageprocessor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void init() throws Exception {
        super.init();
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_view.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Image processor");
        primaryStage.setScene(new Scene(root, 800, 600));
        //primaryStage.setResizable(false);
        primaryStage.show();

        Controller controller = loader.getController();
        controller.setStageAndSetupListeners(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
