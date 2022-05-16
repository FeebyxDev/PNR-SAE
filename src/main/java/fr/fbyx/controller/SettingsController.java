package fr.fbyx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import fr.fbyx.App;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SettingsController implements Initializable {
    
    private Stage stage;

    @FXML
    private MFXToggleButton alwaysOnTopToggle;

    @FXML
    private MFXToggleButton themeToggle;

    public SettingsController() {
        stage = App.getStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        alwaysOnTopToggle.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			boolean newVal = !stage.isAlwaysOnTop();
			alwaysOnTopToggle.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
			stage.setAlwaysOnTop(newVal);
            alwaysOnTopToggle.setText(newVal ? "On" : "Off");
		});

        themeToggle.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = themeToggle.getText().equals("Light");
            themeToggle.setText(newVal ? "Dark" : "Light");
            
        });
    }

}
