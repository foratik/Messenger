package org.example;

import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        Data.initializeUsers();
        Data.initializeChats();
        Server server = new Server(8080);
        Data.saveChats();
    }
}