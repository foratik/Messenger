package org.example.model;


import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
    public void run() {
        super.run();
        while (true) {
            try {
                Data.saveChats();
                refreshPrivateChats();
                String input = dataInputStream.readUTF();
                Matcher matcher;
                String output = "";
                if ((matcher = Commands.getMatcher(input, Commands.NEW_CHAT)).find()) {
                    newChat(matcher);
                } else if ((matcher = Commands.getMatcher(input, Commands.LOAD_CHAT)).find()) {
                    loadCurrentChat(matcher);
                } else if ((matcher = Commands.getMatcher(input, Commands.SEND_MESSAGE)).matches()) {
                    sendMessage(matcher);
                    refreshPrivateChats();
                } else if ((matcher = Commands.getMatcher(input, Commands.DELETE_FOR_ALL)).find()) {
                    deleteMessageForALl(matcher);
                    refreshPrivateChats();
                } else if ((matcher = Commands.getMatcher(input, Commands.EDIT_MESSAGE)).find()) {
                    editMessage(matcher);
                    refreshPrivateChats();
                } else if ((matcher = Commands.getMatcher(input, Commands.NEW_ROOM)).find()) {
                    newRoom(matcher);
                } else if ((matcher = Commands.getMatcher(input, Commands.ADD_MEMBER)).find()) {
                    addMember(matcher);
                } else {
                    System.out.println(output = "invalid command !");
                    dataOutputStream.writeUTF(output + input);
                }
            } catch (IOException e) {
                System.out.println("connection lost with client!");
                Server.allConnections.remove(this);
                break;
            }
        }
    }


    public synchronized void loadChats(User user) {
        String username = user.getUsername();
        ArrayList<Chat> chats = new ArrayList<>();
        for (Chat chat : Data.getAllChats()) {
            for (User member : chat.members) {
                if (member!=null&&member.getUsername().equals(username)) {
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
    }

    public void loadCurrentChat(Matcher matcher) {
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

    public void newChat(Matcher matcher) {
        String username = matcher.group("username");
        if (username.equals("null"))
            return;
        ArrayList<User> users = new ArrayList<>();
        users.add(currentUser);
        users.add(Data.findUserWithUsername(username));
        currentChat = new Chat("Private Chat", new ArrayList<>(), users);
        System.out.println("new chat created with " + username);
        try {
            dataOutputStream.writeUTF("new chat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadChats(currentUser);
    }

    public static void refreshPrivateChats() {
        for (Connection connection : Server.allConnections) {
            if (connection.currentChat != null)
                for (Message message : connection.currentChat.messages)
                    if (!message.sender.getUsername().equals(connection.currentUser.getUsername()))
                        message.isSeen = true;
            try {
                connection.dataOutputStream.writeUTF("refresh");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            connection.loadChats(connection.currentUser);
        }
        System.out.println(Server.allConnections.size() + " connections refresh !");
    }

    public synchronized void sendMessage(Matcher matcher) {

        if (currentChat == null)
            return;
        String text = matcher.group("text");
        HashMap<String, Boolean> hashMap = new HashMap<>();
        for (User user : currentChat.members)
            hashMap.put(user.getUsername(), false);
        Message message = new Message(currentUser, text, hashMap);
        currentChat.messages.add(message);
        Data.saveChats();
        try {
            dataOutputStream.writeUTF("new message");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadChats(currentUser);

    }

    public void deleteMessageForALl(Matcher matcher) {
        String text = matcher.group("text");
        Message selectedMessage = null;
        for (Message message : currentChat.messages) {
            if (message.sender.getUsername().equals(currentUser.getUsername()))
                if (message.text.equals(text)) {
                    selectedMessage = message;
                    break;
                }
        }

        currentChat.messages.remove(selectedMessage);

        try {
            dataOutputStream.writeUTF("delete message");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadChats(currentUser);

    }

    public void editMessage(Matcher matcher) {
        String currentText = matcher.group("currentText");
        String newText = matcher.group("newText");

        ArrayList<Message> messages = new ArrayList<>();

        Message selectedMessage = null;
        for (Message message : currentChat.messages) {
            if (message.sender.getUsername().equals(currentUser.getUsername()))
                if (message.text.equals(currentText)) {
                    selectedMessage = message;
                    selectedMessage.text = newText;
                    messages.add(message);
                } else messages.add(message);
            else messages.add(message);
        }

        currentChat.messages = messages;

        try {
            dataOutputStream.writeUTF("edit message");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadChats(currentUser);

    }

    public void newRoom(Matcher matcher){
        String name = matcher.group("name");

        for (Chat allChat : Data.getAllChats()) {
          if (allChat.name.equals(name)){
              System.out.println("duplicate name");
              return;
          }
        }
        ArrayList<User> users = new ArrayList<>();
        users.add(currentUser);
        currentChat = new Chat(name, new ArrayList<>(), users);

        System.out.println("new chat room : "+ name );

        try {
            dataOutputStream.writeUTF("new room");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadChats(currentUser);
    }

    public void addMember(Matcher matcher){
        String username = matcher.group("username");
        if (currentChat.type!=Type.ROOM)
            return;
        for (User member : currentChat.members) {
            if (member.getUsername().equals(username))
                return;
        }

        currentChat.members.add(Data.findUserWithUsername(username));

        System.out.println(username+" added to "+currentChat.name);

        try {
            dataOutputStream.writeUTF("new member");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadChats(currentUser);
    }
}