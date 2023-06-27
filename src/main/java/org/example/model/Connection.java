package org.example.model;


import org.example.Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class Connection extends Thread {
    Socket socket;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Set<String> subscribtionSet = new HashSet<>();

    public User currentUser;

    public Connection(Socket socket) throws IOException {
        System.out.println("New connection form: " + socket.getInetAddress() + ":" + socket.getPort());
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        String string = null;
        while (string == null) {
            string = dataInputStream.readUTF();
        }
        if (string.contains("client")) {

        }
    }

    @Override
    public synchronized void run() {
        super.run();
            while (true) {
                try {
                    String input = dataInputStream.readUTF();
                    Matcher matcher;
                    String output = null;
                    if ((matcher = Commands.getMatcher(input, Commands.NEW_TASK_WITH_WORKER)).find()) {

                        dataOutputStream.writeUTF(output);
                    } else if ((matcher = Commands.getMatcher(input, Commands.NEW_TASK)).find()) {

                        dataOutputStream.writeUTF(output);
                    } else if (Commands.getMatcher(input, Commands.GET_TASKS).matches()) {

                        dataOutputStream.writeUTF(output);
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
}