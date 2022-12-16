import java.net.Socket;
import java.io.*;

/**
 * Classe représentant un client le morpion.
 * 
 * Le client peut se connecter au serveur, entrer son pseudo, envoyer et recevoir des messages du serveur.
 * Il peut également quitter la session en saisissant la commande "exit".
 */
public class Client {
    public static void main(String[] args) {
        BufferedReader input;
        PrintWriter output;

        try {
            Socket sockcli = new Socket("127.0.0.1" ,1234);
            BufferedReader inputPseudo = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Entrez un pseudo : ");
            String pseudo = inputPseudo.readLine();
            input = new BufferedReader(new InputStreamReader(sockcli.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(sockcli.getOutputStream()), true);
            output.println(pseudo);

            // Création d'un thread pour lire les messages du serveur
            new Thread(() -> {
                try {
                    String message;
                    while ((message = input.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException ex) {
                    // Gestion des erreurs
                    System.out.println("Une erreur est survenue lors de la lecture des messages du serveur : " + ex.getMessage());
                }
            }).start();

            // Boucle pour envoyer les messages de l'utilisateur au serveur
            BufferedReader inputMessage = new BufferedReader(new InputStreamReader(System.in));
            String messageToSend;
            while (!(messageToSend = inputMessage.readLine()).equals("exit")) {
                output.println(messageToSend);
            }

        } catch (IOException ex) {
            // Gestion des erreurs
            System.out.println("Une erreur est survenue : " + ex.getMessage());
        }
    }
}
