package fr.fbyx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import fr.fbyx.App;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    private Stage stage;

    @FXML
    private MFXButton loginBtn;


    public LoginController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginBtn.setOnMouseClicked(event -> {
            try {
                stage.setScene(new Scene((App.loadFXML("Main")), 1280, 720));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
}
