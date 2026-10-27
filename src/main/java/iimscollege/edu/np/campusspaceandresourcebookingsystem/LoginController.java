package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;
    @FXML private Label errorLabel;

    private SystemBackendModule.SystemBackendService backendService;

    public void initService(SystemBackendModule.SystemBackendService service) {
        this.backendService = service;
    }

    @FXML
    private void handleLogin() {
        String user = usernameInput.getText().trim();
        String pass = passwordInput.getText().trim();

        try {
            SystemBackendModule.User loggedUser = backendService.login(user, pass);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-layout.fxml"));
            Parent root = loader.load();

            MainLayoutController mainCtrl = loader.getController();
            mainCtrl.initSession(backendService, loggedUser);

            Stage stage = (Stage) usernameInput.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Campus System - " + loggedUser.getUsername());
            stage.centerOnScreen();

        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleOpenRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register-view.fxml"));
            Parent root = loader.load();

            RegisterController regCtrl = loader.getController();
            regCtrl.initService(backendService);

            Stage stage = new Stage();
            stage.setTitle("Create Account");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}