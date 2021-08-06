package auth;

import chat.server.Server;
import chat.server.db.DBHandler;
import chat.server.db.User;
import db.User;
import server.NettyServer;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 1. unique login (accept)
 2. unknown user login/password ( reject)
 3. already logged in (reject)
 4. receive message to itself
 5. broadcast message
 */
public class UserHandler {
    private final NettyServer server;
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private String name;
    private User user;



    public UserHandler(NettyServer server, Socket socket) {
        this.server = server;
        try {
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());

            new Thread(() -> {
                listen();
            }).start();

        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    private void listen() {
        try {
            doAuth();
            readMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void doAuth() throws IOException {
        while (true) {
            /**
             * -auth login password
             * -creating message-history file if not exists
             * -appending last 100 lines if exists
             */
            try{
                String input = in.readUTF();
                if (input.startsWith("-auth")) {
                    String[] credentials = input.split("\\s");
                    this.user = server.getAuthenticationService().
                            findUserByCredentials(credentials[1], credentials[2]);
                    if (user != null) {
                        if (server.isNicknameFree(user.getNickname())) {
                            sendMessage("CMD: auth is OK.");
                            name = user.getNickname();
                            server.broadcast(name + " logged in.");
                            server.subscribe(this);
                            /**
                             * создание файла с историей сообщений
                             */
                            String filename = "history_" + this.user.getLogin() + ".txt";
                            File f = new File("src/main/java/chat/server/auth/chathistory/",filename);
                            if(!f.exists()){
                                f.createNewFile();
                            } else {
                                RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
                                long filelength = f.length() - 1;
                                randomAccessFile.seek(filelength);

                                int readedLines = 0;
                                int linesToRead = 100;

                                StringBuilder sb = new StringBuilder();

                                for (long ptr = filelength; ptr >= 0 ; ptr--) {
                                    randomAccessFile.seek(ptr);
                                    char c = (char) randomAccessFile.read();
                                    if(c == '\n'){
                                        readedLines++;
                                        if(readedLines == linesToRead){
                                            break;
                                        }
                                    }
                                    sb.append(c);
                                }
                                out.writeUTF(sb.reverse().toString());
                            }
                            return;
                        } else {
                            sendMessage("Current user already logged in.");
                        }
                    } else {
                        sendMessage("Unknown user. Incorrect login or password");
                    }
                } else {
                    Date time = new Date();
                    sendMessage("Invalid authentication request. " + time);
                }

            } catch (EOFException e){
                throw new RuntimeException("All clients left.",e);
            }

        }
    }
    //операция отправки сообщения в чат
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            //savingSentMsg(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // операция сохранения отправленного сообщения в файл истории данного чата.
   // public void savingSentMsg(String message) throws IOException {
        try{
            String filename = "history_" + this.user.getLogin() + ".txt";
            File f = new File("src/main/java/chat/server/auth/chathistory/",filename);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f, true));
            bufferedWriter.append(message).append("\n");
            bufferedWriter.flush();
        } catch (NullPointerException e){
            String filename = "history_of_invalid_authentification_requests.txt";
            File f = new File("src/main/java/chat/server/auth/chathistory/",filename);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f, true));
            bufferedWriter.append(message).append("\n");
            bufferedWriter.flush();
        }

    }


    /**
     1. private message
     */
    public void readMessage () throws IOException {
        while (true) {
            try{
                String message = in.readUTF();
                if (message.startsWith("/w")) {
                    String[] parts = message.split("\\s");
                    if (server.checkUserForExistance(parts[1])) {
                        String[] newmsg = new String[parts.length - 2];
                        System.arraycopy(parts, 2, newmsg, 0, parts.length - 2);
//saving sent msg to history(formatting this message)
                        StringBuilder historyMsg = new StringBuilder("You to " + parts[1] + ": ");
                        for (String s : newmsg) {
                            historyMsg.append(s).append(" ");
                        }
                } else if (message.startsWith("-quit")) {
                    sendMessage("You're offline.");
                    server.unsubscribe(this);
                    server.broadcast(name + " is offline");
                }
                else
                {
                    server.broadcast(name + ": " + message);
                }
            }   catch (IOException e){
                server.unsubscribe(this);
                throw new RuntimeException("There are no users left", e);
            }
        }

    }

    public String getName(){
        return name;
    }
}

