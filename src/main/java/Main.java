import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

    Socket socket = new Socket("selsey.nsqdc.city.ac.uk", 6667);

    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
    Scanner input = new Scanner(socket.getInputStream());

    output.print("NICK spike-bot \r\n");
    output.flush();

    output.print("User spike-bot 0 * : spike-bot \r\n");
    output.flush();

        output.print("JOIN #help \r\n");
        output.flush();

    while(input.hasNext()){
        System.out.println(input.nextLine());
        output.print("PRIVMSG #help HI \r\n");
        output.flush();
    }
        input.close();
        output.close();
        socket.close();
    }

}
