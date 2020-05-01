import org.json.JSONException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, JSONException {
        String ip = "127.0.0.1";
        String password = "WwXseAavja^6AG";
        int port = 7777;
        try {
            if (args.length >= 1) {
                ip = args[0];
            }
            if (args.length >= 2) {
                port = Integer.parseInt(args[1]);
            }
            if (args.length >= 3) {
                password = args[2];
            }
        }
        catch (Exception e){
        System.out.println("Please format the values like ip + port afterwise if you would like the bot to join to a specific channel invite it over the irc server by writing <botname> <invite> <channelname>, you can also set the password by passing in a 3rd argument instead of the default one");
        }
        Bot bot = new Bot();
        bot.connect(ip, port, password);
    }
}
