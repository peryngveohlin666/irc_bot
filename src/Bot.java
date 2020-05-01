import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Bot {
    PrintWriter output;
    Scanner input;
    Socket socket;
    String host = "";
    List<String> rooms = Arrays.asList("#cyberia", "#LainBot", "#/g/ang");
    String nickname = "Lain";
    String password = "WwXseAavja^6AG";

    public void connect(String host, int port, String password, String channel) throws IOException, JSONException {
        this.socket = new Socket(host, port);
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new Scanner(socket.getInputStream());
        this.host = host;
        this.password = password;

        sendMessage("NICK " + nickname);
        sendMessage("User " + nickname + " 0 * : " + nickname);
        for (int i = 0; i < this.rooms.size(); i++) {
            sendMessage("JOIN " + this.rooms.get(i));
        }
        joinChannel(channel);
        while (input.hasNext()) {
            onMessageReceived(input.nextLine());
        }
    }

    public void sendMessage(String message) {
        output.print(message + "\n");
        output.flush();
    }

    public void sendToChannel(String channel, String message) {
        sendMessage("PRIVMSG " + channel + " :" + message);
    }

    public void disconnect() throws IOException {
        sendMessage("QUIT");
    }

    public void onMessageReceived(String message) throws IOException, JSONException {

        if (message.startsWith("PING")) {
            pong(message);
        }

        if (message.split(":")[1].contains("353")) {
            String users[] = getUserList(message);
            for (int i = 0; i < users.length; i++) {
                createAFile(users[i]);
            }
        }


        if (cleanMessage(message).startsWith(this.nickname)) {
            respond(cleanMessage(message.replace(nickname, "")), getSendingLocation(message), message);
        }

        logMessages(getSendingUser(message) + ": " + cleanMessage(message) + " (In " + getSendingLocation(message) + ")");

        System.out.println(message);

    }

    public String cleanMessage(String message) {
        String[] out = message.split(":");
        String rt = "";
        if (out.length >= 3) {
            for (int i = 2; i < out.length; i++) {
                rt = rt + "" + out[i];
            }
        }
        return rt;
    }

    public void logMessages(String message) throws IOException {
        File file = new File("log.txt");
        FileWriter writer = new FileWriter("log.txt", true);
        writer.write(message);
        writer.write("\n");
        writer.close();
    }

    public void createAFile(String name) throws IOException {
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

    public void createUser(String username) throws IOException {
        writeToFile(username, "100;0;" + "new");
    }

    void updateCoins(String user, int new_coins) throws IOException {
        int coins = Integer.parseInt(readFromFile(user).split(";")[0]);
        int relationship = Integer.parseInt(readFromFile(user).split(";")[1]);
        String date = (readFromFile(user).split(";")[2]);
        coins = new_coins;
        writeToFile(user, coins + ";" + relationship + ";" + date);
    }

    void updateRelationship(String user, int new_relationship) throws IOException {
        int coins = Integer.parseInt(readFromFile(user).split(";")[0]);
        int relationship = Integer.parseInt(readFromFile(user).split(";")[1]);
        String date = (readFromFile(user).split(";")[2]);
        relationship = new_relationship;
        writeToFile(user, coins + ";" + relationship + ";" + date);
    }

    void dateToday(String user) throws IOException{
        int coins = Integer.parseInt(readFromFile(user).split(";")[0]);
        int relationship = Integer.parseInt(readFromFile(user).split(";")[1]);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String today = df.format(Calendar.getInstance().getTime()).split(" ")[0];
        writeToFile(user, coins + ";" + relationship + ";" + today);
    }

    public void respond(String incoming, String channel, String raw) throws IOException, JSONException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String info;
        String today;
        String date;
        int coins, relationship;
        switch (incoming) {
            case " help":
                sendToChannel(channel, "After typing my name; Type in stallman to get the lyrics for the free software song, Type in roll to get a random number between 0 and 100, beer to buy a pint! (this will cost you 10 coins), type daily to collect you daily coins, type coins to check the amount of coins you have, type kiss to try getting a kiss,");
                sendToChannel(channel, "type send + <Username> to sent the user coins, randomfact to get a random fact, join + #channelname to invite me to your channel (I log messages so please consider that before inviting me to your channel), dc <password> to make me disconnect, kick <user> <#channel> <password> to kick a user (only works if I am the channel operator)");
                sendToChannel(channel, "op <username> <#channel> <password> to make someone an operator in that channel (only works if I am an operator and obviously if you know the password) -I am implementing these to control my own channel since there are no NickServers and when i leave i lose my op status-");
                break;
            case " mert":
                sendToChannel(channel, "cutie");
                break;
            case " stallman":
                sendToChannel(channel, "Join us now and share the software " +
                        "You'll be free, hackers, you'll be free. " +
                        "Join us now and share the software; " +
                        "You'll be free, hackers, you'll be free. " +
                        "Hoarders can get piles of money, " +
                        "That is true, hackers, that is true. " +
                        "But they cannot help their neighbors; " +
                        "That's not good, hackers, that's not good. ");
                sendToChannel(channel, "When we have enough free software " +
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
                sendToChannel(channel,getSendingUser(raw) + " rolled " + Integer.toString(roll) + ".");
                break;
            case " beer":
                info = readFromFile(getSendingUser(raw));
                if(info.equals("")){
                    createUser(getSendingUser(raw));
                }
                coins = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[0]);
                relationship = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[1]);
                if (coins >= 10) {
                    sendToChannel(channel, getSendingUser(raw) + " Baught a pint! It cost you 10 coins! Your relationship with the bartender improved!");
                    coins = coins - 10;
                    relationship = relationship + 10;
                    updateCoins(getSendingUser(raw), coins);
                    updateRelationship(getSendingUser(raw), relationship);
                }
                else{
                    sendToChannel(channel, getSendingUser(raw) + " Is broke");
                }
                break;
            case " coins":
                info = readFromFile(getSendingUser(raw));
                if(info.equals("")){
                    createUser(getSendingUser(raw));
                }
                sendToChannel(channel, "You have: " + readFromFile(getSendingUser(raw)).split(";")[0] + " coins.");
                break;
            case " daily":
                info = readFromFile(getSendingUser(raw));
                today = df.format(Calendar.getInstance().getTime()).split(" ")[0];
                if(info.equals("")){
                    createUser(getSendingUser(raw));
                }
                coins = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[0]);
                relationship = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[1]);
                date = (readFromFile(getSendingUser(raw)).split(";")[2].split("\n")[0]);
               if (!date.equals(today)){
                sendToChannel(channel, getSendingUser(raw) + " Collected their daily coins of 100!");
                coins = coins + 100;
                relationship = relationship + 5;
                updateCoins(getSendingUser(raw), coins);
                updateRelationship(getSendingUser(raw), relationship);
                dateToday(getSendingUser(raw));
                }
                else{
                    sendToChannel(channel, getSendingUser(raw) + " You can't collect any more coins today");
                }
                break;
            case " kiss":
                info = readFromFile(getSendingUser(raw));
                if(info.equals("")){
                    createUser(getSendingUser(raw));
                }
                relationship = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[1]);
                if(relationship > 50) {
                    sendToChannel(channel,getSendingUser(raw) + " Got a kiss from this pretty lady!");
                }
                else{
                    sendToChannel(channel, getSendingUser(raw) + " Got slapped by this pretty lady trying to kiss her!");
                    updateRelationship(getSendingUser(raw), relationship - 10);
                }
                break;
            case " randomfact":
                String fact = randomFact();
                sendToChannel(channel, fact);
                break;

            default:
                String password = this.password;
                if(incoming.contains(" send") && incoming.split(" ").length==4){
                    try
                    {
                        info = readFromFile(getSendingUser(raw));
                        String name = incoming.split(" ")[2].toLowerCase();
                        int length = incoming.split(" ").length;
                        File receiver = new File(name);
                        String price = incoming.split(" ")[3];
                        if(receiver.exists()){
                            int coins_sender = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[0]);
                            int relationship_sender = Integer.parseInt(readFromFile(getSendingUser(raw)).split(";")[1]);
                            String date_sender = (readFromFile(getSendingUser(raw)).split(";")[2].split("\n")[0]);
                            if(info.equals("")){
                                createUser(getSendingUser(raw));
                            }
                            int coins_receiver = Integer.parseInt(readFromFile(name).split(";")[0]);
                            int relationship_receiver = Integer.parseInt(readFromFile(name).split(";")[1]);
                            String date_receiver = (readFromFile(name).split(";")[2].split("\n")[0]);
                            if (coins_sender >= Integer.parseInt(price) && Integer.parseInt(price) > 0 && !getSendingUser(raw).equals(name)) {
                                updateCoins(getSendingUser(raw), coins_sender - Integer.parseInt(price));
                                updateCoins(name, coins_receiver + Integer.parseInt(price));
                                sendToChannel(channel, getSendingUser(raw) + " sent " + receiver + " " + price + " coins. Don't use it for buying stuff like accela... Heard it is bad for you OwO");
                            } else if (Integer.parseInt(price) <= 0 || getSendingUser(raw).equals(name)) {
                                sendToChannel(channel, getSendingUser(raw) + " is sneaky.");

                            } else {
                                sendToChannel(channel, getSendingUser(raw) + " is broke!");
                            }
                        }
                        else{
                            sendToChannel(channel, getSendingUser(raw) + " Sorry , can not find your friend in my database.... Umm would you be kind enough to ask them to register (by writing daily) or double check their nickanme?");
                        }
                    }
                    catch (Exception e)
                    {
                        sendToChannel(channel, "This breaks my heart (you can't do this)");
                    }
                }
                else if(incoming.contains(" join #") && incoming.split(" ").length==3){
                    try {
                        String channel_name = incoming.split(" ")[2].toLowerCase();
                        joinChannel(channel_name);
                        sendToChannel(channel, "Joining " + channel_name);
                    }
                    catch (Exception e){
                        sendToChannel(channel, "Invalid option");
                    }

                }
                else if(incoming.contains(" kick") && incoming.split(" ").length==5) {
                    try {
                        String user_name = incoming.split(" ")[2].toLowerCase();
                        String channel_name = incoming.split(" ")[3].toLowerCase();
                        if(incoming.split(" ")[4].equals(password)) {
                            kickUser(user_name, channel_name);
                        }
                        else {
                            sendToChannel(channel, "Invalid password.");
                        }
                    } catch (Exception e) {
                        sendToChannel(channel, "Invalid option");
                    }
                }
                else if(incoming.contains(" op") && incoming.split(" ").length==5) {
                    try {
                        String user_name = incoming.split(" ")[2].toLowerCase();
                        String channel_name = incoming.split(" ")[3].toLowerCase();
                        if(incoming.split(" ")[4].equals(password)) {
                            opUser(user_name, channel_name);
                        }
                        else {
                            sendToChannel(channel, "Invalid password.");
                        }
                    } catch (Exception e) {
                        sendToChannel(channel, "Invalid option");
                    }
                }
                else if(incoming.contains(" topic") && incoming.split(" ").length>=5) {
                    try {
                        String channel_name = incoming.split(" ")[2];
                        String topic_string = "";
                        String[] topic_listed = incoming.split(" ");
                        int len = incoming.split(" ").length;
                        for(int i = 3; i < len -1; i++){
                            topic_string = topic_string + " " + topic_listed[i];
                        }

                        if(incoming.split(" ")[len-1].equals(password)) {
                            changeTopic(topic_string, channel_name);
                        }
                        else {
                            sendToChannel(channel, "Invalid password.");
                        }
                    } catch (Exception e) {
                        sendToChannel(channel, "Invalid option");
                    }
                }
                else if (incoming.contains(" dc") && incoming.split(" ").length>=3){
                    int len = incoming.split(" ").length;
                    if(incoming.split(" ")[len-1].equals(password)) {
                        disconnect();
                    }
                }
                else {
                    sendToChannel(channel, "Type help to get a hold of what I am capable of.");
                }
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

    public void pong(String message) throws IOException {
        sendMessage(message.replace("PING", "PONG"));
    }

    public String randomFact() throws JSONException, IOException {
        String jsonString = "";
        String fact = "";
        URL link = new URL("https://uselessfacts.jsph.pl/random.json?language=en");
        HttpURLConnection con = (HttpURLConnection) link.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            jsonString = response.toString();
        } else {
            fact = "An error has occurred while dealing with this request";
        }
        JSONObject obj = new JSONObject(jsonString);
        fact = (String) obj.get("text");
        return fact;

    }

    public void joinChannel(String channel){
        sendMessage("JOIN " + channel);
    }

    public void kickUser(String user, String channel){
        sendMessage("KICK " + channel + " " + user);
    }

    public void opUser(String user, String channel){
        sendMessage("MODE " + channel + " +o " + user);
    }

    public void changeTopic(String topic, String channel){
        sendMessage("TOPIC " + channel + " :" + topic);
    }

}
