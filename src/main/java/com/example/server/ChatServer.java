package com.example.server;

import com.example.db.ConnexionData;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ChatServer {
    private static byte[] incoming = new byte[256];
    private static final int PORT = 8080;

    private static DatagramSocket socket;

    static {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<Integer> ports =new ArrayList<>();

    private static final InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        System.out.println("Server started on port " + PORT);

        while (true) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length); // prepare packet
            try {
                socket.receive(packet); // receive packet
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String message = new String(packet.getData(), 0, packet.getLength()); // create a string
            System.out.println("Server received: " + message);

//            int p = 0;
            Integer tab[]=new Integer[2];
            tab[0]= packet.getPort();

            if (message.contains("init;")) {
                String msg[]=message.split(";");
                String Identifier= ConnexionData.getIdentifier(Integer.valueOf(msg[2]));
                String s =Identifier;
                users.add(s);
                ports.add(packet.getPort());
                System.out.println(users);
//                System.out.println(ports);
            }
            else if (message.contains("Individual;")) {
                String msg[] = message.split(";");

                byte[] byteMessage = (msg[3]).getBytes(); // convert the string to bytes

                for (String user : users) {
                    if (user.equals(msg[2])) {
                        tab[1]=ports.get(users.indexOf(user));
                    }
                }
                //sending
                    DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, tab[1]);
                    try {
                        socket.send(forward);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }else if (message.contains("Group")){
                String msg[] = message.split(";");

                int userPort = packet.getPort();  // get port from the packet
                byte[] byteMessage = (msg[3]).getBytes(); // convert the string to bytes

                for (Integer forward_port : ports) {

                    if (forward_port != userPort) {
                        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forward_port);
                        try {
                            socket.send(forward);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

//            else{
//                int userPort = packet.getPort();  // get port from the packet
//                byte[] byteMessage = (message).getBytes(); // convert the string to bytes
////
//                for (int i=0; i< tab.length;i++) {
////                    if (forward_port != userPort) {
//                        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, tab[i]);
//                        try {
//                            socket.send(forward);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
////                    }
//                }
//            }
            // forward to group
//            else if(message.contains("group;")){
//                String msg[]=message.split(";");
//
//                for (String user : users) {
//                    String port[] = user.split(":");
//                    ports.add(Integer.valueOf(port[1]));
//                }
//            }
//            else{
//
//                int userPort = packet.getPort();  // get port from the packet
//                byte[] byteMessage = (message).getBytes(); // convert the string to bytes
//
//                for (Integer forward_port : ports) {
//
//                    if (forward_port != userPort) {
//                        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forward_port);
//                        try {
//                            socket.send(forward);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            }

        }
    }
}
