package org.example.model;

import java.util.ArrayList;

public class Chat {
    public String name;
    public int id;
    public ArrayList<Message> messages;
    public ArrayList<User> members;
    public Type type;

    public Chat(String name, ArrayList<Message> messages, ArrayList<User> members) {
        this.name = name;
        this.messages = messages;
        this.members = members;
        if(members.size()== Data.getUsers().size())
            this.type = Type.PUBLIC;
        else if (members.size()==2)
            this.type = Type.PRIVATE;
        else this.type = Type.ROOM;
        this.id = Data.getAllChats().size()+1;
        Data.getAllChats().add(this);
    }
}

enum Type{
    PUBLIC,
    PRIVATE,
    ROOM
}
