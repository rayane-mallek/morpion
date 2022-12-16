import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
 * Classe représentant un serveur de jeu de morpion en réseau.
 * 
 * Le serveur peut accepter jusqu'à deux clients se connectant simultanément, et gère leur communication
 * pour jouer une partie de morpion.
 * 
 * Le serveur utilise un plateau de jeu sous forme d'un tableau à deux dimensions pour stocker l'état du jeu.
 * Les valeurs possibles sont :
 * - EMPTY (0) : case vide
 * - CROSS (1) : case contenant un "croix"
 * - NOUGHT (2) : case contenant un "rond"
 * 
 * Le serveur utilise également un booléen pour indiquer qui doit jouer à chaque tour :
 * - Si isCrossTurn est vrai, c'est au joueur "croix" de jouer
 * - Si isCrossTurn est faux, c'est au joueur "rond" de jouer
 * 
 * La classe Server implémente également une liste de clients connectés (classe ServerRunnable) et possède
 * une méthode broadcastMessage permettant d'envoyer un message à tous les clients connectés.
 * 
 * @see ServerRunnable
 */
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

    /**
    * Envoie un message à tous les clients
    */
    public void broadcastMessage(String message, ServerRunnable sender) {
        for (ServerRunnable client : clients) {
                client.sendMessage(message);

        }
    }

    /**
    * Envoie le nom du client 1 au client 2
    */
    public String sendOtherPlayerName(ServerRunnable sender) {
        for (ServerRunnable client : clients) {
            if (client != sender) {
                return client.name;
            }
        }
        return "erreur ):";
    }

    /**
    * Envoie la grille du morpion aux clients
    */
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