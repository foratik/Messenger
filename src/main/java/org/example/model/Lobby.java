package org.example.model;

import javafx.animation.Timeline;

import java.util.ArrayList;

public class Lobby {
    private User admin;
    private int maxNumberOfPlayers;
    private ArrayList<User> players;
    private String lobbyName;
    private LobbyState state;

    public Lobby(User admin, int maxNumberOfPlayers, String lobbyName) {
        this.admin = admin;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.lobbyName = lobbyName;
        this.state = LobbyState.PUBLIC;
        this.players = new ArrayList<>();
    }

    public ArrayList<User> getPlayers() {
        return players;
    }


    public User getAdmin() {
        return admin;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public LobbyState getState() {
        return state;
    }

    public void addPlayer(User user) {
        players.add(user);
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public void setLobbyName(String lobbyName) {this.lobbyName = lobbyName;}

    public void removeLobby() {
        Timeline timeline = new Timeline();
    }

    public void setState(LobbyState state) {
        this.state = state;
    }
}

enum LobbyState {
    PRIVATE, PUBLIC;
}
