module iimscollege.edu.np.campusspaceandresourcebookingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.compiler;


    opens iimscollege.edu.np.campusspaceandresourcebookingsystem to javafx.fxml;
    exports iimscollege.edu.np.campusspaceandresourcebookingsystem;
}