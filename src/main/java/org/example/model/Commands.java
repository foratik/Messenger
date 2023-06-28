package org.example.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Commands {

    LOAD_CHAT("load chat (?<id>[\\d]+)"),
    NEW_CHAT("new chat (?<username>[\\S]+)"),
    SEND_MESSAGE("send message (?<text>.*)"),
    GET_NODES("k get nodes"),
    DELETE_TASK("k create delete task --name=(?<taskName>[\\S]+)"),
    DISABLE_NODE("k cordon node (?<node>[\\d]+)"),
    ENABLE_NODE("k uncordon node (?<node>[\\d]+)"),
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

