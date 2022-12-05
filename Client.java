import java.net.Socket;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        BufferedReader input;
        PrintWriter output;

        try {
            Socket sockcli = new Socket("127.0.0.1" ,1234);
          //  Scanner inputPseudo = new Scanner(System.in);
           // System.out.println("Entrez un pseudo : ");
            String pseudo = "PSEUDO";

            while(true) {
                try {
                    input = new BufferedReader(new InputStreamReader(sockcli.getInputStream()));
                    output = new PrintWriter(new OutputStreamWriter(sockcli.getOutputStream()), true);

                    output.println(pseudo);

                    Scanner myObj = new Scanner(System.in);

                    while (true) {
                        String msg = myObj.nextLine();
                        output.println(msg);
//                        System.out.println("msg : " + input.readLine());

                    }
                } finally {
                    sockcli.close();
                }
            }

        } catch (IOException ex) {}
    }
}
