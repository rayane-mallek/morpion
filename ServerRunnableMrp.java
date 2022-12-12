import java.io.*;
import java.net.Socket;
public class ServerRunnable implements Runnable {
    private Server server;
    private Socket sockclient;
    private String name;

    BufferedReader input;
    PrintWriter output;

    public ServerRunnable(Socket sockclient, Server server) {
        this.server = server;
        this.sockclient = sockclient;

        try {
            input = new BufferedReader(new InputStreamReader(this.sockclient.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(this.sockclient.getOutputStream()), true);
            System.out.printf("Client %s connected%n", sockclient.getInetAddress());
        } catch (IOException e) {
            System.err.println("Erreur lors de la création des flux : " + e.getMessage());
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                String message = input.readLine();
                sendMessage(message);
                findLine(message);

            }
        } catch (IOException ex) {
            System.out.println(this.name + " disconnected. (run of ServerRunnable)");
        } finally {
            try {
                input.close();
                output.close();
                sockclient.close();
            } catch (IOException ex) {
                System.err.println("Error closing socket: " + ex.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

}
