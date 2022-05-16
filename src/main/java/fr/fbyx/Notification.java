package fr.fbyx;

import java.util.ArrayList;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.enums.NotificationState;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class Notification extends MFXSimpleNotification {
    private final StringProperty headerText = new SimpleStringProperty("Notification Header");
    private final StringProperty contentText = new SimpleStringProperty();

    public Notification(String title, String message, String type) {

        setHeaderText(title);
        setContentText(message);

        Label headerLabel = new Label();
        headerLabel.textProperty().bind(headerText);
        MFXIconWrapper readIcon = new MFXIconWrapper("mfx-eye", 16, 32);
        ((MFXFontIcon) readIcon.getIcon()).descriptionProperty().bind(Bindings.createStringBinding(
                () -> (getState() == NotificationState.READ) ? "mfx-eye" : "mfx-eye-slash",
                notificationStateProperty()
        ));
        StackPane.setAlignment(readIcon, Pos.CENTER_RIGHT);
        StackPane placeHolder = new StackPane(readIcon);
        placeHolder.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(placeHolder, Priority.ALWAYS);
        HBox header = new HBox(10, headerLabel, placeHolder);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(InsetsFactory.of(5, 0, 5, 0));
        header.setMaxWidth(Double.MAX_VALUE);


        Label contentLabel = new Label();
        contentLabel.getStyleClass().add("content");
        contentLabel.textProperty().bind(contentText);
        contentLabel.setWrapText(true);
        contentLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentLabel.setAlignment(Pos.TOP_LEFT);


        BorderPane container = new BorderPane();
        container.getStyleClass().add("notification");
        if(!type.equals("classic")) container.getStyleClass().add(type);
        container.setTop(header);
        container.setCenter(contentLabel);
        container.getStylesheets().add(App.class.getResource("css/ExampleNotification.css").toString());
        container.setMinHeight(200);
        container.setMaxWidth(400);

        setContent(container);
    }

    public String getHeaderText() {
        return headerText.get();
    }

    public StringProperty headerTextProperty() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText.set(headerText);
    }

    public String getContentText() {
        return contentText.get();
    }

    public StringProperty contentTextProperty() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText.set(contentText);
    }
}