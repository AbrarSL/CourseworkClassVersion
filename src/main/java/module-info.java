module com.github.abrarsl.courseworkclassversion {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.github.abrarsl.courseworkclassversion to javafx.fxml;
    exports com.github.abrarsl.courseworkclassversion;
}