import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    int[][] gameBoard;

    private static final int EMPTY = 0;
    private static final int CROSS = 1;
    private static final int CIRCLE = 2;


    // Cross always plays first
    boolean isCrossTurn = true;

    String currentPlayer = "";



    private List<ServerRunnable> clients;

    public Server() {
        this.clients = new ArrayList<>();
        this.gameBoard = new int[3][3];
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerRunnable client = new ServerRunnable(clientSocket, this);
                clients.add(client);
                new Thread(client).start();
                broadcastMessage("Un nouveau client s'est connecté", client);
                if (clients.size() == 2) {
                    currentPlayer = clients.get(0).name;
                    broadcastMessage("Deux clients sont connectés, la partie peut commencer", null);
                    sendBoard();
                }
            }
        }
    }

    public void broadcastMessage(String message, ServerRunnable sender) {
        for (ServerRunnable client : clients) {
                client.sendMessage(message);

        }
    }

    public String sendOtherPlayerName(ServerRunnable sender) {
        for (ServerRunnable client : clients) {
            if (client != sender) {
                return client.name;
            }
        }
        return "erreur ):";
    }

    public void sendBoard() {
        String board = "\ta\tb\tc\n";

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                if (gameBoard[i][j] == EMPTY) {
                    board += j == 0 ? (i + 1) + "\t.\t" : ".\t"; // we print the number of the line if it's the beginning of the row
                } else if (gameBoard[i][j] == CROSS)  {
                    board += j == 0 ? (i + 1) + "\tX\t" : "X\t";
                } else {
                    board += j == 0 ? (i + 1) + "\tO\t" : "O\t";
                }
            }
            board += "\n";
        }
            broadcastMessage(board, null);
            broadcastMessage("C'est a " + currentPlayer + " de jouer", null);


    }



    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();

    }
}