package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

/**
 * Classe che contiene i metodi per inizializzare la sessione, ovvero per la registrazione, il login, la ricerca di un hotel, la ricerca di tutti gli hotel e l'uscita
 * <p> Questa classe contiene i metodi per inizializzare la sessione, ovvero per la registrazione, il login, la ricerca di un hotel, la ricerca di tutti gli hotel e l'uscita </p>
 * <p> Esempio di utilizzo del metodo sessionMethods </p>
 * <pre> Socket socket = new Socket(HOST_NAME, PORT);
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * BufferedReader inServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * MulticastSocket socketUDP = new MulticastSocket(4446);
 * SessionInitializer.sessionMethods(socket, out, inServer, socketUDP); </pre>
 * <p> Esempio di utilizzo del metodo closeConnections </p>
 * <pre> Socket socket = new Socket(HOST_NAME, PORT);
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * BufferedReader inServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * MulticastSocket socketUDP = new MulticastSocket(4446);
 * SessionInitializer.closeConnections(socket, out, inServer, socketUDP); </pre>
 *
 */
public class SessionInitializer {

  static final String HOST_NAME = "localhost";
  static final int PORT = 8888;
  static final String EOF = "EOF";
  static final String END = "END";

  /**
   * Inizializza la sessione con il server, ovvero la registrazione, il login, la ricerca di un hotel, la ricerca di tutti gli hotel e l'uscita
   * @param socket
   * @param out
   * @param inServer
   * @param socketUDP
   * @throws IOException
   */
  public static void sessionMethods(
    Socket socket,
    PrintWriter out,
    BufferedReader inServer,
    MulticastSocket socketUDP
  ) throws IOException {
    Scanner in = new Scanner(System.in);
    Scanner scan = null;
    String command = null;
    String serverResponse = null;
    String userConnected = null;
    String usn = null;
    String pwd = null;
    String city = null;
    String hotelName = null;
    String hotelInformations = "";
    Boolean stop = false; //condizione per concludere l'esecuzione dell'applicazione
    /*
     * variabile utilizzata per vedere se un utente è loggato
     */
    scan = new Scanner(socket.getInputStream());
    {
      synchronized (System.out) {
        System.out.println(
          "Inserire le operazioni da effettuare:\n - Register\n - Login\n - Search Hotel\n - Search All Hotels\n - Exit"
        );
      }
      while (in.hasNextLine() && !stop) {
        command = in.nextLine().toLowerCase();

        switch (command) {
          case "register":
            /*
             * invio il comando di registrazione al server
             */
            out.println(command);
            usn = InsertInformations.insertUsername(in);
            out.println(usn);/* invio il nome dell'utente al server */

            serverResponse = inServer.readLine().toLowerCase();
            synchronized (System.out) {
              System.out.println(serverResponse);
            }

            while (serverResponse.equals("username esistente")) {
              synchronized (System.out) {
                System.out.println("Username esistente, reinserire username:");
              }
              usn = InsertInformations.insertUsername(in);
              out.println(usn);
              serverResponse = inServer.readLine().toLowerCase();
            }

            pwd = InsertInformations.insertPassword(in);

            out.println(pwd);
            serverResponse = inServer.readLine();

            synchronized (System.out) {
              System.out.println("response del server " + serverResponse);
            }
            /*
             * in base al messaggio ricevuto dal server
             */

            break;
          case "login":
            /*
             * invio il messaggio di login al server
             */
            out.println(command);
            usn = InsertInformations.insertUsername(in);
            out.println(usn);
            serverResponse = inServer.readLine();

            while (
              serverResponse.equals("username non esistente") ||
              serverResponse.equals("user loggato")
            ) {
              synchronized (System.out) {
                System.out.println("Risposta dal server: " + serverResponse);
                usn = InsertInformations.insertUsername(in);
                out.println(usn);
                serverResponse = inServer.readLine();
              }
            }
            /*
             * da aggiustare
             */
            pwd = InsertInformations.insertPassword(in);
            out.println(pwd);
            serverResponse = inServer.readLine();
            while (serverResponse.equals("password errata")) {
              synchronized (System.out) {
                System.out.println("Risposta dal server: " + serverResponse);
              }
              pwd = InsertInformations.insertPassword(in);
              out.println(pwd);
              serverResponse = inServer.readLine();
            }
            /*
             * ricevo se è loggato o meno
             */

            synchronized (System.out) {
              userConnected = usn;
              System.out.println("user connected: " + userConnected);
              System.out.println("Login avvenuto con successo");
            }
            String lastCommand = SessionHandler.sessionMethods(
              socket,
              out,
              inServer,
              userConnected
            );
            System.out.println("last command: " + lastCommand);
            //se esco da qui ho finito l'esecuzione
            if (lastCommand.equals("exit")) {
              stop = true;
            }
            //vedere questa condizione, perché se fa il logout dovrei tornare in questa schermata
            break;
          case "logout":
            /*
             * logout
             */
            out.println(command);
            synchronized (System.out) {
              System.out.println("Inserire username per il logout:");
            }
            usn = in.nextLine();
            /*
             * in base a cosa ritorna
             */
            out.println(usn);
            serverResponse = inServer.readLine();
            /*if (serverResponse.equals("USER UNLOGGED") ) {
            synchronized (System.out) {
              System.out.println("Logout avvenuto con successo");
            }
            stop = false;
          } else {
            synchronized (System.out) {
              System.out.println("Errore durante il logout");
            }
          }*/
            synchronized (System.out) {
              System.out.println(serverResponse);
            }
            break;
          case "search hotel":
            /*
             * invio il messaggio di ricerca dell'hotel al server
             */
            JointClientOperations.searchHotelClient(out, in, inServer, command);
            break;
          case "search all hotels":
            /*
             * invio il messaggio di ricerca di tutti gli hotel al server
             
            out.println(command);
            city = InsertInformations.insertCity(in);
            out.println(city);
            while ((serverResponse = inServer.readLine()) != null) {
              synchronized (System.out) {
                if (!serverResponse.equals(EOF)) {
                  //non stampo l'EOF
                  System.out.println(serverResponse); // stampa le più opzioni
                }
                if (serverResponse.equals(END)) {
                  break;
                }
              }
            }*/
            JointClientOperations.searchAllHotelsClient(
              out,
              in,
              inServer,
              command
            );

            break;
          case "exit":
            stop = true;
            out.println(command);
            break;
          default:
            synchronized (System.out) {
              System.out.println("Comando non riconosciuto");
            }
            break;
        }
      }
      synchronized (System.out) {
        System.out.println("Connessione interrotta");
        closeConnections(socket, out, in, inServer, socketUDP);
      }
    }

    in.close();
    out.close();
    inServer.close();
  }

