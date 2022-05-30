package fr.fbyx.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class Model {

	public static final ObservableList<Animal> especeAnimal;

    
    static {

        especeAnimal = FXCollections.observableArrayList(
			new Animal("Chouette","vue_Chouette", "SELECT * FROM vue_Chouette"),
			new Animal("Loutre", "vue_Loutre", "SELECT * FROM vue_Loutre"),
			new Animal("GCI", "vue_GCI", "SELECT * FROM vue_GCI"),
			new Animal("Hippocampe", "vue_Hippocampe", "SELECT * FROM vue_Hippocampe"),
			new Animal("Batracien", "vue_Batracien", "SELECT * FROM vue_Batracien")
		);

    }
}
