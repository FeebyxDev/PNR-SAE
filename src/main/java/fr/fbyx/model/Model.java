package fr.fbyx.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class Model {

	public static final ObservableList<Animal> especeAnimal;
	public static final ObservableList<Users> users;
	public static final ArrayList<String> roles;

    
    static {

        especeAnimal = FXCollections.observableArrayList(
			new Animal("Chouette", "SELECT * FROM vue_Chouette"),
			new Animal("Hippocampe", "SELECT * FROM vue_Hippocampe"),
			new Animal("Batracien", "SELECT * FROM vue_Batracien")
		);

		users = FXCollections.observableArrayList(
			Users.ofSplit("Anthony;Pogu;Admin"),
			Users.ofSplit("Youenn;Jamard;Developper"),
			Users.ofSplit("Loris;Danel;Member")
		);

		roles = new ArrayList<>();
		roles.add("Member");
		roles.add("Developper");
		roles.add("Admin");
    }
}
