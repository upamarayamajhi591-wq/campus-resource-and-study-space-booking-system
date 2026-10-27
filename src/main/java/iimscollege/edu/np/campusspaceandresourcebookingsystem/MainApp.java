package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static SystemBackendModule.SystemBackendService backendService;

    @Override
    public void start(Stage stage) throws Exception {
        backendService = new SystemBackendModule.SystemBackendService();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.initService(backendService);

        stage.setTitle("University Resource Management - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}