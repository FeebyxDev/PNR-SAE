package fr.fbyx.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;
import fr.fbyx.Notification;
import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class SearchController implements Initializable {
    
    private MysqlConnect connect;
    private HashMap<String, MFXTableView> childTables;
    private HashMap<String, MFXTableView> allTables = new HashMap<>();
    private ArrayList<MFXTextField> fields = new ArrayList<>();

    @FXML
	private MFXFilterComboBox<String> comboEspece;

    @FXML
    private MFXButton refreshData;

    @FXML
	private MFXTableView<ObservableList> table;

    @FXML
    private MFXScrollPane scroll;

    @FXML
    private MFXButton validBtn;

    @FXML private HBox newTables;

    @FXML private HBox mainTableBox;

    public SearchController() {
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ScrollUtils.addSmoothScrolling(scroll);
    
        connect = App.getMysqlConncetion();
        

        // Model.especeAnimal.forEach(d -> comboEspece.getItems().add(d.getAnimalString()));


        /**
		 * Set up des Especes
		 */
		/* try {
			PreparedStatement ps = connect.getConnexion().prepareStatement("SHOW TABLES WHERE Tables_in_pnr LIKE 'vuel%';");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				comboEspece.getItems().add(rs.getString("Tables_in_pnr"));
			}
            comboEspece.getSelectionModel().selectFirst();
		} catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tables");
			e.printStackTrace();
		} */

        try {
			PreparedStatement ps = connect.getConnexion().prepareStatement("SELECT * FROM hideMainSearch;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				comboEspece.getItems().add(rs.getString("nomAffichage"));
			}
            comboEspece.getSelectionModel().selectFirst();
		} catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tables");
			e.printStackTrace();
		}
        
        // comboEspece.setItems(Model.especeAnimal);

        String getTb = getCBTable();
        fillTable(table, getTb);
        allTables.put(getTb, table);
        actualiseTables();

        comboEspece.setOnAction(e -> {
            RefreshData(null);
            RefreshData(null);
        });

        refreshData.setOnMouseClicked(event -> {
            RefreshData(null);
            MFXNotificationSystem.instance()
				.setPosition(NotificationPos.TOP_RIGHT)
				.publish(new Notification("Refresh Data", "Data as been refreshed", "check"));
        });
        
        ArrayList<String> especes = new ArrayList<>();

        table.autosizeColumnsOnInitialization();
        table.getSelectionModel().setAllowsMultipleSelection(false);

        table.getSelectionModel().selectionProperty().addListener((MapChangeListener<? super Integer, ? super ObservableList>) change -> {
            if(change.getValueAdded() != null) {
                liasonTable();
            }
        });


        validBtn.setOnAction(e -> {

            System.out.println(table.getFilters().get(0).toString());
            // 6
            AbstractFilter<ObservableList, ?> selected = table.getFilters().get(0);
            selected.setSelectedPredicateIndex(6);
            FilterBean<ObservableList, ?> predicate = selected.toFilterBean("135-2");
            MFXFilterPane filterpane = ((MFXTableViewSkin) table.getSkin()).filterPane;
            System.out.println(filterpane.getActiveFilters().add(predicate));
            table.getTransformableList().setPredicate(filterpane.filter());

            /* table.getSelectionModel().setAllowsMultipleSelection(true);
            table.getSelectionModel().getSelectedValues().forEach(d -> {
                System.out.println(d.toString());
            });*/

        });

    }

    private void RefreshData(ObservableList data) {
        table.getFilters().clear();
        table.getTableColumns().clear();
        String getTb = getCBTable();
        fillTable(table, getTb);
        allTables.put(getTb, table);
        actualiseTables();
        table.autosize();
        /* if(data != null){
            System.out.println("RefreshData DATAAAAA");
            table.getSelectionModel().deselectItem(data);
            table.getSelectionModel().selectItem(data);
            liasonTable();
        }  */
    }

    private void liasonTable() {
        try {
            PreparedStatement ps = connect.getConnexion().prepareStatement("SELECT * FROM hideTableSearch WHERE nomAffi = '" + comboEspece.getSelectedItem() + "';");
            ResultSet rs = ps.executeQuery();
            int[] tmpindex = {0};
            final String[] attrAsso = {""};

            while(rs.next()) {
                tmpindex[0] = 0;
                table.getTableColumns().forEach(colu -> {
                    try {
                        if(colu.getText().equals(rs.getString("attrTable"))) {
                            tmpindex[0] = table.getTableColumns().indexOf(colu);
                            attrAsso[0] = rs.getString("attrAsso");
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                });
                
            }
            String value = (String) table.getSelectionModel().getSelectedValues().get(0).get(tmpindex[0]);
            ArrayList tableColumn = searchAttr(attrAsso[0]);
            addFilter((MFXTableView) tableColumn.get(0), (MFXTableColumn) tableColumn.get(1), value);


            PreparedStatement ps2 = connect.getConnexion().prepareStatement("SELECT * FROM hideTableSearch WHERE nomAffi = '" + comboEspece.getSelectedItem() + "';");
            ResultSet rs2 = ps2.executeQuery();

            while(rs2.next()) {
                ArrayList ar = (ArrayList) searchAttr(rs2.getString("attrTable"));
                if(!ar.isEmpty()) {
                    MFXTableView tb = (MFXTableView) ar.get(0);
                    int index = tb.getTableColumns().indexOf((MFXTableColumn) ar.get(1));
                    String value2 = (((ObservableList)((MFXTableView) ar.get(0)).getCell(0).getData())).get(index).toString();
                    ArrayList tableAndColumn = searchAttr(rs2.getString("attrAsso"));
                    addFilter((MFXTableView) tableAndColumn.get(0), (MFXTableColumn) tableAndColumn.get(1), value2);
                }
            }

           
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList searchAttr(String attr) {
        ArrayList ret = new ArrayList<>();

        for(MFXTableView<MFXTableColumn> tb : childTables.values()) {
            for(MFXTableColumn col : tb.getTableColumns()) {
                if(col.getText().equals(attr)) {
                    ret.add(tb);
                    ret.add(col);
                }
            }
        }
        return ret;
    }

    private void addFilter(MFXTableView tb, MFXTableColumn col, String value) {
        int index = tb.getTableColumns().indexOf(col);
        AbstractFilter<ObservableList, ?> selected = (AbstractFilter<ObservableList, ?>) tb.getFilters().get(index);
        selected.setSelectedPredicateIndex(6);
        FilterBean<ObservableList, ?> predicate = selected.toFilterBean(value);
        MFXFilterPane filterpane = ((MFXTableViewSkin) tb.getSkin()).filterPane;
        filterpane.getActiveFilters().clear();
        filterpane.getActiveFilters().add(predicate);
        tb.getTransformableList().setPredicate(filterpane.filter());
    }

    private void actualiseTables() {
        newTables.getChildren().clear();
        childTables = new HashMap<>();
        try {
            PreparedStatement ps = connect.getConnexion().prepareStatement("SELECT * FROM hideTableSearch WHERE nomAffi = '" + comboEspece.getSelectedItem() + "';");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                createTable(rs.getString("tableAssocie"));
                // System.out.println(rs.getString("nomTable") +" : "+rs.getString("tableAssocie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getCBTable() {        
        try {
            PreparedStatement ps = connect.getConnexion().prepareStatement("SELECT nomTable FROM hideMainSearch WHERE nomAffichage = '" + comboEspece.getSelectionModel().getSelectedItem() + "';");
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString("nomTable");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createTable(String strtable) {
        VBox vboxTable = new VBox();
        vboxTable.setSpacing(10);
        vboxTable.setPadding(new Insets(10, 10, 10, 10));
        vboxTable.setAlignment(Pos.CENTER);
        
        Label tbName = new Label("Table : " + strtable);
        tbName.setFont(new Font(20));
        

        MFXTableView<ObservableList> tb = new MFXTableView<>();
        tb.setMinHeight(250);

        HBox hboxTb = new HBox();
        hboxTb.setSpacing(10);
        hboxTb.setPadding(new Insets(10, 10, 10, 10));
        hboxTb.setAlignment(Pos.CENTER);

        MFXButton removeBtn = new MFXButton("Supprimer");
        removeBtn.getStyleClass().setAll("rmBtn", "medium-btn", "mfx-button", "disabled");
        removeBtn.setDisable(true);

        removeBtn.setOnAction(event -> {
            List<ObservableList> selectedval = tb.getSelectionModel().getSelectedValues();
            // System.out.println(selectedval.get(0).getClass());
            selectedval.forEach(data -> {
                try {
                    // System.out.println(data.toString());
                    // System.out.println(data.size());
                    String tmp = "";
                    for (int i = 0; i < data.size(); i++) {
                        if(i == data.size()-1) tmp += tb.getTableColumns().get(i).getText() + " = '" + data.get(i) + "'";
                        else tmp += tb.getTableColumns().get(i).getText() + " = '" + data.get(i) + "' AND ";
                    }
                    String request = "DELETE FROM " + strtable + " WHERE " + tmp + ";";
                    // System.out.println(request);
                    PreparedStatement ps = connect.getConnexion().prepareStatement(request);
                    ps.executeUpdate();
                    tb.getItems().remove(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        MFXButton modifyBtn = new MFXButton("Modifier");
        modifyBtn.getStyleClass().setAll("modBtn", "medium-btn", "mfx-button", "disabled");

        modifyBtn.setOnAction(event -> {
             // System.out.println(allTables.toString());
            // allTables.get(strtable);
            try {
                ArrayList<String> doNotModify = new ArrayList<>();
                ResultSet rs = connect.getConnexion().getMetaData().getPrimaryKeys(null, null, strtable);
                System.out.println("TESTTTTTTT");
                while(rs.next()) {
                    /* System.out.println(rs.getString("COLUMN_NAME"));
                    for (int i = 0; i < rs.getFetchSize(); i++) {
                        System.out.println(rs.getString(i));
                    } */
                    doNotModify.add(rs.getString("COLUMN_NAME"));
                }

                HashMap<String, String> tmp = new HashMap<>();
                for (int i = 0; i < tb.getSelectionModel().getSelectedValues().get(0).size(); i++) {
                    if(!tmp.containsKey(tb.getTableColumns().get(i).getText())) {
                        tmp.put(tb.getTableColumns().get(i).getText(), (String) tb.getSelectionModel().getSelectedValues().get(0).get(i));
                    }
                }
                if(mainTableBox.getChildren().size() > 1) mainTableBox.getChildren().remove(1);
                mainTableBox.getChildren().add(createTableFields(strtable, tmp, doNotModify, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        MFXButton addBtn = new MFXButton("Ajouter");
        addBtn.getStyleClass().setAll("addBtn", "medium-btn", "mfx-button");

        addBtn.setOnAction(event -> {
            // System.out.println(allTables.toString());
            // allTables.get(strtable);
            try {
                ResultSet rs = connect.getConnexion().getMetaData().getImportedKeys(connect.getConnexion().getCatalog(), null, strtable);
                HashMap<String, String> tmp = new HashMap<>();
                ArrayList<String> doNotModify = new ArrayList<>();
                while(rs.next()) {
                    System.out.println(rs.getString("FKTABLE_NAME") + " : " + rs.getString("FKCOLUMN_NAME") + " : " + rs.getString("PKTABLE_NAME") + " : " + rs.getString("PKCOLUMN_NAME"));
                    MFXTableView tbfk = allTables.get(rs.getString("PKTABLE_NAME"));
                    if(tbfk != null) {
                        if(tbfk.getSelectionModel().getSelectedValues().size() > 1) throw new IllegalArgumentException();
                        int index = tbfk.getTableColumns().indexOf(tbfk.getTableColumns().stream().filter(col -> {
                            try {
                                return ((MFXTableColumn) col).getText().equals(rs.getString("PKCOLUMN_NAME"));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }).findFirst().get());
                        String val = ((ObservableList<String>) tbfk.getSelectionModel().getSelectedValues().get(0)).get(index);
                        tmp.put(rs.getString("FKCOLUMN_NAME"), val);
                        doNotModify.add(rs.getString("FKCOLUMN_NAME"));
                    }
                }
                if(mainTableBox.getChildren().size() > 1) mainTableBox.getChildren().remove(1);
                mainTableBox.getChildren().add(createTableFields(strtable, tmp, doNotModify, true));
            } catch (Exception e) {
                e.printStackTrace();
            }

            
        });

        hboxTb.getChildren().addAll(removeBtn, modifyBtn, addBtn);
        
        childTables.put(strtable, tb);
        allTables.put(strtable, tb);
        vboxTable.getChildren().addAll(tbName, tb, hboxTb);
        newTables.getChildren().add(vboxTable);


        tb.getSelectionModel().allowsMultipleSelection();

        tb.getSelectionModel().selectionProperty().addListener((MapChangeListener<? super Integer, ? super ObservableList>) change -> {
            
            if(tb.getSelectionModel().getSelectedValues().get(0) == null || tb.getSelectionModel().getSelectedValues().size() < 1) {
                removeBtn.setDisable(true);
                removeBtn.getStyleClass().add("disabled");
                modifyBtn.setDisable(true);
                modifyBtn.getStyleClass().add("disabled");
            } else {
                removeBtn.setDisable(false);
                removeBtn.getStyleClass().remove("disabled");
                modifyBtn.setDisable(false);
                modifyBtn.getStyleClass().remove("disabled");
            }

            if(tb.getSelectionModel().getSelectedValues().size()>1) {
                modifyBtn.setDisable(true);
                modifyBtn.getStyleClass().add("disabled");
            } else {
                modifyBtn.setDisable(false);
                modifyBtn.getStyleClass().remove("disabled");
            }

            if(change.getValueAdded() != null) {
                
            }
        });

        fillTable(tb, strtable);
    }

    private void fillTable(MFXTableView<ObservableList> tb, String strtable) {
        ObservableList<ObservableList> data;
        data = FXCollections.observableArrayList();

        try{
            ResultSet rs = connect.getConnexion().prepareStatement("SELECT * FROM " + strtable + ";").executeQuery();
            MFXTableColumn<ObservableList> col;

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/

            ArrayList<String> columnList = new ArrayList<>();

            /* MFXTableColumn sel = new MFXTableColumn("Selection", false);
            sel.setRowCellFactory(data1 -> new MFXCheckListCell<>());
            table.getTableColumns().add(sel); */


            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                col = new MFXTableColumn(rs.getMetaData().getColumnName(i+1), true);
                
                
                // System.out.println("Column Type : " + rs.getMetaData().getColumnName(i+1) + " : " + rs.getMetaData().getColumnTypeName(i+1));

                columnList.add(rs.getMetaData().getColumnName(i+1));
                
                col.setRowCellFactory(ol -> new MFXTableRowCell<>(param -> {
                    
                    // return new MFXTableRow(table, ((ObservableList) param).get(j));
                    return ((ObservableList) param).get(j);
                }));

                
                
                switch(rs.getMetaData().getColumnTypeName(i+1)) {
                    case "VARCHAR":
                    default:
                        col.setComparator(Comparator.comparing(ol -> (String) ol.get(j)));
                        break;
                    case "INT":
                        col.setComparator(Comparator.comparing(ol -> Integer.valueOf((String) ol.get(j))));
                        break;
                    case "DECIMAL":
                        col.setComparator(Comparator.comparing(ol -> Double.valueOf((String) ol.get(j))));
                        break;
                }

                
                tb.getFilters().add(new StringFilter<>(col.getText(), ol -> (String) ol.get(j)));

                tb.getTableColumns().add(col);


                // System.out.println("Column ["+i+"] ");
            }
            // totalColumns.forEach(c -> table.getTableColumns().add(c));
            
            

            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                ObservableList row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                    
                }
                data.add(row);
            }

            tb.setItems(data);
        }catch(Exception e){
            // e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }






    private VBox createTableFields(String nomTable, HashMap<String, String> data, ArrayList<String> doNotModify, boolean isNew) {

		VBox vb = new VBox();
		Label lb = new Label(nomTable);
		
		MFXFontIcon remove = new MFXFontIcon("mfx-x-alt");
		remove.getStyleClass().add("close-icon");
		remove.setSize(30);
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			mainTableBox.getChildren().remove(vb);
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

						Pattern pat2 = Pattern.compile("[\\s\\S](?<!'([A-zÀ-ú ]+))");
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
        vb.setPadding(new Insets(20, 20, 20, 20));
		vb.setPrefWidth(300);
		vb.setLayoutX(10);
		vb.setLayoutY(10);
		try {
			for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                String content = "";
                boolean disabled = false;
                if(data.get(rs.getMetaData().getColumnName(i+1)) != null) content = data.get(rs.getMetaData().getColumnName(i+1));
                // System.out.println(doNotModify.toString());
                if(doNotModify.contains(rs.getMetaData().getColumnName(i+1))) disabled = true;

				if(fk.containsKey(rs.getMetaData().getColumnName(i+1))){
					vb.getChildren().add(createComboB(rs.getMetaData().getColumnName(i+1), rs.getMetaData().getColumnTypeName(i+1), fk, content, disabled));
				} else {
					vb.getChildren().add(createAttr(rs.getMetaData().getColumnName(i+1), rs.getMetaData().getColumnTypeName(i+1), content, disabled));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        MFXButton validerBtn;
        if(isNew)  validerBtn = new MFXButton("Ajouter");
        else validerBtn = new MFXButton("Enregister");

        validerBtn.getStyleClass().setAll("addBtn", "medium-btn", "mfx-button");
        vb.getChildren().add(validerBtn);
        vb.setAlignment(Pos.CENTER);

        
        final MFXTableView[] tbex = {allTables.get(nomTable)};

        validerBtn.setOnAction(e -> {
			// System.out.println("Valider");
			final boolean[] isValid = {true};
			final HashMap[] map = {new HashMap<String, ArrayList>()};
			mainTableBox.getChildren().forEach(vbox -> {

				final String[] table = {""};
				final ArrayList[] values = {new ArrayList()};

				if(vbox instanceof VBox) ((VBox) vbox).getChildren().forEach(hbox -> {

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
                map[0].clear();
				map[0].put(table[0], values[0]);
			});

			if(isValid[0]) {
				System.out.println("Tout est ok !");
				// System.out.println(map[0].toString());
				map[0].forEach((k, v) -> {
					// System.out.println(k + " : " + v.toString());
					MysqlConnect connect = App.getMysqlConncetion();
                    if(isNew) {
                        String nbval = "?";
                        for(int i = 1; i < ((ArrayList) v).size(); i++) {
                            nbval += ", ?";
                        }
                        try {
                            // System.out.println("INSERT INTO ? VALUES (" + nbval + ")");
                            PreparedStatement ps = connect.getConnexion().prepareStatement("INSERT INTO " + (String) k + " VALUES (" + nbval + ");");
                            for(int j = 0; j < ((ArrayList) v).size(); j++) {
                                System.out.println(((ArrayList) v).get(j));
                                ps.setObject(j+1, ((ArrayList) v).get(j));
                            }
                            
                            ps.executeUpdate();
                            ObservableList row = FXCollections.observableArrayList();
                            ((ArrayList) v).forEach(val -> {
                                row.add(val.toString());
                            });
                            this.childTables.get((String) k).getItems().add(row);
                            this.mainTableBox.getChildren().remove(vb);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            String request = "UPDATE " + (String) k + " SET ";
                            String pk = "";
                            ResultSet rs2 = connect.getConnexion().getMetaData().getPrimaryKeys(connect.getConnexion().getCatalog(), null, (String) k);
                            while(rs2.next()) {
                                pk = rs2.getString("COLUMN_NAME");
                            }
                            int indexpk = 0;
                            for(int i = 0; i < ((ArrayList) v).size(); i++) {
                                String nomCol = ((MFXTableColumn) tbex[0].getTableColumns().get(i)).getText();
                                if(!pk.equals(nomCol)) {
                                    if(i == 0) {
                                        request += nomCol;
                                    } else {
                                        request += ", " + nomCol;
                                    }
                                    request += " = ?";
                                } else {
                                    indexpk = i;
                                }
                            }
                            request += " WHERE " + pk + " = ?;";
                            System.out.println(request);
                            PreparedStatement ps3 = connect.getConnexion().prepareStatement(request);
                            int h = 1;
                            // System.out.println("index pk : " + indexpk);
                            for(int j = 0; j < ((ArrayList) v).size(); j++) {
                                System.out.println("J = " + j);
                                if(j != indexpk) {
                                    System.out.println(((ArrayList) v).get(j));
                                    ps3.setObject(h, ((ArrayList) v).get(j));
                                    h++;
                                }
                            }

                            // System.out.println(((ArrayList) v).toString());
                            // System.out.println(h + " : " + ((ArrayList) v).get(indexpk));
                            ps3.setObject(h, ((ArrayList) v).get(indexpk));

                            ps3.executeUpdate();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }

				});
			} else {
				System.out.println("Tout n'est pas ok !");
			}

		});

		return vb;
	}

	private HBox createAttr(String attrName, String attrType, String content, boolean disabled) {
		HBox hb = new HBox();
		hb.setSpacing(7);
		// hb.getChildren().add(new Label(attrName + " (" + attrType + ") : "));
		hb.alignmentProperty().set(Pos.CENTER);
		MFXTextField textf = new MFXTextField();
		textf.setMinWidth(400);
        textf.setMaxWidth(400);
		textf.floatModeProperty().set(FloatMode.BORDER);
		textf.floatingTextProperty().set(attrName + " :" + attrType);
        textf.setText(content);
        textf.setDisable(disabled);
		fields.add(textf);
		hb.getChildren().add(textf);
		return hb;
	}

	private HBox createComboB(String attrName, String attrType, HashMap<String, ArrayList> fk, String content, boolean disabled) {
		HBox hb = new HBox();
		hb.setSpacing(7);
		// hb.getChildren().add(new Label(attrName + " (" + attrType + ") : "));
		hb.alignmentProperty().set(Pos.CENTER);

		MFXComboBox<String> cb = new MFXComboBox<>();
		cb.setMinWidth(400);
        cb.setMaxWidth(400);
		cb.floatModeProperty().set(FloatMode.BORDER);
		cb.floatingTextProperty().set(attrName + " :" + attrType);
		cb.getItems().addAll(fk.get(attrName));
        cb.setValue(content);
        cb.setDisable(disabled);
		fields.add(cb);
		hb.getChildren().add(cb);


		return hb;
	}
}
