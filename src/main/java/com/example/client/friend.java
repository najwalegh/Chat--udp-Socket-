package com.example.client;

import com.example.db.ConnexionData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class friend extends Application {

    static String me;

    public friend(String me){
        this.me=me;
    }

    @Override
    public void start(Stage stage) {
        //Label for education
        Text FriendLabel = new Text("Select a friend to chat with");

        //list View for educational qualification
        ResultSet rs =new ConnexionData().friend();
        ObservableList<String> names = FXCollections.observableArrayList();

//        for (String item : UserList) {
//            names.add(item);
//            System.out.println(item);
//        }
//        for (int i = 0; i < UserList.size(); i++) {
//            String item = UserList.get(i);
//            names.add(item);
//        }

        //database
        PreparedStatement st = null;
        ResultSet rs2 = null;
        Connection con = ConnexionData.getCon();
        try {
            while (rs.next()) {
                int id_user= rs.getInt("id_user");
                st=con.prepareStatement("select DISTINCT * from user where id_user="+id_user);
                rs2=st.executeQuery();
                if (rs2.next()) {
                    String name = rs2.getString("username");
                    names.add(name);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        ListView<String> FriendListView = new ListView<String>(names);

//        // Enable single selection mode
        FriendListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //button confirmation
        Button buttonChat = new Button("OK");

        //button confirmation
        Button buttonGroup = new Button("Group");

        //button confirmation
        Button buttonRefresh = new Button("Refresh");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(500, 500);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(FriendLabel, 0, 5);
        gridPane.add(FriendListView, 1, 5);
        gridPane.add(buttonChat, 2, 8);
        gridPane.add(buttonGroup, 0, 8);
        gridPane.add(buttonRefresh, 1, 8);

        FriendLabel.setStyle("-fx-font: normal bold 15px 'serif'");
        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        stage.setTitle("Select friend");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

        buttonChat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Get the currently selected item
                String selectedItem = FriendListView.getSelectionModel().getSelectedItem();
                appel(selectedItem);
                stage.close();
            }
        });

        buttonGroup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Get the currently selected item
                List < String> selectedItems = FriendListView.getSelectionModel().getSelectedItems();
                appel2(selectedItems);
                stage.close();
            }
        });

        buttonRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
                Platform.runLater( () -> new friend(me).start( new Stage() ) );
            }
        });
    }

    private void appel(String selectedItem) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Your class that extends Application
                try {
                    new ChatBox(selectedItem , me,false).start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void appel2(List<String> selectedItems) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Your class that extends Application
                    try {
                        new ChatBox("Group", me,true).start(new Stage());
                    } catch(Exception e){
                        e.printStackTrace();
                    }

            }
        });
    }


    public static void main(String args[]){
        launch(args);
    }
}