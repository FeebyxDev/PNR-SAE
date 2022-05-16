package fr.fbyx.controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import fr.fbyx.App;
import fr.fbyx.Notification;
import fr.fbyx.model.MysqlConnect;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    private Stage stage;
    private ParentController parentController;

    @FXML
    private MFXButton loginBtn;

    @FXML
    private MFXTextField userField;

    @FXML
    private MFXPasswordField passField;


    public LoginController() {
        this.stage = App.getStage();
        this.parentController = App.getParentController();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginBtn.setOnMouseClicked(event -> {
            System.out.println("LoginPress");

            MysqlConnect connect = new MysqlConnect();
            App.setMysqlConncetion(connect);
            connect.setProperties(userField.getText(), passField.getText());
            Connection con = connect.connect();
            if(con != null) {
                parentController.setContent("fxml/Main.fxml");
            } else {
                System.out.println("Connection failed");
                MFXNotificationSystem.instance()
                    .setPosition(NotificationPos.TOP_RIGHT)
                    .publish(new Notification("Connection échouée", "Le nom d'utilisateur ou le mot de passe n'est pas valide !", "error"));
            }

        });
    }
    
}
