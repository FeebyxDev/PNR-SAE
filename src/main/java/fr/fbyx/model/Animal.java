package fr.fbyx.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.fbyx.App;

public class Animal {
    private String animal;
    private String request;

    public Animal(String animal, String request) {
        this.animal = animal;
        this.request = request;
    }

    public ResultSet makeARequest() {
        MysqlConnect connect = App.getMysqlConncetion();
        ResultSet res = null;
        try {
            PreparedStatement statement = connect.connect().prepareStatement(this.request);
            res = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;

    }

    public String getAnimalString() {
        return animal;
    }

    public String getRequest() {
        return request;
    }

    public static Animal ofSplit(String data) {
		String[] dataArray = data.split(";");
		return new Animal(dataArray[0], dataArray[1]);
	}

}
