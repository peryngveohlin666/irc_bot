import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.Connect("selsey.nsqdc.city.ac.uk", 6667);
    }
}
