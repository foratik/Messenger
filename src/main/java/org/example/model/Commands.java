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

