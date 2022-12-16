import java.io.*;
import java.net.Socket;

/**
 * Classe représentant un joueur du jeu Morpion.
 * Cette classe hérite de la classe Thread et s'exécute en parallèle du serveur pour chaque joueur connecté.
 * Elle gère la communication avec le joueur et son tour de jeu dans la partie en cours.
 */
public class ServerRunnable implements Runnable {

    // Constantes pour représenter l'état d'une case de la grille de jeu
    private static final int EMPTY = 0;
    private static final int CROSS = 1;
    private static final int CIRCLE = 2;

    // Référence vers le serveur
    Server server;

    private Socket sockclient;
    String name;

    /**
     * Constructeur de la classe ServerRunnable
     * @param sockclient Socket de communication avec le client
     * @param server Référence vers le serveur
     */
    public ServerRunnable(Socket sockclient, Server server) {
        this.sockclient = sockclient;
        this.server = server;
    }

    /**
     * Méthode exécutée lorsque le thread est lancé.
     * Elle gère la communication avec le client et son tour de jeu dans la partie en cours.
     */
    public void run() {
        BufferedReader input;
        PrintWriter output;
        String message;


        try {
            // Création des objets de lecture et d'écriture sur le socket

            input = new BufferedReader(new InputStreamReader(this.sockclient.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(this.sockclient.getOutputStream()), true);

            this.name = input.readLine();
            output.println("Bienvenue " + this.name + "!");
            

            // Boucle de jeu
            while (true) {
                message = input.readLine();
                if (server.currentPlayer == this.name) {

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

    /*
    * Envoie un message à un client
    */
    public void sendMessage(String message) {
        PrintWriter output;
        try {
            output = new PrintWriter(new OutputStreamWriter(this.sockclient.getOutputStream()), true);
            output.println(message);
        } catch (IOException ex) {
            System.out.println("Erreur lors de l'envoi du message : " + ex.getMessage());
        }
    }

    /**
    * Envoie la grille du morpion aux clients
    */
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
        server.broadcastMessage("C'est a " + server.currentPlayer + " de jouer", this);

    }

    /**
    * Converti la chaine de caractères passée par un client en coordonnées utilisables par les autres méthodes
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

    /**
    * Cette méthode permet de jouer un coup sur le plateau de jeu.
    * 
    * @param choice La chaîne de caractères représentant les coordonnées du coup joué (ex : "a1", "b2", etc.)
    */
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

    /**
    * Cette méthode permet de vérifier si un coup est valide, c'est-à-dire s'il respecte les règles du jeu et si la case visée est disponible.
    * 
    * @param move La chaîne de caractères représentant les coordonnées du coup joué (ex : "A1", "B2", etc.)
    * @return true si le coup est valide, false
    */
    public boolean isValid(String move) {
        if (move.length() != 2) {
            return false;
        }
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

    /**
    * Vérifie si la partie est terminée
    */
    public boolean gameIsFinished(){
        return !findEmptyCase() || findLine();
    }

    /**
    * Méthode qui vérifie s'il y a une ligne complète sur la grille de jeu (c'est-à-dire si un joueur a gagné).
    * @return true si une ligne est complète, false sinon
    */
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


    /**
    * Cette méthode permet de vérifier s'il reste des cases vides sur le plateau de jeu.
    * 
    * @return true s'il reste au moins une case vide sur le plateau, false sinon.
    */
    private boolean findEmptyCase(){
        for (int i = 0; i < server.gameBoard.length; i++) {
            for (int j = 0; j < server.gameBoard[0].length; j++) {
                if( server.gameBoard[i][j] == 0) return true;
            }
        }
        return false;
    }

}
