package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BookingController {

    @FXML private TextField resourceIdInput;
    @FXML private TextField dateInput;
    @FXML private TextArea purposeInput;

    private SystemBackendModule.SystemBackendService backendService;
    private SystemBackendModule.Resource selectedResource;

    public void initSession(SystemBackendModule.SystemBackendService service, SystemBackendModule.User user, SystemBackendModule.Resource resource) {
        this.backendService = service;
        this.selectedResource = resource;
        resourceIdInput.setText(resource.getId() + " - " + resource.getName());
    }

    @FXML
    private void handleSubmit() {
        String date = dateInput.getText().trim();
        String purpose = purposeInput.getText().trim();

        if (date.isEmpty() || purpose.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            backendService.createBooking(selectedResource.getId(), purpose, date);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Booking submitted successfully!", ButtonType.OK);
            alert.showAndWait();

            Stage stage = (Stage) resourceIdInput.getScene().getWindow();
            stage.close();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }
}