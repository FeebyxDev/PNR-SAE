package fr.fbyx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import fr.fbyx.App;
import fr.fbyx.ExportData;
import fr.fbyx.Notification;
import fr.fbyx.model.Animal;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.notifications.base.INotification;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeController implements Initializable {
    
    @FXML
    private MFXButton searchButton;

    @FXML
    private MFXButton insertButton;

    @FXML
	private StackPane contentPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        searchButton.setOnMouseClicked(event -> {
            ExportData.export("SELECT * FROM Chouette");
            /* VBox newVBox;
            try {
                newVBox = FXMLLoader.load(App.loadURL("fxml/Search.fxml"));
                App.getMainController().getContentPane().getChildren().setAll(newVBox);
            } catch (IOException e) {
                e.printStackTrace();
            } */
        });

        insertButton.setOnMouseClicked(event -> {
            VBox newVBox;
            try {
                newVBox = FXMLLoader.load(App.loadURL("fxml/NvObs.fxml"));
                App.getMainController().getContentPane().getChildren().setAll(newVBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }    

}
