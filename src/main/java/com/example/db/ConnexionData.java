package com.example.db;

import com.example.client.ChatBox;
import com.example.client.friend;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class ConnexionData {
    static String user = "root";
    static String password = "";
    static String url = "jdbc:mysql://localhost/projetudp";
    static String driver = "com.mysql.cj.jdbc.Driver";

    static ArrayList<String> ActiveUsers = new ArrayList<>();

    public static ArrayList<String> getMyList() {
        return ActiveUsers;
    }

    public static Connection getCon() {
        Connection con = null;
        try {
            Class.forName(driver);
            try {
                con = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return con;
    }
//    public void session(int id){
//        PreparedStatement st = null;
//        Connection con = ConnexionData.getCon();
//        try {
//            // Execute a SELECT statement
//            st = con.prepareStatement("INSERT INTO SESSION(id_user) VALUES ("+id+")");
//            st.executeUpdate();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    public void login(TextField tname, PasswordField tpass) {
        PreparedStatement st = null;
        PreparedStatement st2 = null;
        ResultSet rs = null;
        Connection con = ConnexionData.getCon();
        int id;
        try {
            st = con.prepareStatement("SELECT * FROM user WHERE USERNAME =? AND PASSWORD = ?");
            st.setString(1, tname.getText());
            st.setString(2, tpass.getText());
            rs = st.executeQuery();
            if (rs.next()) {
                id=rs.getInt("id_user");
                String username= rs.getString("username");
//                session(session);
                try {
                    // Execute a SELECT statement
                    st2 = con.prepareStatement("INSERT INTO SESSION(id_user) VALUES ("+id+")");
                    st2.executeUpdate();

                    //add user to activeUser'list
                    ActiveUsers.add(username);

                    //sending the init message to server
                    ChatBox.send(username);

                }catch (Exception e){
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Your class that extends Application
                        try {
                            new friend(username).start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Login Error", ButtonType.OK);
                alert.show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logout(String identifier){
        PreparedStatement st = null,st2 = null;
        ResultSet rs = null;
        Connection con = ConnexionData.getCon();
        try {
            // Execute a SELECT statement
            st= con.prepareStatement("SELECT * FROM USER WHERE USERNAME='"+identifier+"'");
            rs=st.executeQuery();
            if (rs.next()){
                int id_user = rs.getInt("id_user");
                st2 = con.prepareStatement("DELETE FROM SESSION WHERE id_user="+id_user);
                st2.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ResultSet friend() {
        PreparedStatement st = null;
        ResultSet rs = null;
        Connection con = ConnexionData.getCon();
        try {
            // Execute a SELECT statement
            st = con.prepareStatement("SELECT DISTINCT * FROM session ");
            rs = st.executeQuery();
        }catch (Exception e){
            e.printStackTrace();
        }
        return rs;
    }

    public static int getIdUser(String identifier) {
        PreparedStatement st = null;
        ResultSet rs = null;
        Connection con = getCon();

        int id_user = 0;
        try {
            // Execute a SELECT statement
            st = con.prepareStatement("SELECT * FROM user WHERE USERNAME ='" + identifier + "'");
            rs = st.executeQuery();
            if (rs.next()) {
                id_user = rs.getInt("id_user");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println( id_user);
        return id_user;
    }

    public static String getIdentifier(Integer id_user) {
        PreparedStatement st = null;
        ResultSet rs = null;
        Connection con = getCon();

        String Identifier = "";
        try {
            // Execute a SELECT statement
            st = con.prepareStatement("SELECT * FROM user WHERE id_user ="+id_user);
            rs = st.executeQuery();
            if (rs.next()) {
                Identifier = rs.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Identifier;
    }
}