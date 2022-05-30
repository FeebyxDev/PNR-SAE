package fr.fbyx.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.fbyx.App;
import fr.fbyx.MysqlConnect;

public class Animal {
    private String animal;
    private String vue;
    private String request;

    public Animal(String animal, String vue, String request) {
        this.animal = animal;
        this.vue = vue;
        this.request = request;
    }

    public ResultSet makeARequest() {
        MysqlConnect connect = App.getMysqlConncetion();
        ResultSet res = null;
        try {
            PreparedStatement statement = connect.getConnexion().prepareStatement(this.request);
            res = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public ResultSet getTables() {
        MysqlConnect connect = App.getMysqlConncetion();
        ResultSet res = null;
        try {
            PreparedStatement statement = connect.getConnexion().prepareStatement("SELECT * FROM tableInfo WHERE viewName = ?");
            statement.setString(1, this.vue);
            res = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getAnimalString() {
        return animal;
    }

    public String getVue() {
        return vue;
    }

    public String getRequest() {
        return request;
    }

    public static Animal ofSplit(String data) {
		String[] dataArray = data.split(";");
		return new Animal(dataArray[0], dataArray[1], dataArray[2]);
	}

}
