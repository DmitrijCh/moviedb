package com.springbootapp.moviedb.storage;

import com.springbootapp.moviedb.connection.Connection;
import com.springbootapp.moviedb.entity.Table;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskStorage {

    public List<Table> getTasks() {

        List<Table> result = new ArrayList<>();

        String query = "SELECT * FROM table_name order by id asc";

        try {
            Statement statement = Connection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {

                Table table = new Table();
                table.setId(resultSet.getInt("id"));
                table.setTitle(resultSet.getString("title"));
                table.setPosterPath(resultSet.getString("poster_path"));

                result.add(table);

                System.out.println(table);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

//public void addTask(Table table) {
//    MoviedbApplication moviedbApplication = new MoviedbApplication();
//    Connection connection = new Connection();
//
//    String query = "INSERT INTO table_name (id, poster_path, title) VALUES (?, ?, ?)";
//
//    try {
//        PreparedStatement statement = Connection.connection.prepareStatement(query);
//        statement.setLong(1, table.getId();
//        statement.setString(2, table.getPosterPath("title"));
//        statement.setString(3, table.getTitle());
//        statement.execute();
//    } catch (Exception ex) {
//        ex.printStackTrace();
//    }
//}
}

