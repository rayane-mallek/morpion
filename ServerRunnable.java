import java.io.*;
import java.net.Socket;
public class ServerRunnable implements Runnable {
    private Socket sockclient;
    private String name;
    public ServerRunnable(Socket sockclient) {
        this.sockclient = sockclient;
    }

    public void run() {
        BufferedReader input;
        PrintWriter output;
        String message;

        try {
            input = new BufferedReader(new InputStreamReader(this.sockclient.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(this.sockclient.getOutputStream()), true);

            this.name = input.readLine();
            if(this.name == null) {
                this.name = "Anonyme";
            }

                while (true) {
                    message = input.readLine();
                    if (message != null) {
                        System.out.println(name + ": " + message);
                    }else{
                        break;
                    }
                    output.println(message);
                }
                this.sockclient.close();

        } catch (IOException ex) {
            System.out.println(this.name + " déconnecté.");


        }

    }
}
