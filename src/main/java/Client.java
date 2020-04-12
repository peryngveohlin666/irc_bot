import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    PrintWriter output;
    Scanner input;
    Socket socket;

    public void Connect(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new Scanner(socket.getInputStream());

        SendMessage("NICK spike-bot");
        SendMessage("User spike-bot 0 * : spike-bot");
        SendMessage("JOIN #help");

        while(input.hasNext()){
            System.out.println(input.nextLine());
            onMessageReceived(input.nextLine());
        }
    }

    public void SendMessage(String message){
        output.print(message + "\n");
        output.flush();
    }

    public void Disconnect() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public void onMessageReceived(String message){
        System.out.println(message);
        SendMessage("PRIVMSG #help :" +  message);
    }

}
