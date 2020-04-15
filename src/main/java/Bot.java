import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
        SendMessage("User " + nickname + " 0 * : " + nickname);
        for (int i = 0; i < this.rooms.size(); i++) {
            SendMessage("JOIN " + this.rooms.get(i));
        }
        while (input.hasNext()) {
            onMessageReceived(input.nextLine());
        }
    }

    public void SendMessage(String message) {
        output.print(message + "\n");
        output.flush();
    }

    public void SendToChannel(String channel, String message) {
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

        if (message.split(":")[1].contains("353")) {
            String users[] = getUserList(message);
            for (int i = 0; i < users.length; i++) {
                CreateAFile(users[i]);
            }
        }


        if (CleanMessage(message).startsWith(this.nickname)) {
            Respond(CleanMessage(message.replace(nickname, "")), getSendingLocation(message), message);
        }

        LogMessages(getSendingUser(message) + ": " + CleanMessage(message) + " (In " + getSendingLocation(message) + ")");

        System.out.println(message);

    }

    public String CleanMessage(String message) {
        String[] out = message.split(":");
        String rt = "";
        if (out.length >= 3) {
            for (int i = 2; i < out.length; i++) {
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

    public void CreateAFile(String name) throws IOException {
        File file = new File(name);
        FileWriter writer = new FileWriter(name, true);
        writer.close();
    }

    public void writeToFile(String name, String text) throws IOException {
        File file = new File(name);
        List<String> lines = Files.readAllLines(file.toPath());
        FileWriter writer = new FileWriter(name);
        writer.write(text);
        writer.write("\n");
        writer.close();
    }

    public String readFromFile(String name) throws IOException {
        FileReader fr = new FileReader(name);
        int i = 0;
        String contents = "";
        while ((i = fr.read()) != -1) {
            contents = contents + (char) i;
        }
        return contents;
    }

    public String[] getUserList(String message) {
        String users[] = message.split(":")[2].split(" ");
        System.out.println(users);
        return users;
    }

    public void Respond(String incoming, String channel, String raw) throws IOException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String info;
        String today;
        String date;
        int coins, relationship;
        switch (incoming) {
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
                info = readFromFile(getSendingUser(raw));
                today = df.format(Calendar.getInstance().getTime());
                if(info.equals("")){
                    writeToFile(getSendingUser(raw), "100;0;" + today);
                }
                SendToChannel(channel, getSendingUser(raw) + " Baught a pint! It cost you 10 coins!");
                coins = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[0]);
                relationship = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[1]);
                date = (readFromFile(getSendingUser(raw)).split(";")[2]);
                coins = coins - 10;
                relationship = relationship + 10;
                writeToFile(getSendingUser(raw), coins + ";" + relationship + ";" + date);
                break;
            case " coins":
                info = readFromFile(getSendingUser(raw));
                today = df.format(Calendar.getInstance().getTime());
                if(info.equals("")){
                    writeToFile(getSendingUser(raw), "100;0;" + today);
                }
                SendToChannel(channel, "You have: " + readFromFile(getSendingUser(raw)).split(";")[0] + " coins.");
                break;
            case " daily":
                info = readFromFile(getSendingUser(raw));
                today = df.format(Calendar.getInstance().getTime()).split(" ")[0];
                if(info.equals("")){
                    writeToFile(getSendingUser(raw), "100;0;" + today);
                }
                coins = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[0]);
                relationship = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[1]);
                date = (readFromFile(getSendingUser(raw)).split(";")[2].split("\n")[0]);
                System.out.println(date);
                System.out.println(today);
               if (!date.equals(today)){
                SendToChannel(channel, getSendingUser(raw) + " Collected their daily coins of 100!");
                coins = coins + 100;
                relationship = relationship + 1;
                writeToFile(getSendingUser(raw), coins + ";" + relationship + ";" + today);
                }
                else{
                    SendToChannel(channel, getSendingUser(raw) + " You can't collect any more coins today");
                }
                break;
            default:
                SendToChannel(channel, "Type help to get a hold of what I am capable of! Also join #cyberia");
        }
    }

    public String getSendingLocation(String message) {
        String room = "";
        if (message.split("PRIVMSG").length >= 2 && message.split(":")[1].split("#").length >= 2) {
            room = message.split(":")[1].split("PRIVMSG ")[1];
        } else if (message.split("PRIVMSG ").length == 2) {
            room = getSendingUser(message);
        }
        return room;
    }

    public String getSendingUser(String message) {
        String user = message.split(":")[1].split("!")[0];
        return user;
    }

    public void Pong(String message) throws IOException {
        SendMessage(message.replace("PING", "PONG"));
    }

}
