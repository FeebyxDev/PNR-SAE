package fr.fbyx.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;
import fr.fbyx.Notification;
import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class SearchController implements Initializable {
    
    private MysqlConnect connect;
    private HashMap<String, MFXTableView> childTables;

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


        fillTable(table, getCBTable());

        comboEspece.setOnAction(e -> {
            RefreshData();
            RefreshData();
        });

        refreshData.setOnMouseClicked(event -> {
            RefreshData();
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

    private void RefreshData() {
        table.getFilters().clear();
        table.getTableColumns().clear();
        fillTable(table, getCBTable());
        table.autosize();
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

    private String getCBTable() {
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
        removeBtn.getStyleClass().setAll("rmBtn", "medium-btn", "mfx-button");

        MFXButton modifyBtn = new MFXButton("Modifier");
        modifyBtn.getStyleClass().setAll("modBtn", "medium-btn", "mfx-button");


        MFXButton addBtn = new MFXButton("Ajouter");
        addBtn.getStyleClass().setAll("addBtn", "medium-btn", "mfx-button");

        hboxTb.getChildren().addAll(removeBtn, modifyBtn, addBtn);
        
        childTables.put(strtable, tb);
        vboxTable.getChildren().addAll(tbName, tb, hboxTb);
        newTables.getChildren().add(vboxTable);


        tb.getSelectionModel().allowsMultipleSelection();

        tb.getSelectionModel().selectionProperty().addListener((MapChangeListener<? super Integer, ? super ObservableList>) change -> {
            
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
                        col.setComparator(Comparator.comparing(ol -> Integer.valueOf((String)ol.get(j))));
                        break;
                    case "DECIMAL":
                        col.setComparator(Comparator.comparing(ol -> Double.valueOf((String)ol.get(j))));
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
}
