import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Bot bot = new Bot();
        bot.connect("127.0.0.1", 7777);
    }
}
