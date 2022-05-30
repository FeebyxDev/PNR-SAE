package fr.fbyx.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;
import fr.fbyx.model.Animal;
import fr.fbyx.model.Model;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NvObsController implements Initializable {

	private MysqlConnect connect;
	private ArrayList<MFXTextField> fields = new ArrayList<>();

	@FXML
	private MFXFilterComboBox<String> especeCombo;

    @FXML
	private MFXCheckListView observateurList;

	@FXML
	private VBox FillAttributes;

	@FXML
	private MFXButton validerBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		connect = App.getMysqlConncetion();
		

		/**
		 * Set up bouton valider
		 */
		validerBtn.setOnAction(e -> {
			// System.out.println("Valider");
			FillAttributes.getChildren().forEach(vbox -> {
				((VBox) vbox).getChildren().forEach(hbox -> {
					if(hbox instanceof HBox) ((HBox) hbox).getChildren().forEach(node -> {
						// System.out.println(node.toString());
						if (node instanceof MFXTextField) {
							MFXTextField field = (MFXTextField) node;
							if (field.getText().isEmpty()) {
								field.setStyle("-fx-border-color: red");

							} else {
								field.setStyle("-fx-border-color: green");
								
								
							}
						}
					});
				});
			});
		});

		
		
		/**
		 * Set up des Especes
		 */
		try {
			PreparedStatement ps = connect.getConnexion().prepareStatement("SHOW TABLES WHERE Tables_in_pnr NOT LIKE 'vue_%';");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				especeCombo.getItems().add(rs.getString("Tables_in_pnr"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		especeCombo.setOnAction(e -> {
			String selectedTable = especeCombo.getSelectionModel().getSelectedItem();
			FillAttributes.getChildren().clear();
			FillAttributes.getChildren().add(createTable(selectedTable));
		});
		
		
		// Model.especeAnimal.forEach(d -> especeCombo.getItems().add(d.getAnimalString()));
		/* especeCombo.setOnAction(e -> {
			ResultSet rs = getCBItem().getTables();
			try {
				while (rs.next()) {
					FillAttributes.getChildren().add(createTable(rs.getString("nomTable")));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        }); */

		// --------------------------------------------------
		



		/**
		 * Set Up des Observateurs
		 */
		ObservableList<String> obser = FXCollections.observableArrayList();
		try {
			PreparedStatement ps = connect.getConnexion().prepareStatement("SELECT * FROM Observateur");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				obser.add((rs.getString("nom")==null?"":rs.getString("nom")) + " " + rs.getString("prenom"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		observateurList.setItems(obser);
		// --------------------------------------------------


		

	}
	
	private Animal getCBItem() {
        String animal = especeCombo.getSelectionModel().getSelectedItem();
        return Model.especeAnimal.stream().filter(d -> d.getAnimalString().equals(animal)).findFirst().get();
    }

	private VBox createTable(String nomTable) {
		VBox vb = new VBox();
		Label lb = new Label(nomTable + " : ");
		lb.setStyle("-fx-font-size: 20;-fx-padding:20 0 0 0;");
		vb.getChildren().add(lb);
		ResultSet rs = null;
		try {
			PreparedStatement statement = connect.getConnexion().prepareStatement("SELECT * FROM " + nomTable);
			rs = statement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		vb.setSpacing(5);
		vb.setStyle("-fx-background-color: #ffffff;");
		vb.setPrefWidth(300);
		vb.setLayoutX(10);
		vb.setLayoutY(10);
		try {
			for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				vb.getChildren().add(createAttr(rs.getMetaData().getColumnName(i+1), rs.getMetaData().getColumnTypeName(i+1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vb;
	}

	private HBox createAttr(String attrName, String attrType) {
		HBox hb = new HBox();
		hb.setSpacing(7);
		// hb.getChildren().add(new Label(attrName + " (" + attrType + ") : "));
		hb.alignmentProperty().set(Pos.CENTER);
		MFXTextField textf = new MFXTextField();
		textf.setMinWidth(300);
		textf.floatModeProperty().set(FloatMode.BORDER);
		textf.floatingTextProperty().set(attrName + " (" + attrType + ")");
		fields.add(textf);
		hb.getChildren().add(textf);
		return hb;
	}



}
