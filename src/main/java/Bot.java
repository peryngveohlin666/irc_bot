import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Bot {
    PrintWriter output;
    Scanner input;
    Socket socket;
    String host = "";
    List<String> rooms = Arrays.asList("#cyberia", "#help", "#spikeBot");
    String nickname = "spikeBot";

    public void Connect(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new Scanner(socket.getInputStream());
        this.host = host;

        SendMessage("NICK " + nickname);
        SendMessage("User "+ nickname +" 0 * : " + nickname);
        for(int i = 0; i < this.rooms.size(); i++) {
            SendMessage("JOIN " + this.rooms.get(i));
        }
        while(input.hasNext()){
            onMessageReceived(input.nextLine());
        }
    }

    public void SendMessage(String message){
        output.print(message + "\n");
        output.flush();
    }

    public void SendToChannel(String channel, String message){
        SendMessage("PRIVMSG " + channel + " :" + message);
    }

    public void Disconnect() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public void onMessageReceived(String message) throws IOException {

        if (message.startsWith("PING")) {
                Pong(message);
        }


        if(CleanMessage(message).startsWith(this.nickname)){
           Respond(CleanMessage(message.replace(nickname, "")), getSendingLocation(message), message);
        }

        LogMessages(getSendingUser(message) + ": " + CleanMessage(message) + " (In " + getSendingLocation(message) + ")");

        System.out.println(message);

    }

    public String CleanMessage(String message){
        String[] out = message.split(":");
        String rt = "";
        if (out.length >= 3) {
            for (int i=2; i < out.length; i++){
                rt = rt + "" + out[i];
            }
        }
        return rt;
    }

    public void LogMessages(String message) throws IOException {
        File file = new File("log.txt");
        FileWriter writer = new FileWriter("log.txt", true);
        writer.write(message);
        writer.write("\n");
        writer.close();
    }

    public void Respond(String incoming, String channel, String raw){
        switch(incoming) {
            case " help":
                SendToChannel(channel, "Type in stallman to get the lyrics for the free software song, Type in roll to get a random number between 0 and 100, beer to buy a pint!");
                break;
            case " mert":
                SendToChannel(channel, "cutie");
                break;
            case " stallman":
                SendToChannel(channel, "Join us now and share the software " +
                        "You'll be free, hackers, you'll be free. " +
                        "Join us now and share the software; " +
                        "You'll be free, hackers, you'll be free. " +
                        "Hoarders can get piles of money, " +
                        "That is true, hackers, that is true. " +
                        "But they cannot help their neighbors; " +
                        "That's not good, hackers, that's not good. ");
                SendToChannel(channel, "When we have enough free software " +
                        "At our call, hackers, at our call, " +
                        "We'll kick out those dirty licenses " +
                        "Ever more, hackers, ever more. " +
                        "Join us now and share the software; " +
                        "You'll be free, hackers, you'll be free. " +
                        "Join us now and share the software; " +
                        "You'll be free, hackers, you'll be free.");
                break;
            case " roll":
                Random rand = new Random();
                int roll = rand.nextInt(101);
                SendToChannel(channel, Integer.toString(roll));
                break;
            case " beer":
                SendToChannel(channel, getSendingUser(raw) + " Baught a pint!");
                break;
            default :
                SendToChannel(channel, "Type help to get a hold of what I am capable of! Also join #cyberia");
        }
    }

    public String getSendingLocation(String message){
        String room = "";
        if(message.split("PRIVMSG").length>=2 && message.split(":")[1].split("#").length >=2){
            room = message.split(":")[1].split("PRIVMSG ")[1];
        }
        else if (message.split("PRIVMSG ").length == 2) {
            room = getSendingUser(message);
        }
        return room;
    }

    public String getSendingUser(String message){
        String user =  message.split(":")[1].split("!")[0];
        return user;
    }

    public void Pong(String message) throws IOException {
        SendMessage(message.replace("PING", "PONG"));
    }

}
