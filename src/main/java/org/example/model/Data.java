package org.example.model;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Data {

    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Chat> allChats = new ArrayList<>();
    private static ArrayList<Lobby> lobbies = new ArrayList<>();
    private static User stayedLoggedIn;
    private static String defaultMap;


    public static ArrayList<User> getUsers() {
        return users;
    }

    public static void setUsers(ArrayList<User> users) {
        Data.users = users;
    }

    public static User getStayedLoggedIn() {
        return stayedLoggedIn;
    }

    public static void setStayedLoggedIn(User stayedLoggedIn) {
        Data.stayedLoggedIn = stayedLoggedIn;
    }

    public static User findUserWithUsername(String username) {
        for (User user : users)
            if (user.getUsername().equals(username)) return user;
        return null;
    }

    public static void saveUsers() throws IOException {

        FileWriter fileWriter = new FileWriter("data.json");
        fileWriter.write(new Gson().toJson(Data.getUsers()));
        fileWriter.close();
        System.out.println("Users data saved to file successfully !");
    }

    public static void saveChats() {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("chats.json");
            fileWriter.write(new Gson().toJson(Data.allChats));
            fileWriter.close();
        } catch (IOException ignored) {

        }
        System.out.println("All chats data saved to file successfully !");
    }

    public static void initializeUsers() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get("data.json")));
            ArrayList<User> users = new Gson().fromJson(json, new TypeToken<ArrayList<User>>() {
            }.getType());
            if (users != null) Data.setUsers(users);
            System.out.println(("Users data initialized successfully !"));
        } catch (IOException e) {
            System.out.println(("Unable to read from database"));
        }
    }

    public static void initializeChats() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get("chats.json")));
            ArrayList<Chat> chats = new Gson().fromJson(json, new TypeToken<ArrayList<Chat>>() {
            }.getType());
            if (chats != null) Data.allChats = chats;
            System.out.println(("All chats initialized successfully !"));
        } catch (IOException e) {
            System.out.println(("Unable to read chats from database"));
        }
    }

    public static void saveStayedLoggedInUser() throws IOException {
        FileWriter fileWriter = new FileWriter("loggedIn.json");
        fileWriter.write(new Gson().toJson(Data.getStayedLoggedIn()));
        fileWriter.close();
    }

    public static void loadStayedLoggedInUser() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get("loggedIn.json")));
            User user = new Gson().fromJson(json, new TypeToken<User>() {
            }.getType());
            if (user != null)
                Data.setStayedLoggedIn(Data.findUserWithUsername(user.getUsername()));

        } catch (IOException ignored) {

        }
    }

    public static ArrayList<Chat> getAllChats() {
        return allChats;
    }

    public static Chat findChatWithId(int id){
        for (Chat chat : allChats)
            if (chat.id==id)
                return chat;
        return null;
    }

    public static ArrayList<Lobby> getLobbies() {
        return lobbies;
    }
    public static synchronized Lobby getLobbyByIName(String lobbyName) {
        Lobby joiningLobby;
        for (Lobby lobby : Data.lobbies) {
            if (lobby.getLobbyName().equals(lobbyName)) {
                return lobby;
            }
        }
        return null;
    }
}
