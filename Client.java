import java.net.Socket;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private BufferedReader input;
    private PrintWriter output;

    public Client() {
        while (true) {
            try {
                Socket sock = new Socket("localhost", 1234);
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
                break;
            } catch (IOException e) {
                System.err.println("Erreur lors de la création du socket : " + e.getMessage());
            }
        }
    }
    public void sendMessage(String message) {
        output.println(message);
    }

    public String receiveMessage() {
        try {
            return input.readLine();
        } catch (IOException e) {
            System.err.println("Erreur lors de la réception d'un message : " + e.getMessage());
            return "Error receive message";
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

    }

}
