package org.example.model;


import com.google.gson.Gson;
import org.example.Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;

public class Connection extends Thread {
    Socket socket;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Set<String> subscribtionSet = new HashSet<>();

    public User currentUser;
    public Chat currentChat;

    public Connection(Socket socket) throws IOException {
        System.out.println("New connection form: " + socket.getInetAddress() + ":" + socket.getPort());
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        String string = null;
        while (string == null) {
            string = dataInputStream.readUTF();
        }


        currentUser = Data.findUserWithUsername(string);
//        ArrayList<User> users = new ArrayList<>();
//        users.add(Data.findUserWithUsername("saeed"));
//        users.add(Data.findUserWithUsername("ali"));
//        HashMap<String,Boolean> hashMap = new HashMap<>();
//        hashMap.put("saeed",false);
//        hashMap.put("ali",false);
//        ArrayList<Message> messages = new ArrayList<>();
//        Message message = new Message(currentUser,"salam",hashMap);
//        messages.add(message);
//        messages.add(new Message(currentUser,"salam2222",hashMap));
//        messages.add(new Message(currentUser,"bye !",hashMap));
//        new Chat("first chat",messages,users);

        loadChats(currentUser);

    }

    @Override
    public synchronized void run() {
        super.run();
            while (true) {
                try {
                    Data.saveChats();
                    String input = dataInputStream.readUTF();
                    Matcher matcher;
                    String output = "";
                    if ((matcher = Commands.getMatcher(input, Commands.NEW_CHAT)).find()) {
                        newChat(matcher);
                        //dataOutputStream.writeUTF(output);
                    } else if ((matcher = Commands.getMatcher(input, Commands.LOAD_CHAT)).find()) {
                        loadCurrentChat(matcher);
                        //dataOutputStream.writeUTF(output);
                    } else if ((matcher=Commands.getMatcher(input, Commands.SEND_MESSAGE)).matches()) {
                        sendMessage(matcher);
                        //dataOutputStream.writeUTF(output);
                    } else if (Commands.getMatcher(input, Commands.GET_NODES).matches()) {
                        dataOutputStream.writeUTF(output);
                    } else if ((matcher = Commands.getMatcher(input, Commands.DELETE_TASK)).find()) {
                        dataOutputStream.writeUTF(output);
                    } else if ((matcher = Commands.getMatcher(input, Commands.DISABLE_NODE)).find()) {
                        dataOutputStream.writeUTF(output);
                    } else if ((matcher = Commands.getMatcher(input, Commands.ENABLE_NODE)).find()) {
                        dataOutputStream.writeUTF(output);
                    } else {
                        System.out.println(output = "invalid command !");
                        dataOutputStream.writeUTF(output);
                    }
                } catch (IOException e) {
                    System.out.println("connection lost with client!");
                    break;
                }
            }
    }


    public synchronized void loadChats(User user){
        String username = user.getUsername();
        ArrayList<Chat> chats = new ArrayList<>();
        for (Chat chat : Data.getAllChats()) {
            for (User member : chat.members) {
                if (member.getUsername().equals(username)){
                    chats.add(chat);
                    try {
                        dataOutputStream.writeUTF(new Gson().toJson(chat));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        }
        try {
            dataOutputStream.writeUTF("/end/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return new Gson().toJson(chats);
    }

    public void loadCurrentChat(Matcher matcher){
        int id = Integer.parseInt(matcher.group("id"));
        currentChat = Data.findChatWithId(id);
        for (Message message : currentChat.messages)
            if (!message.sender.getUsername().equals(currentUser.getUsername()))
                message.isSeen = true;

        try {
            dataOutputStream.writeUTF("load chat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadChats(currentUser);
    }

    public void newChat(Matcher matcher){
        String username = matcher.group("username");
        ArrayList<User> users = new ArrayList<>();
        users.add(currentUser);
        users.add(Data.findUserWithUsername(username));
        currentChat = new Chat("Private Chat",new ArrayList<>(),users);
        System.out.println("new chat created with "+username);
        try {
            dataOutputStream.writeUTF("new chat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadChats(currentUser);
    }

    public void sendMessage(Matcher matcher){
        if (currentChat==null)
            return;
        String text = matcher.group("text");
        HashMap<String,Boolean> hashMap = new HashMap<>();
        for (User user : currentChat.members)
            hashMap.put(user.getUsername(),false);
        Message message = new Message(currentUser,text,hashMap);
        currentChat.messages.add(message);
        try {
            dataOutputStream.writeUTF("new message");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadChats(currentUser);

    }
}