package org.example.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static ArrayList<Connection> allConnections = new ArrayList<>();
    public Server(int port) {
        System.out.println("Starting Server service...");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                allConnections.add(connection);
                connection.start();
            }
        } catch (IOException e) {
            System.out.println("Server failed");
            //TODO: try to reconnect...
        }
    }
}
