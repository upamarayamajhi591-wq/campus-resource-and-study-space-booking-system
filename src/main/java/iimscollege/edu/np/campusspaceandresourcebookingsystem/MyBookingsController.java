package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MyBookingsController {

    @FXML private TableView<SystemBackendModule.Booking> bookingTable;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colId;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colResId;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colDate;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colPurpose;
    @FXML private TableColumn<SystemBackendModule.Booking, String> colStatus;

    public void initSession(SystemBackendModule.SystemBackendService service, SystemBackendModule.User user) {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colResId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResourceId()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBookingDate()));
        colPurpose.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPurpose()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().toString()));

        bookingTable.setItems(FXCollections.observableArrayList(service.getUserBookings(user.getUsername())));
    }
}