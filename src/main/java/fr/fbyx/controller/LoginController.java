package fr.fbyx.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;
import fr.fbyx.Notification;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    private Stage stage;
    private ParentController parentController;
    private MysqlConnect connect = new MysqlConnect();

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
        App.setMysqlConncetion(connect);

        passField.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ENTER) {
                System.out.println("Enter Pressed");
                openApp();
            }
        });

        loginBtn.setOnMouseClicked(event -> {
            // System.out.println("LoginPress");
            openApp();
        });

    }

    private void openApp() {
        connect.setProperties(userField.getText()+"£pnr", passField.getText());
        Connection con = connect.getConnexion();
        if(con != null) {
            try {
                if(App.isAdmin()) App.setAdmin(false);
                ResultSet rs = con.prepareStatement("SELECT * FROM vue_admin WHERE user = '" + userField.getText()+"£pnr" + "'").executeQuery();
                if(rs.next()) App.setAdmin(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            parentController.setContent("fxml/Main.fxml");
        } else {
            System.out.println("Connection failed");
            MFXNotificationSystem.instance()
                .setPosition(NotificationPos.TOP_RIGHT)
                .publish(new Notification("Connection échouée", "Le nom d'utilisateur ou le mot de passe n'est pas valide !", "error"));
        }
    }
    
}
