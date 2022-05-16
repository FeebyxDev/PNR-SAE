module fr.fbyx {
    requires MaterialFX;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    

    opens fr.fbyx to javafx.fxml;
    opens fr.fbyx.controller to javafx.fxml;
    exports fr.fbyx;
    exports fr.fbyx.controller;
}
