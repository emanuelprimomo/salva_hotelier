package server;

import classes.Hotel;
import classes.Review;
import classes.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import netscape.javascript.JSObject;
import server.Cities;

/*
 * Operazioni utente loggato
 *  searchHotel(nomeHotel, città): ricerca i dati di un particolare hotel appartenente a
 una città e li invia all’utente. Questa operazione può essere effettuata anche dagli
 utenti non loggati.
 ○ serchAllHotels(città): ricerca i dati di tutti gli hotel di quella città e li invia all’utente.
 Gli hotel devono essere inviati ordinati secondo il ranking, calcolato in base alle
 recensioni. Questa operazione può essere effettuata anche dagli utenti non loggati.
 ○ insertReview(nomeHotel, nomeCittà, GlobalScore, [ ] SingleScores): inserisce una
 review per un hotel di una certa città. Viene indicato sia il punteggio complessivo
 per quell’hotel che i singoli punteggi per le varie categorie. L’utente deve essere
 registrato ed aver effettuato il login per effettuare questa operazione.
 ○ showMyBadges(): l’utente richiede di mostrare il proprio distintivo, corrispondente
 al maggior livello di expertise raggiunto. L’utente deve essere registrato ed aver
 effettuato il login per effettuare questa operazione
 
 
 Devo creare una classe recensione dove inserisco il tempo della recensione, il badge e il punteggio
 E ordino in base a quello, dove inserisco tutto? Dentro 
 Uso una concurrentHashMap per la lista degli hotel, dove metto city, arraylist
 Ho la multihashmap per gli hotel e li ordino in base alle recensioni, con il metodo sort
 ogni qualvolta però che cambia l'elemento in testa alla lista della città, mando un messaggio udp 
 a tutti i dispositivi connessi
 Devo pensare a come considerare le recensioni, faccio una classe recensione in cui ci metto semplicemente, voto totale, tempo e badge
 
 */
@Deprecated
public class Logged {

  private static final String PATH_AND_FILE_NAME = "src/data/Users.json";
  private static final String PATH_AND_FILE_NAME_HOTELS =
    "src/data/Hotels.json";
  //non mi carica la lista delle città
  private static final Set<String> cities = Cities.cities;

  public static void insertReview(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels
  ) throws IOException {
    /*
     * Da verificare
     */
    Hotel hotelInformations = null;
    String hotelName = in.readLine();
    System.out.println("Received hotel name: " + hotelName);
    String city = in.readLine();
    System.out.println("Received city: " + city);
    if (cities.contains(city) == false) {
      out.println("NOCITY");
      return;
    } else {
      out.println("CITY");
    }
    /*
     * ricorda di aggiungere al client la lettura di city o non city
     */

    Float globalScore = Float.parseFloat(in.readLine());
    System.out.println("Received global score: " + globalScore);

    Float rate = Float.parseFloat(in.readLine());
    System.out.println("Received single rate: " + rate);

    Float levelClean = Float.parseFloat(in.readLine());
    System.out.println("Received level clean: " + levelClean);

    String levelPosition = in.readLine();
    System.out.println("Received level position: " + levelPosition);

    Float levelQuality = Float.parseFloat(in.readLine());
    System.out.println("Received level quality: " + levelQuality);

    Float levelService = Float.parseFloat(in.readLine());
    System.out.println("Received level service: " + levelService);

    Float synVote =
      (globalScore + rate + levelClean + levelQuality + levelService) / 5.0f;
    //c'e un altro modo per scrivere questa media?

    String usn = in.readLine();
    System.out.println("Received username : " + usn);
    User user = users.get(usn);
    //non può essere null perché è loggato
    //verifico l'esistenza dell'albergo nella città prima nella lista degli hotel?
    ArrayList<Hotel> hotelList = hotels.get(city);
    if (hotelList == null) {
      System.out.println(
        "[LOGGED] Città non presente nella lista degli hotel già recensiti "
      );

      //se esiste aggiungo l'hotel alla lista
      JointOperations.insertHotelName(city, hotelName, hotels);
      if (hotels.get(city) == null) {
        System.out.println("[LOGGED] Hotel non presente nella città");
        out.println("NOHOTEL");
        return;
      }
    }
    System.out.println(
      "[LOGGED] Città presente nella lista degli hotel già recensiti "
    );
    /*
     * DA RIVEDERE
     */
    for (ArrayList<Hotel> hotelLists : hotels.values()) {
      for (Hotel hotel : hotelLists) {
        if (hotel.getName().equals(hotelName)) {
          // aggiungo la recensione
          Review review = new Review(synVote);
          // devo aggiungere la review all'hotel
          hotel.addReview(review);
          // aggiorno il ranking dell'hotel e aggiorno la lista degli hotel
          RankingAlgorithm.calculateRanking(hotel);
          System.out.println(
            "[LOGGED] dati dell'hotel inserito: " + hotel.toString()
          );

          break;
        }
      }
    }
    out.println("HOTEL");
    System.out.println("[LOGGED] Recensione aggiunta con successo");
    //aggiorno il badge dell'utente
    user.incrementRN();
    //aggiorno la lista degli hotel in base al ranking

  }

  public static void showMyBadges(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users,
    User user
  ) throws IOException {
    out.println(user.getBadge().toString());
    out.flush();
  }

  public static synchronized void userLoggedOperations(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels,
    User userLogged
  ) throws IOException {
    /*
     * stampo quello che può fare l'utente, devo iterare
     */
    System.out.println("Sono entrato nella logged.java");
    String command = " ";
    while (!(command = in.readLine().toLowerCase()).equals("exit")) {
      System.out.println("Ricevuto comando: " + command);
      switch (command) {
        case "search hotel":
          JointOperations.searchHotel(in, out, hotels);
          //prendo il dato
          break;
        case "search all hotels":
          JointOperations.searchAllHotels(in, out, hotels);
          //prendo i dati
          break;
        case "insert review":
          insertReview(in, out, users, hotels);
          //più complicato
          break;
        case "show my badges":
          showMyBadges(in, out, users, userLogged);
          //prendo le info sullo user
          break;
        case "logout":
          JointOperations.logout(in, out, users);
          //chiudo la connessione con la socket
          break;
        /*case "exit":
        System.out.println("Exiting...");
        break;*/
        default:
          out.println("Comando non riconosciuto");
          break;
      }
    }
    System.out.println("Exiting...");
  }
}
