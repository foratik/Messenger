package org.example.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Commands {

    LOAD_CHAT("load chat (?<id>[\\d]+)"),
    NEW_CHAT("new chat (?<username>[\\S]+)"),
    ADD_MEMBER("add member (?<username>[\\S]+)"),
    SEND_MESSAGE("send message (?<text>.*)"),
    DELETE_FOR_ALL("delete message (?<text>.+)"),
    EDIT_MESSAGE("edit message (?<currentText>.+)e1e1e1e(?<newText>.+)"),
    NEW_ROOM("new room (?<name>.+)"),
    FRIENDLY_MESSAGE("(?<sender>[\\S]+) sent a friendly message to (?<receiver>[\\S]+)"),
    ACCEPT_FRIENDLY_MESSAGE("(?<receiver>[\\S]+) accept friendly message from (?<sender>[\\S]+)"),
    REJECT_FRIENDLY_MESSAGE("(?<receiver>[\\S]+) reject friendly message from (?<sender>[\\S]+)"),
    JOIN_LOBBY("(?<joiner>[\\S]+) joined the lobby (?<lobbyName>[\\S]+)"),
    LEFT_LOBBY("(?<lefter>[\\S]+) left the lobby (?<lobbyName>[\\S]+)"),
    CREATE_LOBBY("admin (?<admin>[\\S]+) create lobby with maxNumberOfPlayers (?<maxNumberOfPlayers>[\\d]+)"),
    DELETE_LOBBY("delete lobby (?<lobbyName>[\\S]+)"),
    REFRESH("refresh"),
    START_GAME("user (?<user>[\\S]+) starts the lobby (?<lobbyName>[\\S]+) as a game"),
    SET_PUBLIC("user (?<user>[\\S]+) set lobby (?<lobbyName>[\\S]+) public"),
    SET_PRIVATE("user (?<user>[\\S]+) set lobby (?<lobbyName>[\\S]+) private"),
    ;
    private final String regex;

    Commands(String regex) {
        this.regex = regex;
    }

    public static Matcher getMatcher(String input, Commands command){
        Pattern pattern = Pattern.compile(command.regex);
        return pattern.matcher(input);
    }
}

