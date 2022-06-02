package fr.fbyx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import fr.fbyx.App;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class MainController implements Initializable {

    private Stage stage;
	private double xOffset;
	private double yOffset;
	private final ToggleGroup toggleGroup;
	   

    @FXML
	private VBox navBar;

    @FXML
	private StackPane contentPane;

	public StackPane getContentPane() {
		return contentPane;
	}

    public MainController() {
        this.stage = App.getStage();
        this.toggleGroup = new ToggleGroup();
		ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle ressource) {
	
		initializeLoader();
    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();
		loader.addView(MFXLoaderBean.of("HOME", App.loadURL("fxml/Home.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-home", "Accueil")).setDefaultRoot(true).get());
		loader.addView(MFXLoaderBean.of("SEARCH", App.loadURL("fxml/Search.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-search", "Recherche")).get());
		loader.addView(MFXLoaderBean.of("NVOBS", App.loadURL("fxml/NvObs.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-plus", "Nouvelle Observation")).get());
		loader.addView(MFXLoaderBean.of("SETTINGS", App.loadURL("fxml/Settings.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-sliders", "Paramètres")).get());
		if(App.isAdmin()) loader.addView(MFXLoaderBean.of("NVTABLE", App.loadURL("fxml/NvTable.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-plus", "Nouvelle Table")).get());
		if(App.isAdmin()) loader.addView(MFXLoaderBean.of("ADMIN", App.loadURL("fxml/Admin.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-lock", "Administration")).get());
		loader.setOnLoadedAction(beans -> {
			List<ToggleButton> nodes = beans.stream()
					.map(bean -> {
						ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
						toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
						if (bean.isDefaultView()) {
							contentPane.getChildren().setAll(bean.getRoot());
							toggle.setSelected(true);
						}
						return toggle;
					}).toList();
			navBar.getChildren().setAll(nodes);
		});
		loader.start();
    }

    private ToggleButton createToggle(String icon, String text) {
		MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);
		wrapper.setStyle("-mfx-color: #ffffff;");
		MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
		toggleNode.setAlignment(Pos.CENTER_LEFT);
		toggleNode.setMaxWidth(Double.MAX_VALUE);
		toggleNode.setToggleGroup(toggleGroup);
		toggleNode.getStyleClass().add("menu-btn");

		return toggleNode;
	}

}
