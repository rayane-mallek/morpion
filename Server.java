import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private static final int EMPTY = 0;
    private static final int CROSS = 1;
    private static final int CIRCLE = 2;

    private int[][] gameBoard;
    private List<ServerRunnable> clientsThreads;

    // Cross always plays first
    private boolean isCrossTurn = true;

    public Server() {
        clientsThreads = new ArrayList<>();
        this.gameBoard = new int[3][3];
    }
    public void start() {
        ServerSocket sockserv = null;
        String move;

        try {
            sockserv = new ServerSocket(1234);
            while(true) {
                try {
                    Socket sockcli = sockserv.accept();
                    ServerRunnable t = new ServerRunnable(sockcli, this);
                    clientsThreads.add(t);
                    new Thread(t).start();

                    sendBoard();

                    do {
                        move = "a1";
                        playBoard(move);
                    } while (!isValid(move));

                    sendBoard();
                    gameIsFinished();
                    System.out.println("sortie while");
                } catch (IOException ex){
                    System.out.println("Erreur lors de l'acceptation d'un client : " + ex.getMessage());
                }
            }

        } catch (IOException e){
            System.out.println("Erreur de création du serveur");
        } finally {
            try {
                sockserv.close();
            } catch (IOException ex) {

            }
        }
    }

    /**
     * Prints the board, with the coordinates (A B C for the horizontal axis, 1 2 3 for the vertical axis)
     *
     * @return void
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

        System.out.println("a");
        for (ServerRunnable client : clientsThreads) {
            client.sendMessage(board);
        }
        System.out.println("b");
    }

    public void testMsg(String msg){
        for (ServerRunnable client : clientsThreads) {
            client.sendMessage(msg);
        }
    }

    private int[] convertStringToCoordinates(String move) {
        char[] choiceChars = move.toCharArray();
        char axisX = choiceChars[0];
        char axisY = choiceChars[1];
        int a = 0;
        int b = 0;

        //compare the char to the axis
        switch (axisX){
            case 'a':
                a = 0;
                break;
            case 'b':
                a = 1;
                break;
            default:
                a = 2;
                break;
        }
        b = Character.getNumericValue(axisY)-1;
        int[] coordinates = {a, b};

        return coordinates;
    }

    public void playBoard(String choice){
        int[] coordinates = convertStringToCoordinates(choice);

        if (isCrossTurn) {
            gameBoard[coordinates[0]][coordinates[1]] = CROSS;
        } else {
            gameBoard[coordinates[0]][coordinates[1]] = CIRCLE;
        }
        isCrossTurn = !isCrossTurn;
    }

    public boolean isValid(String move) {
        int[] coordinates = convertStringToCoordinates(move);

        if (gameBoard[coordinates[0]][coordinates[1]] != EMPTY) {
            return false;
        }

        return true;
    }

    public boolean gameIsFinished(){
         return !findEmptyCase();
    }

    // La méthode isGameWon parcourt toutes les lignes, colonnes et diagonales du plateau de jeu d'un morpion, et additionne les valeurs des cases de chaque ligne, colonne et diagonale. Si la somme de toutes les valeurs des cases du plateau de jeu est égale à 18 ou à 36, cela signifie qu'une combinaison gagnante est présente, et la méthode retourne true. Sinon, elle retourne false.
    private boolean findLine(){
        int sum = 0;
        // Check rows and columns
        for (int i = 0; i < gameBoard.length; i++) {
            sum += gameBoard[i][0] + gameBoard[i][1] + gameBoard[i][2];
            sum += gameBoard[0][i] + gameBoard[1][i] + gameBoard[2][i];
        }

        // Check diagonals
        sum += gameBoard[0][0] + gameBoard[1][1] + gameBoard[2][2];
        sum += gameBoard[0][2] + gameBoard[1][1] + gameBoard[2][0];

        return sum == 18 || sum == 36;
    }

    private boolean findEmptyCase(){
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                if( gameBoard[i][j] == 0) return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