  /**
   * Metodo per chiudere le connessioni
   * @param socket
   * @param out
   * @param in
   * @param inServer
   * @param socketUDP
   */
  private static void closeConnections(
    Socket socket,
    PrintWriter out,
    Scanner in,
    BufferedReader inServer,
    MulticastSocket socketUDP
  ) {
    try {
      if (out != null) out.close();
    } catch (Exception e) {
      synchronized (System.out) {
        System.out.println("Errore durante la chiusura di PrintWriter: " + e);
      }
      e.printStackTrace();
    }

    try {
      if (inServer != null) inServer.close();
    } catch (IOException e) {
      synchronized (System.out) {
        System.out.println(
          "Errore durante la chiusura di BufferedReader: " + e
        );
      }
      e.printStackTrace();
    }

    try {
      if (in != null) in.close();
    } catch (Exception e) {
      synchronized (System.out) {
        System.out.println("Errore durante la chiusura di Scanner: " + e);
      }
      e.printStackTrace();
    }

    try {
      if (socket != null && !socket.isClosed()) socket.close();
    } catch (IOException e) {
      synchronized (System.out) {
        System.out.println("Errore durante la chiusura di Socket: " + e);
      }
      e.printStackTrace();
    }
    try {
      if (socketUDP != null && !socketUDP.isClosed()) socketUDP.close();
    } catch (Exception e) {
      synchronized (System.out) {
        System.out.println(
          "Errore durante la chiusura di MulticastSocket: " + e
        );
      }
      e.printStackTrace();
    }
  }
}
