package fr.fbyx.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import io.github.palexdev.materialfx.font.MFXFontIcon;
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
import javafx.scene.input.MouseEvent;
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
			final boolean[] isValid = {true};
			final HashMap[] map = {new HashMap<String, ArrayList>()};
			FillAttributes.getChildren().forEach(vbox -> {

				final String[] table = {""};
				final ArrayList[] values = {new ArrayList()};

				((VBox) vbox).getChildren().forEach(hbox -> {

					if(hbox instanceof HBox) ((HBox) hbox).getChildren().forEach( node -> {
						// System.out.println(node.toString());
						
						if (node instanceof Label) {
							table[0] = ((Label) node).getText();
						}

						if (node instanceof MFXTextField) {
							MFXTextField field = (MFXTextField) node;
							if (field.getText().isEmpty()) {
								field.setStyle("-fx-border-color: red");
								isValid[0] = false;
							} else {
								field.setStyle("-fx-border-color: green");
								switch (field.getFloatingText().split(":")[1]) {
									case "INT":
										try {
											values[0].add(Integer.parseInt(field.getText()));
											// System.out.println(Integer.parseInt(field.getText()));
										} catch (NumberFormatException e1) {
											field.setStyle("-fx-border-color: red");
											isValid[0] = false;
										}
										break;
									case "DECIMAL":
										try {
											values[0].add(Double.valueOf(field.getText()));
										} catch (NumberFormatException e1) {
											field.setStyle("-fx-border-color: red");
											isValid[0] = false;
										}
										break;
									case "VARCHAR":
									default:
										values[0].add(field.getText());
										break;
								}
							}
						}
						
					});

					
				});
				// System.out.println(table[0] + " : " + values[0].toString());
				map[0].put(table[0], values[0]);
			});

			if(isValid[0]) {
				System.out.println("Tout est ok !");
				// System.out.println(map[0].toString());
				map[0].forEach((k, v) -> {
					// System.out.println(k + " : " + v.toString());
					MysqlConnect connect = App.getMysqlConncetion();
					final String[] nbval = {"?"};
					for(int i = 1; i < ((ArrayList) v).size(); i++) {
						nbval[0] += ", ?";
					}
					try {
						// System.out.println("INSERT INTO ? VALUES (" + nbval[0] + ")");
						PreparedStatement ps = connect.getConnexion().prepareStatement("INSERT INTO " + (String) k + " VALUES (" + nbval[0] + ");");
						for(int j = 0; j < ((ArrayList) v).size(); j++) {
							System.out.println(((ArrayList) v).get(j));
							ps.setObject(j+1, ((ArrayList) v).get(j));
						}
						
						ps.executeUpdate();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				});

			} else {
				System.out.println("Tout n'est pas ok !");
			}

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
			// FillAttributes.getChildren().clear();
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
		Label lb = new Label(nomTable);
		
		MFXFontIcon remove = new MFXFontIcon("mfx-x-alt");
		remove.getStyleClass().add("close-icon");
		remove.setSize(30);
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			FillAttributes.getChildren().remove(vb);
		});
		
		HBox title = new HBox();
		title.spacingProperty().set(10);
		title.setStyle("-fx-font-size: 20;-fx-padding:20 0 0 0;");
		title.setAlignment(Pos.CENTER_LEFT);
		title.getChildren().addAll(remove, lb);
		vb.getChildren().add(title);
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
		textf.floatingTextProperty().set(attrName + " :" + attrType);
		fields.add(textf);
		hb.getChildren().add(textf);
		return hb;
	}



}
