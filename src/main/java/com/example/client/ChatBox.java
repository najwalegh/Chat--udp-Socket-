package com.example.client;
import com.example.db.ConnexionData;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.*;

public class ChatBox extends Application {

    private static  String identifier ;
    private static  String me ;
    private static  Boolean group ;


    private static final int SERVER_PORT = 8080; // send to server

    private static final TextArea messageArea = new TextArea();

    private static final TextField inputBox = new TextField();
    private static final InetAddress address;
    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final DatagramSocket socket;
    static {
        try {
            socket = new DatagramSocket(); // init to any available port
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatBox(String selectedItem, String me, Boolean group){
    this.identifier=selectedItem;
    this.me=me;
    this.group=group;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
//        primaryStage.setTitle(me+"->"+identifier);

            primaryStage.setTitle(me + "->" + identifier);

//        }else{
//            primaryStage.setTitle(me+"-> group");
//        }
        messageArea.setMaxWidth(500);
        messageArea.setEditable(false);

        BorderPane root = new BorderPane();
        root.setCenter(messageArea);

//        inputBox.setMaxWidth(500);
        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {

                // message to send
                String temp;
                if(group!=true){
                    temp ="Individual;"+me+";"+identifier+";"+me + ": " + inputBox.getText();
                }else{
                    temp ="Group;"+"me"+";"+identifier +";"+me+ ": " + inputBox.getText();
                }

                // update messages on screen
                messageArea.setText(messageArea.getText() + inputBox.getText() + "\n");
                // convert to bytes
                byte[] msg = temp.getBytes();
                // remove text from input box
                inputBox.setText("");

                // create a packet & send
                DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
                try {
                    socket.send(send);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        BorderPane root2 = new BorderPane();
        root2.setCenter(inputBox);

        //button select friend
        Button buttonFriend = new Button("Select another one ");

        root2.setLeft(buttonFriend);

        //button select friend
        Button buttonLogOut = new Button("Logout");

        //button Group
//        Button buttonGroup = new Button("Group");

        BorderPane root3 = new BorderPane();
        root3.setRight(buttonLogOut);
//        root3.setLeft(buttonGroup);

        // put everything on screen
        Scene scene = new Scene(new VBox(35, root, root2, root3), 550, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
//        send();
//        if(group!=true){
//            send2();
//        }else{
//            send3();
//        }
        buttonFriend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new friend(me).start(new Stage());
                primaryStage.close();
            }
        });

        buttonLogOut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ConnexionData.logout(identifier);
                primaryStage.close();
            }
        });
    }

    public static void send(String me) throws IOException {
        int id_user=ConnexionData.getIdUser(me);
        ClientThread clientThread = new ClientThread(socket, messageArea);
        clientThread.start();

        // send initialization message to the server
        byte[] uuid = ("init;" + me +";"+id_user).getBytes();

        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
        socket.send(initialize);

    }

//    public void send2() throws IOException {
//        byte[] uuid = ("Individual;"+me+";"+identifier).getBytes();
//        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
//        socket.send(initialize);
//    }

//    private void send3() throws IOException{
//        byte[] uuid = ("Group;"+me+";"+identifier).getBytes();
//        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
//        socket.send(initialize);
//    }

    public static void main(String[] args) throws IOException {
        ClientThread clientThread = new ClientThread(socket, messageArea);
        clientThread.start();

        // send initialization message to the server
        byte[] uuid = ("init;" + identifier).getBytes();
        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
        socket.send(initialize);
        launch();
    }
}
