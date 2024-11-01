package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.util.Scanner;

public class JointClientOperations {

  static final String EOF = "EOF";
  static final String END = "END";

  public static void searchHotelClient(
    PrintWriter out,
    Scanner in,
    BufferedReader inServer,
    String command
  ) throws IOException {
    out.println(command);
    String hotelName = InsertInformations.insertHotelName(in);
    out.println(hotelName);
    String city = InsertInformations.insertCity(in);
    out.println(city);
    String serverResponse = "";
    String hotelInformations = "";
    /*
     * in base a cosa restituisce, stampo
     */
    //serverResponse = inServer.readLine().toLowerCase();
    //da vedere meglio
    while ((serverResponse = inServer.readLine()) != null) {
      if (serverResponse.equals(EOF)) {
        break; // Fine della trasmissione
      } else {
        hotelInformations += serverResponse;
      }
    }
    synchronized (System.out) {
      System.out.println("Info sull'hotel " + hotelInformations); // stampa le più opzioni
    }
    hotelInformations = null;
  }

  public static void searchAllHotelsClient(
    PrintWriter out,
    Scanner in,
    BufferedReader inServer,
    String command
  ) throws IOException {
    out.println(command);
    String serverResponse = "";
    String hotelInformations = "";
    String city = InsertInformations.insertCity(in);
    out.println(city);
    /*
     * in base a cosa restituisce, stampo
     */
    //serverResponse = inServer.readLine().toLowerCase();
    //da vedere meglio
    while ((serverResponse = inServer.readLine()) != null) {
      if (serverResponse.equals(EOF)) {
        synchronized (System.out) {
          System.out.println(hotelInformations); // stampa le più opzioni
        }
        hotelInformations = null;
      } else if (!serverResponse.equals(END)) {
        hotelInformations += serverResponse;
      }
    }
  }
}
