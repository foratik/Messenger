package org.example;

import org.example.model.Data;
import org.example.model.Server;

public class Main {
    public static void main(String[] args) {
        Data.initializeUsers();
        Data.initializeChats();
        Server server = new Server(8080);
        Data.saveChats();
    }
}