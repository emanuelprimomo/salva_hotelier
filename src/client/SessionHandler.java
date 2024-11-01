package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/*
 * Svolge le operazioni post login, nella exit eseguo anche il logout
 */

public class SessionHandler {

  public static final String HOST_NAME = "localhost";
  public static final int PORT = 8888;
  public static final String EOF = "EOF";
  public static final String END = "END";

  public static String sessionMethods(
    Socket socket,
    PrintWriter out,
    BufferedReader inServer,
    String usn
  ) throws IOException {
    Scanner in = new Scanner(System.in);
    Scanner scan = null;
    String command = null;
    String serverResponse = null;
    String userConnected = null;
    //String usn = null;
    String pwd = null;
    String city = null;
    String hotelName = null;
    String hotelInformations = "";
    Boolean stop = false;
    Boolean log = true;
    while (log) {
      synchronized (System.out) {
        System.out.println(
          "Inserire comando:\n - Search Hotel\n - Search All Hotels\n - Insert Review\n - Show My Badges\n - Logout\n - Exit"
        );
      }
      command = in.nextLine().toLowerCase();
      switch (command) {
        case "search hotel":
          /*
           *
           
          out.println(command);
          hotelName = InsertInformations.insertHotelName(in);
          /*
           * verifico se c'è l'hotel nel json?
           
          out.println(hotelName);

          synchronized (System.out) {
            System.out.println("Inviato hotel name: " + hotelName);
          }
          /*serverResponse = inServer.readLine().toLowerCase();
            synchronized (System.out) {
              System.out.println(serverResponse); // stampa le più opzioni
            }
          out.flush();
          city = InsertInformations.insertCity(in);
          out.println(city);
          synchronized (System.out) {
            System.out.println("Inviata città: " + city);
          }
          /*serverResponse = inServer.readLine().toLowerCase();
            synchronized (System.out) {
              System.out.println(serverResponse); // stampa le più opzioni
            }
          /*
           * in base a cosa restituisce, stampo
           
          out.println(command);
          //serverResponse = inServer.readLine();
          while ((serverResponse = inServer.readLine()) != null) {
            if (serverResponse.equals(EOF)) {
              break; // Fine della trasmissione
            } else {
              hotelInformations.concat(serverResponse);
              synchronized (System.out) {
                System.out.println("Arrivato hotel: " + serverResponse);
              }
            }
          }
          System.out.println(hotelInformations); // stampa le più opzioni
          hotelInformations = null;*/
          JointClientOperations.searchHotelClient(out, in, inServer, command);
          break;
        case "search all hotels":
          /*
           *
           
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
          }
          break;
        /*
         * Faccio la insert review
         */
          JointClientOperations.searchAllHotelsClient(
            out,
            in,
            inServer,
            command
          );
          break;
        case "insert review":
          /*
           * DA AGGIUSTARE
           */
          out.println(command);
          out.flush();
          hotelName = InsertInformations.insertHotelName(in);
          //verifico se c'è l'hotel nel json?
          out.println(hotelName);

          out.flush();

          city = InsertInformations.insertCity(in);
          out.println(city);


          while ((serverResponse = inServer.readLine()).equals("NOCITY")) {
            synchronized (System.out) {
              System.out.println("Città non presente, reinserire");
            }
            city = InsertInformations.insertCity(in);
            out.println(city);
            out.flush();
          }
          //altrimenti trovata la città
          out.println(InsertInformations.insertGlobalScore(in));
          out.flush();

          out.println(InsertInformations.insertLevelClean(in));
          out.flush();

          out.println(InsertInformations.insertLevelPosition(in));
          out.flush();

          out.println(InsertInformations.insertLevelQuality(in));
          out.flush();

          out.println(InsertInformations.insertLevelService(in));
          out.flush();
          synchronized (System.out) {
            System.out.println(
              "[Client] Inserimento dati recensione completato"
            );
          }
          /*
           * invio l'utente che ha fatto la recensione
           */
          out.println(usn);
          out.flush();
          /*
           * ricevo la risposta dal server
           */
          serverResponse = inServer.readLine();
      
          /*  if ((serverResponse = inServer.readLine()).equals("NOHOTEL")) {
            synchronized (System.out) {
              System.out.println(
                "Hotel non presente, recensione non effettuata"
              );
              break;
            }
          }*/

          out.println(userConnected);
          synchronized (System.out) {
            System.out.println("Recensione avvenuta con successo");
          }
          break;
        case "show my badges":
          out.println(command);
          serverResponse = inServer.readLine();
          synchronized (System.out) {
            System.out.println(serverResponse); // stampa le più opzioni
          }

          break;
        case "logout":
          /*
           *
           */
          out.println(command);
          usn = InsertInformations.insertUsername(in);
          /*
           * invio al server l'username da eliminare, ricevuta risposta
           */
          out.println(usn);
          /*
           * invio l'username che ha effettuato il logout
           */
          serverResponse = inServer.readLine().toLowerCase();
          synchronized (System.out) {
            System.out.println(serverResponse);
          }
          if (serverResponse.equals("user logged out")) {
            log = false;
          }
          break;
        case "exit":
          log = false;
          /*
           * test di logout
           */
          out.println("logout");
          out.println(usn);
          out.println("exit");
          out.flush();
          break;
        default:
          synchronized (System.out) {
            System.out.println("Comando non riconosciuto");
          }
          break;
      }
    }
    return command;
  }
}
