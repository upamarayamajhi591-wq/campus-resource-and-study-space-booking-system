package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;
    @FXML private ComboBox<String> roleSelector;
    @FXML private Label errorLabel;

    private SystemBackendModule.SystemBackendService backendService;

    @FXML
    private void initialize() {
        roleSelector.getItems().addAll("STUDENT", "STAFF");
        roleSelector.setValue("STUDENT");
    }

    public void initService(SystemBackendModule.SystemBackendService service) {
        this.backendService = service;
    }

    @FXML
    private void handleRegister() {
        String u = usernameInput.getText().trim();
        String p = passwordInput.getText().trim();
        String r = roleSelector.getValue();

        if (u.isEmpty() || p.isEmpty()) {
            errorLabel.setText("All fields are required.");
            errorLabel.setVisible(true);
            return;
        }

        try {
            SystemBackendModule.Role role = SystemBackendModule.Role.valueOf(r);
            backendService.registerUser(new SystemBackendModule.User(u, p, role));

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful!", ButtonType.OK);
            alert.showAndWait();

            Stage stage = (Stage) usernameInput.getScene().getWindow();
            stage.close();
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
            errorLabel.setVisible(true);
        }
    }
}