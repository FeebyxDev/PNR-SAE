package fr.fbyx.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NvTableController implements Initializable {

	private MysqlConnect connect;


	@FXML
	private MFXButton validerBtn;

	@FXML
	private MFXFontIcon addAttr;

	@FXML
	private MFXFontIcon addConst;

	@FXML
	private VBox constBox;

	@FXML
	private VBox attrBox;

	@FXML private MFXTextField nomTableField;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		connect = App.getMysqlConncetion();
		
		addAttr.setOnMouseClicked(e -> {
			attrBox.getChildren().add(createAttr());
		});

		addConst.setOnMouseClicked(e -> {
			constBox.getChildren().add(createConst());
		});

		/**
		 * Set up bouton valider
		 */
		validerBtn.setOnAction(e -> {
			final boolean[] isValid = {true};
			HashMap[] attr = {new HashMap<String, String>()};
			ArrayList[] constarr = {new ArrayList<ArrayList>()};

			if(nomTableField.getText().isEmpty()) {
				nomTableField.setStyle("-fx-border-color: red");
				isValid[0] = false;
			} else {
				nomTableField.setStyle("-fx-border-color: green");
			}

			attrBox.getChildren().forEach(child -> { // Pour chaque Element dans attrBox (lignes d'attributs)
				if(child instanceof HBox) {

					ArrayList<String> tmp = new ArrayList<>();

					((HBox) child).getChildren().forEach(node -> { // Pour chaque Element dans la ligne d'attributs (Les champs de saisies)
						// System.out.println(node.toString());
						if(node instanceof MFXTextField) {
							MFXTextField field = (MFXTextField) node;
							if(field.getText().isEmpty()) {
								field.setStyle("-fx-border-color: red");
								isValid[0] = false;
							} else {
								field.setStyle("-fx-border-color: -pnr-green");
								tmp.add(field.getText());
							}
						}
						

						
					});

					if(!tmp.isEmpty()) {
						System.out.println(tmp.get(0) + " " + tmp.get(1));
						attr[0].put(tmp.get(0), tmp.get(1));
					}
				}
			});

			constBox.getChildren().forEach(child -> {
				if(child instanceof HBox) {

					ArrayList<String> tmp = new ArrayList<>();

					((HBox) child).getChildren().forEach(node -> { // Pour chaque Element dans la ligne d'attributs (Les champs de saisies)
						// System.out.println(node.toString());
						if(node instanceof MFXTextField) {
							MFXTextField field = (MFXTextField) node;
							if(field.getText().isEmpty()) {
								field.setStyle("-fx-border-color: red");
								isValid[0] = false;
							} else {
								field.setStyle("-fx-border-color: -pnr-green");
								tmp.add(field.getText());
							}
						}
						

						
					});

					if(!tmp.isEmpty()) {
						constarr[0].add(tmp);					
					}
				}
			});

			if(isValid[0]) {
				System.out.println("Tout est ok !");
				// System.out.println(attr[0].toString());
				// System.out.println(constarr[0].toString());
				String req = "CREATE TABLE " + nomTableField.getText() + " (";

				final String[] attrList = {""};
				final int[] j = {0};
				attr[0].forEach((k, v) -> {				
					attrList[0] += (String) k + " " + (String) v;
					if(((String) v).equals("VARCHAR")) attrList[0] += "(200)";
					// System.out.println(j[0] + "  " + attr[0].size());
					if(j[0] < attr[0].size()-1) attrList[0] += ",";
					j[0]++;
				});

				req += attrList[0];

				/* for(int i = 0; i < attr[0].size(); i++) {
					req += "? ?";
					if(i < attr[0].size()-1) req += ",";
				} */

				if(constarr[0].size()>0) {
					req += ",";

					for(int i = 0; i < constarr[0].size(); i++) {
						ArrayList<String> arr = ((ArrayList) constarr[0].get(i));
						switch (arr.get(0)) {
							case "PRIMARY KEY":
								req += "CONSTRAINT pk_" + nomTableField.getText() + " PRIMARY KEY (" + arr.get(1) + ")";
								break;
							case "CHECK":
								req += "CONSTRAINT ck_" + arr.get(1) + " CHECK (" + arr.get(1) + " " + arr.get(2) + ")";
								break;
							case "FOREIGN KEY":
								req += "CONSTRAINT fk_" + nomTableField.getText() + "_" + arr.get(2) + "_" + arr.get(1) + " FOREIGN KEY (" + arr.get(1) + ") REFERENCES " + arr.get(2)+ '(' +arr.get(3) + ')';
							default:
								break;
						}
						if(i < constarr[0].size()-1) req += ",";
					}

				}

				req += ");";

				try {
					PreparedStatement ps = connect.getConnexion().prepareStatement(req);
					System.out.println(ps.toString());
					ps.executeUpdate();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				// System.out.println(map[0].toString());
				/* map[0].forEach((k, v) -> {
					// System.out.println(k + " : " + v.toString());
					MysqlConnect connect = App.getMysqlConncetion();
					String nbval = "?";
					for(int i = 1; i < ((ArrayList) v).size(); i++) {
						nbval += ", ?";
					}
					try {
						// System.out.println("INSERT INTO ? VALUES (" + nbval[0] + ")");
						PreparedStatement ps = connect.getConnexion().prepareStatement("INSERT INTO " + (String) k + " VALUES (" + nbval + ");");
						for(int j = 0; j < ((ArrayList) v).size(); j++) {
							System.out.println(((ArrayList) v).get(j));
							ps.setObject(j+1, ((ArrayList) v).get(j));
						}
						
						ps.executeUpdate();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				}); */

			} else {
				System.out.println("Tout n'est pas ok !");
			}

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

		

	}

	private HBox createAttr() {
		HBox hb = new HBox();
		hb.setSpacing(7);
		hb.alignmentProperty().set(Pos.CENTER);


		MFXTextField textName = new MFXTextField();
		textName.setMinWidth(150);
		textName.floatModeProperty().set(FloatMode.BORDER);
		textName.floatingTextProperty().set("Nom de l'attribut");

		MFXComboBox attrType = new MFXComboBox();
		attrType.setMinWidth(150);
		attrType.floatModeProperty().set(FloatMode.BORDER);
		attrType.floatingTextProperty().set("Type de l'attribut");
		attrType.getItems().addAll("INT", "VARCHAR", "DECIMAL", "DATE");

		MFXFontIcon close = new MFXFontIcon("mfx-delete-alt");
		close.setSize(20);
		close.getStyleClass().add("rmElem");
		close.cursorProperty().set(Cursor.OPEN_HAND);
		close.setOnMouseClicked(e -> {
			attrBox.getChildren().remove(hb);
		});

		hb.getChildren().add(textName);
		hb.getChildren().add(attrType);
		hb.getChildren().add(close);
		return hb;
	}


	private HBox createConst() {
		HBox hb = new HBox();
		hb.alignmentProperty().set(Pos.CENTER);
		hb.setSpacing(7);

		MFXComboBox constType = new MFXComboBox();
		constType.setMinWidth(150);
		constType.floatModeProperty().set(FloatMode.BORDER);
		constType.floatingTextProperty().set("Type de l'attribut");
		constType.getItems().addAll("CHECK", "PRIMARY KEY", "FOREIGN KEY");

		

		MFXComboBox attrName = new MFXComboBox();
		attrName.setMinWidth(150);
		attrName.floatModeProperty().set(FloatMode.BORDER);
		attrName.floatingTextProperty().set("Attribut concerné");
		attrBox.getChildren().forEach(hbox -> {
			if(hbox instanceof HBox) {
				attrName.getItems().add(((MFXTextField) ((HBox) hbox).getChildren().get(0)).getText());
			}
		});

		MFXFontIcon close = new MFXFontIcon("mfx-delete-alt");
		close.setSize(20);
		close.getStyleClass().add("rmElem");
		close.cursorProperty().set(Cursor.OPEN_HAND);
		close.setOnMouseClicked(e -> {
			constBox.getChildren().remove(hb);
		});



		hb.getChildren().add(constType);
		hb.getChildren().add(attrName);
		hb.getChildren().add(close);

		constType.setOnAction(e -> {
			switch (constType.getSelectionModel().getSelectedItem().toString()) {
				case "CHECK":
					MFXTextField textCheck = new MFXTextField();
					textCheck.setMinWidth(150);
					textCheck.floatModeProperty().set(FloatMode.BORDER);
					textCheck.floatingTextProperty().set("Condition de check");
					hb.getChildren().add(hb.getChildren().size()-1, textCheck);
					break;
				case "FOREIGN KEY":
					MFXFilterComboBox fcb = new MFXFilterComboBox();
					fcb.setMinWidth(150);
					fcb.floatModeProperty().set(FloatMode.BORDER);
					fcb.floatingTextProperty().set("Table ciblée");
					try {
						PreparedStatement ps = connect.getConnexion().prepareStatement("SHOW TABLES WHERE Tables_in_pnr NOT LIKE 'vue_%';");
						ResultSet rs = ps.executeQuery();
						while (rs.next()) {
							fcb.getItems().add(rs.getString("Tables_in_pnr"));
						}
					} catch (SQLException er) {
						er.printStackTrace();
					}

					fcb.setOnAction(e2 -> {
						MFXFilterComboBox fcb2 = new MFXFilterComboBox();
						fcb2.setMinWidth(150);
						fcb2.floatModeProperty().set(FloatMode.BORDER);
						fcb2.floatingTextProperty().set("Attribut ciblé");
						try {
							PreparedStatement ps = connect.getConnexion().prepareStatement("SELECT * FROM " + fcb.getSelectionModel().getSelectedItem().toString());
							ResultSet rs = ps.executeQuery();
							rs.next();
							for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
								fcb2.getItems().add(rs.getMetaData().getColumnName(i));
							}
							/* while (rs.next()) {
								for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
									fcb2.getItems().add(rs.getMetaData().getColumnName(i+1));
								}
							} */
						} catch (SQLException er) {
							er.printStackTrace();
						}
						hb.getChildren().add(hb.getChildren().size()-1, fcb2);
					});

					hb.getChildren().add(hb.getChildren().size()-1, fcb);
					break;
				case "PRIMARY KEY":
				default:
					break;
			}
		});



		return hb;
	}


}
