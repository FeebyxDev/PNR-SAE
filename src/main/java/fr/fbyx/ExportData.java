package fr.fbyx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExportData {
    
    public static void export(String req) {
        MysqlConnect connect = App.getMysqlConncetion();
        /* try {
            // PreparedStatement ps = connect.getConnexion().prepareStatement("SELECT * FROM Chouette INTO OUTFILE 'file.csv' FIELDS TERMINATED BY ','");
            // ResultSet rs = ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } */
        

    }
    
}
