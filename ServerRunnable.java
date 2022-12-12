import java.io.*;
import java.net.Socket;
public class ServerRunnable implements Runnable {

    private static final int EMPTY = 0;
    private static final int CROSS = 1;
    private static final int CIRCLE = 2;


    Server server;

    private Socket sockclient;
    String name;
    public ServerRunnable(Socket sockclient, Server server) {
        this.sockclient = sockclient;
        this.server = server;
    }

    public void run() {
        BufferedReader input;
        PrintWriter output;
        String message;


        try {
            input = new BufferedReader(new InputStreamReader(this.sockclient.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(this.sockclient.getOutputStream()), true);

            this.name = input.readLine();
            output.println("Bienvenue " + this.name + "!");

            while (true) {
                System.out.println("C'est à " + server.currentPlayer + " de jouer");
                if (server.currentPlayer == this.name) {
                    message = input.readLine();

                    while (!isValid(message)) {
                        sendMessage("Coup invalide");
                        message = input.readLine();
                    }

                    playBoard(message);

                    sendBoard();

                    System.out.println("Game terminée ? " + gameIsFinished());
                    if (gameIsFinished()) {
                        server.broadcastMessage("Partie terminée, " + this.name + " a gagné !", this);
                        break;
                    }
                    output.println(message);
                    if (message != null) {
                        System.out.println(message);
                    } else {
                        break;
                    }
                }


            }

                this.sockclient.close();

        } catch (IOException ex) {
            System.out.println(" déconnecté.");

        }

    }

    public void sendMessage(String message) {
        PrintWriter output;
        try {
            output = new PrintWriter(new OutputStreamWriter(this.sockclient.getOutputStream()), true);
            output.println(message);
        } catch (IOException ex) {
            System.out.println("Erreur lors de l'envoi du message : " + ex.getMessage());
        }
    }

    public void sendBoard() {
        String board = "\ta\tb\tc\n";

        for (int j = 0; j < server.gameBoard[0].length; j++) { // use j for the row index
            for (int i = 0; i < server.gameBoard.length; i++) { // use i for the column index
                if (server.gameBoard[i][j] == EMPTY) {
                    board += i == 0 ? (j + 1) + "\t.\t" : ".\t"; // we print the number of the line if it's the beginning of the row
                } else if (server.gameBoard[i][j] == CROSS)  {
                    board += i == 0 ? (j + 1) + "\tX\t" : "X\t";
                } else {
                    board += i == 0 ? (j + 1) + "\tO\t" : "O\t";
                }
            }
            board += "\n";
        }
        server.broadcastMessage(board, this);
    }


    /**
     * Plays a move on the board
     *
     * @param move The move to play
     * @return void
     */

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
        if (server.isCrossTurn) {
            server.gameBoard[coordinates[0]][coordinates[1]] = CROSS;
        } else {
            server.gameBoard[coordinates[0]][coordinates[1]] = CIRCLE;
        }
        server.isCrossTurn = !server.isCrossTurn;
        server.currentPlayer = server.sendOtherPlayerName(this);

    }

    public boolean isValid(String move) {
        int[] coordinates = convertStringToCoordinates(move);

        try {
            if (server.gameBoard[coordinates[0]][coordinates[1]] != EMPTY) {
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        return true;
    }

    public boolean gameIsFinished(){
        return !findEmptyCase() || findLine();
    }

    // La méthode isGameWon parcourt toutes les lignes, colonnes et diagonales du plateau de jeu d'un morpion, et additionne les valeurs des cases de chaque ligne, colonne et diagonale. Si la somme de toutes les valeurs des cases du plateau de jeu est égale à 18 ou à 36, cela signifie qu'une combinaison gagnante est présente, et la méthode retourne true. Sinon, elle retourne false.
    private boolean findLine() {
        for (int i = 0; i < 3; i++) {
            if(server.gameBoard[i][0] == 1 && server.gameBoard[i][1] == 1 && server.gameBoard[i][2] == 1) {
                return true;
            }
            if(server.gameBoard[i][0] == 2 && server.gameBoard[i][1] == 2 && server.gameBoard[i][2] == 2) {
                return true;
            }
            if(server.gameBoard[0][i] == 1 && server.gameBoard[1][i] == 1 && server.gameBoard[2][i] == 1) {
                return true;
            }
            if(server.gameBoard[0][i] == 2 && server.gameBoard[1][i] == 2 && server.gameBoard[2][i] == 2) {
                return true;
            }
            if(server.gameBoard[0][0] == 1 && server.gameBoard[1][1] == 1 && server.gameBoard[2][2] == 1) {
                return true;
            }
            if(server.gameBoard[0][0] == 2 && server.gameBoard[1][1] == 2 && server.gameBoard[2][2] == 2) {
                return true;
            }
            if(server.gameBoard[0][2] == 1 && server.gameBoard[1][1] == 1 && server.gameBoard[2][0] == 1) {
                return true;
            }
            if(server.gameBoard[0][2] == 2 && server.gameBoard[1][1] == 2 && server.gameBoard[2][0] == 2) {
                return true;
            }

        }
        return false;
    }


    private boolean findEmptyCase(){
        for (int i = 0; i < server.gameBoard.length; i++) {
            for (int j = 0; j < server.gameBoard[0].length; j++) {
                if( server.gameBoard[i][j] == 0) return true;
            }
        }
        return false;
    }

}
