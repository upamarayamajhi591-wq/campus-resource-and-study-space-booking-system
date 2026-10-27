package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainLayoutController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;
    @FXML private Button btnResources;
    @FXML private Button btnMyBookings;
    @FXML private Button btnAdmin;

    private SystemBackendModule.SystemBackendService backendService;
    private SystemBackendModule.User currentUser;

    public void initSession(SystemBackendModule.SystemBackendService service, SystemBackendModule.User user) {
        this.backendService = service;
        this.currentUser = user;
        userLabel.setText(user.getUsername() + " (" + user.getRole() + ")");

        if (user.getRole() != SystemBackendModule.Role.ADMIN) {
            btnAdmin.setVisible(false);
            btnAdmin.setManaged(false);
        }

        showResources();
    }

    @FXML
    public void showResources() {
        setActive(btnResources);
        loadPage("available-resources.fxml", (loader) -> {
            AvailableResourcesController ctrl = loader.getController();
            ctrl.initSession(backendService, currentUser);
        });
    }

    @FXML
    public void showMyBookings() {
        setActive(btnMyBookings);
        loadPage("my-bookings.fxml", (loader) -> {
            MyBookingsController ctrl = loader.getController();
            ctrl.initSession(backendService, currentUser);
        });
    }

    @FXML
    public void showAdmin() {
        setActive(btnAdmin);
        loadPage("admin-dashboard.fxml", (loader) -> {
            AdminDashboardController ctrl = loader.getController();
            ctrl.initSession(backendService);
        });
    }

    private void setActive(Button activeBtn) {
        btnResources.setStyle("-fx-background-color: transparent;");
        btnMyBookings.setStyle("-fx-background-color: transparent;");
        btnAdmin.setStyle("-fx-background-color: transparent;");
        activeBtn.setStyle("-fx-background-color: #eaeaea; -fx-font-weight: bold;");
    }

    private void loadPage(String fxml, PageInitializer init) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node node = loader.load();
            if (init != null) init.apply(loader);
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            backendService.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) userLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            LoginController controller = loader.getController();
            controller.initService(backendService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface PageInitializer {
        void apply(FXMLLoader loader);
    }
}