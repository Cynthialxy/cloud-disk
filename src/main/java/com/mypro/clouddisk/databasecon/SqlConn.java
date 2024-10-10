package com.mypro.clouddisk.databasecon;/*
 * SqlConn.java
 * Text file encoding: utf8
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlConn {
    private static final String url = "jdbc:mysql://localhost:3306/cloud-disk?characterEncoding=utf8&useSSL=false&useUnicode=true&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8";
    private static final String user = "root";    //数据库用户名
    private static final String password = "20031126";    //数据库密码

    private static Connection conn = null;   //建立与数据库的连接
    private static Statement stmt = null;   //用于执行静态 SQL 语句的对象
    private static ResultSet rs = null;   //用于表示数据库执行查询后返回的结果集

    PreparedStatement preparedStatement;

    //构造方法，在创建SqlConn对象时执行，尝试加载MySQL数据库驱动程序
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");     //加载数据库驱动
            conn = DriverManager.getConnection(url, user, password);
        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("SqlConn():" + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public static User checkUser(String name, String password) throws SQLException {
        User user = new User();

        String sql1="select password from user where name=?";
        PreparedStatement statement1 = conn.prepareStatement(sql1);
        statement1.setString(1, name);
        ResultSet resultSet1 = statement1.executeQuery();

        if (resultSet1.next()){   //判断是否有输入名字的记录
            String passwordInDatabase = resultSet1.getString("password");
            if(Objects.equals(passwordInDatabase, password)){    //判断密码是否正确
                user = new User();
                user.setName(name);
                user.setPassword(password);
            } else {
                System.out.println("密码错误");
                user=null;
            }
        } else {
                System.out.println("用户名错误");
                user=null;
        }
        return user;
    }

    public int insertIntoUser(String user, String password) {
        String sql="insert into `user`(`name`,`password`) values(?,?) ";
        int i = 0;
        try {
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,user);
            preparedStatement.setString(2,password);
            i=preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("该用户名已存在！");
        }finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("添加用户失败");
            }

        }
        return i;
    }
    public static List<String> selectFromUser() {
        String selectAll = "select * from user";
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("查询失败！");
        }
        List<String> list = null;
        try {
            list = new ArrayList<>();
            rs = stmt.executeQuery(selectAll);
            while (rs.next()) {
                User user_info = new User();
                user_info.setName(rs.getString("name"));
                user_info.setPassword(rs.getString("password"));
                list.add(user_info.name);
            }
        } catch (SQLException e) {
            System.out.println("用户名添加失败！");
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("数据库资源关闭失败！");
            }
        }
        return list;
    }
}
