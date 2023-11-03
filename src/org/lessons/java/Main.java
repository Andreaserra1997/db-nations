package org.lessons.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/db_nations";
        String user = "root";
        String password = "root";

        // provo ad aprire una connection con try-with-resources
        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            String query = "select c.name as Nazione ,c.country_id, r.name as Regione, c2.name as Continente\n" +
                    "from countries c join regions r on c.region_id  = r.region_id \n" +
                    "join continents c2 on r.continent_id  = c2.continent_id \n" +
                    "ORDER by c.name;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String nations = resultSet.getString("Nazione");
                        int countryId = resultSet.getInt("country_id");
                        String regions = resultSet.getString("Regione");
                        String continents = resultSet.getString("Continente");

                        System.out.println(nations + " " + countryId + " " + regions + " " + continents);
                    }
                } catch (SQLException e) {
                    System.out.println("Unable to execute query");
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.out.println("Unable to prepare statement");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("Unable to open connection");
            e.printStackTrace();
        }
    }
}