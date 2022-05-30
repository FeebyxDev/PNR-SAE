package fr.fbyx.controller;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EventListener;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import fr.fbyx.Notification;
import fr.fbyx.model.Animal;
import fr.fbyx.model.Model;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class SearchController implements Initializable {
    
    private ArrayList<MFXTableColumn> totalColumns = new ArrayList<>();
    private ListChangeListener<String> itemsChanged;

    @FXML
    private MFXCheckListView<String> columnCheck;

    @FXML
	private MFXComboBox<String> comboEspece;

    @FXML
    private MFXButton refreshData;

    @FXML
	private MFXTableView<ObservableList> table;

    @FXML
    private MFXScrollPane scroll;

    public SearchController() {
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ScrollUtils.addSmoothScrolling(scroll);
    
        Model.especeAnimal.forEach(d -> comboEspece.getItems().add(d.getAnimalString()));
        
        // comboEspece.setItems(Model.especeAnimal);

        comboEspece.getSelectionModel().selectFirst();

        buildData(getCBItem());

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
    }

    private Animal getCBItem() {
        String animal = comboEspece.getSelectionModel().getSelectedItem();
        return Model.especeAnimal.stream().filter(d -> d.getAnimalString().equals(animal)).findFirst().get();
    }

    private void RefreshData() {
        columnCheck.getItems().clear();
        table.getFilters().clear();
        table.getTableColumns().clear();
        buildData(getCBItem());
        table.autosize();
    }

    /*
    private void setUpTable() {
        MFXTableColumn<Data> espece = new MFXTableColumn<>("Espèce", true, Comparator.comparing(Data::getEspece));
        MFXTableColumn<Data> lieu = new MFXTableColumn<>("Lieu", true, Comparator.comparing(Data::getLieu));
        MFXTableColumn<Data> date = new MFXTableColumn<>("Date", true, Comparator.comparing(Data::getDate));

        
        espece.setRowCellFactory(data -> new MFXTableRowCell<>(Data::getEspece));
        lieu.setRowCellFactory(data -> new MFXTableRowCell<>(Data::getLieu));
        date.setRowCellFactory(data -> new MFXTableRowCell<>(Data::getDate));

        table.getTableColumns().addAll(espece, lieu, date);
        table.getFilters().addAll(
				new StringFilter<>("Espèce", Data::getEspece),
				new StringFilter<>("Lieu", Data::getLieu),
				new StringFilter<>("Date", Data::getDate)
		);
        table.setItems(Model.data);
    }
    */
    

    public void buildData(Animal animal) {
        ObservableList<ObservableList> data;
        data = FXCollections.observableArrayList();
        try{
            ResultSet rs = animal.makeARequest();
            MFXTableColumn<ObservableList> col;


            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/

            ArrayList<String> columnList = new ArrayList<>();
            
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                col = new MFXTableColumn(rs.getMetaData().getColumnName(i+1), true);

                // System.out.println("Column Type : " + rs.getMetaData().getColumnName(i+1) + " : " + rs.getMetaData().getColumnTypeName(i+1));

                columnList.add(rs.getMetaData().getColumnName(i+1));

                col.setRowCellFactory(ol -> new MFXTableRowCell<>(param -> {
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

                
                table.getFilters().add(new StringFilter<>(col.getText(), ol -> (String) ol.get(j)));

                table.getTableColumns().add(col);


                // System.out.println("Column ["+i+"] ");
            }


            columnCheck.getItems().addAll(columnList);
            
            // totalColumns.forEach(c -> table.getTableColumns().add(c));
            
            

            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                    
                }
                // System.out.println("Row [1] added "+row );
                data.add(row);
            }

            //FINALLY ADDED TO TableView

            table.setItems(data);
        }catch(Exception e){
            // e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    private void itemsChangedf(ListChangeListener.Change<String> change) {
        while(change.next()) {
            if(change.wasAdded()) {
                System.out.println("Added : " + change.getAddedSubList());
                change.getAddedSubList().forEach(d -> {
                    System.out.println("Added : " + d);
                    MFXTableColumn<ObservableList> col = new MFXTableColumn<>(d, true);
                    col.setRowCellFactory(ol -> new MFXTableRowCell<>(param -> {
                        return ((ObservableList) param).get(totalColumns.indexOf(col));
                    }));
                    table.getFilters().add(new StringFilter<>(col.getText(), ol -> (String) ol.get(totalColumns.indexOf(col))));
                    table.getTableColumns().add(col);
                });
            }
            if(change.wasRemoved()) {
                System.out.println("Removed : " + change.getRemoved());
                change.getRemoved().forEach(d -> {
                    System.out.println("Removed : " + d);
                    table.getTableColumns().remove(totalColumns.get(totalColumns.indexOf(d)));
                    table.getFilters().remove(totalColumns.get(totalColumns.indexOf(d)));
                });
            }
        }
    }
}
