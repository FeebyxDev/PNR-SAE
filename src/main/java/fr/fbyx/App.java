package fr.fbyx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

import fr.fbyx.controller.*;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;

/**
 * JavaFX App
 */
public class App extends Application {
    
    private static Scene scene;
    private static Stage stage;
    private static boolean isDarkTheme = true;
    private static ParentController parentController;
    private static LoginController logController;
    private static MainController mainController;
    private static MysqlConnect mysqlConncetion;
    public static App rootapp;
    private static boolean isAdmin = false;
    
    public static void launApp() {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.rootapp = this;
        Platform.runLater(() -> {
			MFXNotificationSystem.instance().initOwner(App.getStage());
		});
        this.stage = stage;
        
        this.parentController = new ParentController(stage);
        // this.mainController = new MainController(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/" + "Parent" + ".fxml"));
        // FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/" + "Main" + ".fxml"));

        fxmlLoader.setControllerFactory(c -> this.parentController);
        // fxmlLoader.setControllerFactory(c -> this.mainController);

        Parent root = fxmlLoader.load();
        scene = new Scene((root), 1650, 900);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Parc Naturel Régional");
        stage.setScene(scene);
        ResizeHelper.addResizeListener(stage);
        stage.show();
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static URL loadURL(String path) {
		return App.class.getResource(path);
	}

    public static Stage getStage() {
        return stage;
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static boolean isDarkTheme() {
        return isDarkTheme;
    }

    public static ParentController getParentController() {
        return parentController;
    }

    public static void setMysqlConncetion(MysqlConnect con) {
        mysqlConncetion = con;
    }

    public static MysqlConnect getMysqlConncetion() {
        return mysqlConncetion;
    }

    public static void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    // <MFXFontIcon fx:id="alwaysOnTopIcon" description="mfx-circle" size="15.0" styleClass="always-on-top-icon" />

    public void restartApp() {
        // App.getStage().close();
        App.stage.close();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    App.rootapp.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
    }

}