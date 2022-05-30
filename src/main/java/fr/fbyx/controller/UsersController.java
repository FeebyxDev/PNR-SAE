package fr.fbyx.controller;

import java.io.LineNumberInputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;
import fr.fbyx.Notification;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

public class UsersController implements Initializable {
    
    private MysqlConnect connect = App.getMysqlConncetion();
    private String prefix = "£pnr";

    @FXML
    private MFXListView userList;

    @FXML
    private MFXTextField userTextField;

    @FXML
    private MFXPasswordField passTextField;

    @FXML
    private MFXButton saveUser;

    @FXML
    private MFXButton supprUser;

    @FXML
    private MFXComboBox rolesCombo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ResultSet res = connect.getConnexion().prepareStatement("SELECT * FROM vue_pnr_users;").executeQuery();
            while (res.next()) {
                userList.getItems().add(res.getString("user").split("£")[0]);
            }
            connect.disconnect();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
            ResultSet res = connect.getConnexion().prepareStatement("SELECT * FROM vue_pnr_roles;").executeQuery();
            while (res.next()) {
                rolesCombo.getItems().add(res.getString("user"));
            }

            connect.disconnect();
            rolesCombo.selectFirst();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }


        /*Model.users.forEach(user -> {
            userList.getItems().add(user.getName() + " " + user.getLastname());
        });*/

        supprUser.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (userList.getSelectionModel().getSelectedValues().get(0) != null) {
                String user = userList.getSelectionModel().getSelectedValues().get(0).toString();
                System.out.println(user);
                try {
                    connect.getConnexion().prepareStatement("DROP USER " + user + prefix + "@localhost;").execute();
                    userList.getItems().remove(user);
                    MFXNotificationSystem.instance()
                        .setPosition(NotificationPos.TOP_RIGHT)
                        .publish(new Notification("Suppression réussi", "Suppression de l'utilisateur '" + user +"' réussi !", "check"));
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                connect.disconnect();
            }
        });

        saveUser.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (userTextField.getText() != null) {
                String user = userTextField.getText();
                String pass = passTextField.getText();
                String role = rolesCombo.getSelectionModel().getSelectedItem().toString();
                // System.out.println(pass.length());
                // System.out.println(user.length());
                if(pass.length() < 5 || user.length() < 2) {
                    MFXNotificationSystem.instance()
                    .setPosition(NotificationPos.TOP_RIGHT)
                    .publish(new Notification("Problème de création", "Le mot de passe doit contenir au moins 5 caractères et le nom d'utilisateur au moins 2 caractères", "error"));
                    System.err.println("Le mot de passe doit contenir au moins 5 caractères et le nom d'utilisateur au moins 2 caractères");
                    // throw new IllegalArgumentException("Le mot de passe doit contenir au moins 5 caractères et le nom d'utilisateur au moins 2 caractères");
                } else {
                    try {
                        connect.getConnexion().prepareStatement("CREATE USER " + user + prefix + "@localhost IDENTIFIED BY '" + pass + "';").execute();
                        connect.getConnexion().prepareStatement("GRANT " + role + " TO " + user + prefix + "@localhost;").execute();
                        connect.getConnexion().prepareStatement("SET DEFAULT ROLE " + role + " TO " + user + prefix + "@localhost;").execute();
                        userList.getItems().add(user);
                        MFXNotificationSystem.instance()
                            .setPosition(NotificationPos.TOP_RIGHT)
                            .publish(new Notification("Création réussi", "Création de l'utilisateur '" + user +"' réussi !", "check"));
                        userTextField.clear();
                        passTextField.clear();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        System.err.println("User already exists");
                        MFXNotificationSystem.instance()
                            .setPosition(NotificationPos.TOP_RIGHT)
                            .publish(new Notification("Problème de création", " Erreur lors de la création, utilisateur potentiellement existant", "error"));
                    }
                    connect.disconnect();
                }
            }
        });

        userList.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            
            userTextField.setText(userList.getSelectionModel().getSelectedValues().get(0).toString());
        });
    }

}
