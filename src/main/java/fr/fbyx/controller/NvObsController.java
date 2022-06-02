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
import javafx.scene.control.Label;
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

		HashMap<String, ArrayList> fk = new HashMap<>();
		try {
			PreparedStatement statement = connect.getConnexion().prepareStatement("SELECT * FROM " + nomTable);
			rs = statement.executeQuery();
			// ResultSet rsfk = connect.getConnexion().getMetaData().getIndexInfo(connect.getConnexion().getCatalog(), null, nomTable, true, true);
			
			
			/**
			 * Request to get Check domain to fill the combobox
			 */
			ResultSet rsfk = connect.getConnexion().prepareStatement("SHOW CREATE TABLE " + nomTable).executeQuery();
			

			while (rsfk.next()) {
				/* for(int i = 1; i <= rsfk.getMetaData().getColumnCount(); i++) {
					System.out.println(i + " : " + rsfk.getMetaData().getColumnName(i) + " : " + rsfk.getString(i));
				} */

				String attrName = "";
				ArrayList<String> doms = null;
				String[] create = rsfk.getString(2).split("\n");
				// System.out.println(Arrays.toString(create));

				for (String s : create) {
					if (s.contains("dom")) {
						// System.out.println("Lines : " + s);

						Pattern pat1 = Pattern.compile("[\\s\\S](?<!`(\\w+))");
						Matcher mat1 = pat1.matcher(s.trim());
						attrName = mat1.replaceAll("£").replaceAll("£+", "ø").replaceFirst("ø", "").split("ø")[1];

						// System.out.println("AttrName : " + attrName);

						Pattern pat2 = Pattern.compile("[\\s\\S](?<!'([A-zÀ-ú]+))");
						Matcher mat2 = pat2.matcher(s.trim());
						String s2 = mat2.replaceAll("£");
						String s3 = s2.replaceAll("£+", "ø").replaceFirst("ø", "");
						doms = new ArrayList<>(Arrays.asList(s3.split("ø")));

						// System.out.println("Doms : " + doms.toString());
						
						fk.put(attrName, doms);
					}
				}

				/* ArrayList<String> tmp = new ArrayList();
				tmp.add(rsfk.getString("FKTABLE_NAME"));
				tmp.add(rsfk.getString("PKCOLUMN_NAME")); */
				
			}
		
		// System.out.println(fk.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		vb.setSpacing(10);
		vb.setStyle("-fx-background-color: #ffffff;");
		vb.setPrefWidth(300);
		vb.setLayoutX(10);
		vb.setLayoutY(10);
		try {
			for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				if(fk.containsKey(rs.getMetaData().getColumnName(i+1))){
					vb.getChildren().add(createComboB(rs.getMetaData().getColumnName(i+1), rs.getMetaData().getColumnTypeName(i+1), fk));
				} else {
					vb.getChildren().add(createAttr(rs.getMetaData().getColumnName(i+1), rs.getMetaData().getColumnTypeName(i+1)));
				}
				
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

	private HBox createComboB(String attrName, String attrType, HashMap<String, ArrayList> fk) {
		HBox hb = new HBox();
		hb.setSpacing(7);
		// hb.getChildren().add(new Label(attrName + " (" + attrType + ") : "));
		hb.alignmentProperty().set(Pos.CENTER);

		MFXComboBox<String> cb = new MFXComboBox<>();
		cb.setMinWidth(300);
		cb.floatModeProperty().set(FloatMode.BORDER);
		cb.floatingTextProperty().set(attrName + " :" + attrType);
		cb.getItems().addAll(fk.get(attrName));
		fields.add(cb);
		hb.getChildren().add(cb);


		return hb;
	}

	



}
