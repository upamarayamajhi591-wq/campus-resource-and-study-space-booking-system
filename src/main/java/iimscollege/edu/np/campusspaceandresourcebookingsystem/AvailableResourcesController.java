package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AvailableResourcesController {

    @FXML private TableView<SystemBackendModule.Resource> resourceTable;
    @FXML private TableColumn<SystemBackendModule.Resource, String> colId;
    @FXML private TableColumn<SystemBackendModule.Resource, String> colName;
    @FXML private TableColumn<SystemBackendModule.Resource, String> colType;
    @FXML private TableColumn<SystemBackendModule.Resource, String> colLocation;
    @FXML private TableColumn<SystemBackendModule.Resource, String> colStatus;
    @FXML private TableColumn<SystemBackendModule.Resource, Void> colAction;

    private SystemBackendModule.SystemBackendService backendService;
    private SystemBackendModule.User currentUser;

    public void initSession(SystemBackendModule.SystemBackendService service, SystemBackendModule.User user) {
        this.backendService = service;
        this.currentUser = user;
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType().toString()));
        colLocation.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isAvailable() ? "AVAILABLE" : "UNAVAILABLE"));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Book");
            {
                btn.setStyle("-fx-background-color: #000; -fx-text-fill: #fff; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    SystemBackendModule.Resource res = getTableView().getItems().get(getIndex());
                    openBookingModal(res);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    SystemBackendModule.Resource res = getTableView().getItems().get(getIndex());
                    btn.setDisable(!res.isAvailable());
                    setGraphic(btn);
                }
            }
        });
    }

    private void loadData() {
        if (backendService != null) {
            resourceTable.setItems(FXCollections.observableArrayList(backendService.getAllResources()));
        }
    }

    private void openBookingModal(SystemBackendModule.Resource res) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("booking-view.fxml"));
            Parent root = loader.load();

            BookingController ctrl = loader.getController();
            ctrl.initSession(backendService, currentUser, res);

            Stage stage = new Stage();
            stage.setTitle("New Booking");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}