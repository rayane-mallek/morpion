import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        ServerSocket sockserv = null;

        try {
            sockserv = new ServerSocket(1234);
            while(true) {
                try {
                    Socket sockcli = sockserv.accept();
                    ServerRunnable t = new ServerRunnable(sockcli);
                    new Thread(t).start();
                } catch (IOException ex){

                }
            }

        } catch (IOException ex){

        } finally {
            try {
                sockserv.close();
            } catch (IOException ex) {

            }
        }
    }
}
