package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class AdminDashboardController {

    @FXML private TableView<SystemBackendModule.Booking> bookingTable;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colId;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colUser;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colResId;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colDate;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colStatus;
    @FXML private TableColumn<SystemBackendModule.Booking, Void> colActions;
    @FXML private ListView<String> logListView;

    private SystemBackendModule.SystemBackendService backendService;

    public void initSession(SystemBackendModule.SystemBackendService service) {
        this.backendService = service;
        setupTable();
        refresh();
    }

    private void setupTable() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colResId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResourceId()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBookingDate()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().toString()));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Approve");
            private final Button btnDisapprove = new Button("Disapprove");
            private final HBox container = new HBox(8, btnApprove, btnDisapprove);

            {
                btnApprove.setStyle("-fx-background-color: #000; -fx-text-fill: #fff;");
                btnDisapprove.setStyle("-fx-background-color: #ba1a1a; -fx-text-fill: #fff;");

                btnApprove.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()).getId(), SystemBackendModule.BookingStatus.APPROVED));
                btnDisapprove.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()).getId(), SystemBackendModule.BookingStatus.DISAPPROVED));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    SystemBackendModule.Booking booking = getTableView().getItems().get(getIndex());
                    boolean pending = booking.getStatus() == SystemBackendModule.BookingStatus.PENDING;

                    btnApprove.setDisable(!pending);
                    btnDisapprove.setDisable(!pending);

                    setGraphic(container);
                }
            }
        });
    }

    private void updateStatus(String id, SystemBackendModule.BookingStatus status) {
        try {
            backendService.updateBookingStatus(id, status);
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        bookingTable.setItems(FXCollections.observableArrayList(backendService.getAllBookings()));
        logListView.setItems(FXCollections.observableArrayList(backendService.getSystemLogs()));

        // The Booking objects are mutated in place (same reference), so JavaFX may not
        // re-run updateItem() on already-rendered rows just because setItems() was called
        // with a new list. Force it, so the Approve/Disapprove cell actually re-evaluates
        // and disables itself once a booking is no longer PENDING.
        bookingTable.refresh();
    }
}