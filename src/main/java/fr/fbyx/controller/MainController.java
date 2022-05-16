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
	private final boolean isDarkTheme;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
	private MFXFontIcon minimizeIcon;

    @FXML
	private MFXFontIcon alwaysOnTopIcon;

	@FXML
	private MFXFontIcon toggleSize;

    @FXML
	public AnchorPane rootPane;
    
    @FXML
	private HBox windowHeader;

    @FXML
	private VBox navBar;

    @FXML
	private StackPane contentPane;

	public StackPane getContentPane() {
		return contentPane;
	}

    public MainController(Stage stage) {
        this.stage = stage;
		this.isDarkTheme = App.isDarkTheme();
        this.toggleGroup = new ToggleGroup();
		ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle ressource) {
		if(isDarkTheme) {
			System.out.println("Dark theme Mode activated");
			
		}

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

		initializeLoader();
    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();
		loader.addView(MFXLoaderBean.of("HOME", App.loadURL("fxml/Home.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-home", "Accueil")).setDefaultRoot(true).get());
		loader.addView(MFXLoaderBean.of("SEARCH", App.loadURL("fxml/Search.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-search", "Recherche")).get());
		loader.addView(MFXLoaderBean.of("NVOBS", App.loadURL("fxml/NvObs.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-plus", "Nouvelle Observation")).get());
		loader.addView(MFXLoaderBean.of("SETTINGS", App.loadURL("fxml/Settings.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-sliders", "Paramètres")).get());
		loader.addView(MFXLoaderBean.of("ADMIN", App.loadURL("fxml/Admin.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-lock", "Administration")).get());
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

		MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
		toggleNode.setAlignment(Pos.CENTER_LEFT);
		toggleNode.setMaxWidth(Double.MAX_VALUE);
		toggleNode.setToggleGroup(toggleGroup);
		toggleNode.getStyleClass().add("menu-btn");

		return toggleNode;
	}

}
