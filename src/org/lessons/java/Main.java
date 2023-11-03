package org.lessons.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String url = "jdbc:mysql://localhost:3306/db_nations";
        String user = "root";
        String password = "root";

        // provo ad aprire una connection con try-with-resources
        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            System.out.println("Search: ");
            String userString = scan.nextLine();

            String query = "select c.name as Nazione ,c.country_id, r.name as Regione, c2.name as Continente\n" +
                    "from countries c join regions r on c.region_id  = r.region_id \n" +
                    "join continents c2 on r.continent_id  = c2.continent_id \n" +
                    "where c.name like ?\n" +
                    "ORDER by c.name;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + userString + "%");
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

                System.out.println("Choose a country id: ");
                String selectedId = scan.nextLine();

                String languageQuery = "select l.language\n" +
                        "from country_languages cl \n" +
                        "join languages l  on cl.language_id = l.language_id \n" +
                        "where cl.country_id = ? ";
                try (PreparedStatement languageStatement = connection.prepareStatement(languageQuery)) {
                    languageStatement.setString(1, selectedId);
                    System.out.println("Details for country: " + selectedId);
                    try (ResultSet languageResult = languageStatement.executeQuery()) {
                        System.out.print("Languages: ");
                        while (languageResult.next()) {
                            String languages = languageResult.getString("language");
                            System.out.print(languages + ",");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Unable to execute query");
                    e.printStackTrace();
                }

                String statisticsQuery = "select *\n" +
                        "from country_stats cs \n" +
                        "where country_id = ?\n" +
                        "order by `year` desc \n" +
                        "limit 1;";
                try (PreparedStatement statisticsStatement = connection.prepareStatement(statisticsQuery)) {
                    statisticsStatement.setString(1, selectedId);
                    try (ResultSet statisticsResult = statisticsStatement.executeQuery()) {
                        System.out.println();
                        System.out.println("Most recent stats");
                        while (statisticsResult.next()) {
                            String year = statisticsResult.getString("year");
                            System.out.println("Year: " + year);
                            String population = statisticsResult.getString("population");
                            System.out.println("Population: " + population);
                            String gdp = statisticsResult.getString("gdp");
                            System.out.println("GDP: " + gdp);
                        }
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

        scan.close();
    }
}