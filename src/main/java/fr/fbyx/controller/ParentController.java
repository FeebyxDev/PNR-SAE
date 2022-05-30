package fr.fbyx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParentController implements Initializable {

    private Stage stage;
	private double xOffset;
	private double yOffset;
	private final boolean isDarkTheme;

    @FXML
    private MFXFontIcon logoutIcon;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;

    @FXML
    private MFXFontIcon toggleSize;

    @FXML
	public AnchorPane rootPane;

    @FXML
	public StackPane parentPane;

    @FXML
	private HBox windowHeader;


    public ParentController(Stage stage) {
        this.stage = stage;
		this.isDarkTheme = App.isDarkTheme();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if(isDarkTheme) {
			System.out.println("Dark theme Mode activated");
			
		}

        logoutIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            App.getMysqlConncetion().disconnect();
            App.rootapp.restartApp();
        });

        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));

		toggleSize.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			boolean newVal = !stage.isMaximized();
			toggleSize.pseudoClassStateChanged(PseudoClass.getPseudoClass("maximized"), newVal);
			stage.setMaximized(newVal);
		});

        windowHeader.setOnMousePressed(event -> {
			xOffset = stage.getX() - event.getScreenX();
			yOffset = stage.getY() - event.getScreenY();
		});
		windowHeader.setOnMouseDragged(event -> {
			if(stage.isMaximized()) {
				toggleSize.pseudoClassStateChanged(PseudoClass.getPseudoClass("maximized"), false);
				stage.setMaximized(false);
			}
			stage.setX(event.getScreenX() + xOffset);
			stage.setY(event.getScreenY() + yOffset);
		});

        try {
            parentPane.getChildren().setAll((VBox) FXMLLoader.load(App.loadURL("fxml/Login.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setContent(String fxml) {
        try {
            parentPane.getChildren().setAll((AnchorPane) FXMLLoader.load(App.loadURL(fxml)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
