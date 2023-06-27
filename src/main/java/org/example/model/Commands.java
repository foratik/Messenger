package org.example.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Commands {

    NEW_TASK("k create task --name=(?<taskName>[\\S]+)"),
    NEW_TASK_WITH_WORKER("k create task --name=(?<taskName>[\\S]+) --node=(?<node>[\\d]+)"),
    GET_TASKS("k get tasks"),
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

