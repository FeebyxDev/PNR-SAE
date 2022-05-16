package fr.fbyx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import fr.fbyx.model.Model;
import fr.fbyx.model.Users;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

public class UsersController implements Initializable {
    
    @FXML
    private MFXListView userList;

    @FXML
    private MFXTextField userTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.users.forEach(user -> {
            userList.getItems().add(user.getName() + " " + user.getLastname());
        });

        userList.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            
            userTextField.setText(userList.getSelectionModel().getSelectedValues().get(0).toString());
        });
    }

}
