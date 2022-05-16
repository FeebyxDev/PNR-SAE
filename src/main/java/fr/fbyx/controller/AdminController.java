package fr.fbyx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import fr.fbyx.App;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class AdminController implements Initializable {
    
    @FXML
    private MFXButton modifUsers;

    @FXML
    private StackPane contentAdmin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modifUsers.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            // System.out.println(contentAdmin.getChildren());
            try {
                StackPane newStackPane = FXMLLoader.load(App.loadURL("fxml/Users.fxml"));
                contentAdmin.getChildren().setAll(newStackPane);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
            // contentAdmin.getChildren().setAll(MFXLoaderBean.of("USERS", App.loadURL("fxml/Users.fxml")).get().getRoot());
        });
    }


    

}
