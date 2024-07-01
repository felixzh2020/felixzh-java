package com.felixzh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author FelixZh
 * <p>
 * create table test(id int);
 * insert into test values(1);
 */
public class SparkThriftJdbc {
    public static void main(String[] args) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection connection = DriverManager.getConnection("jdbc:hive2://felixzh:10001/default");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from test");
        while (resultSet.next()) {
            System.out.println(resultSet.getString("id"));
        }
        statement.close();
        connection.close();
    }
}