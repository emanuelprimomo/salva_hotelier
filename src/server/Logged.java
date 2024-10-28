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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Logged {

  private static final String PATH_AND_FILE_NAME_HOTELS_REVIEWED =
    "src/data/hotelsReviewed.json";
  private static final Set<String> cities = Cities.cities;

  // Metodo per aggiornare il file JSON con gli hotel recensiti, qui se cambia il primo si manda il messaggio UDP
  private static void updateHotelsReviewedFile(
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews
  ) throws IOException {
    JsonObject jsonObject = new JsonObject();
    for (String city : hotelsWithReviews.keySet()) {
      JsonArray jsonArray = new JsonArray();
      for (Hotel hotel : hotelsWithReviews.get(city)) {
        JsonElement jsonElement = new Gson().toJsonTree(hotel);
        jsonArray.add(jsonElement);
      }
      jsonObject.add(city, jsonArray);
    }
    // Configura Gson per la formattazione
    try (
      FileWriter writer = new FileWriter(PATH_AND_FILE_NAME_HOTELS_REVIEWED)
    ) {
      writer.write(
        new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject)
      );
    }
  }

  // Metodo per unire i nuovi hotel recensiti nella struttura dati
  private static void mergeHotelsWithReviews(
    String city,
    Hotel hotel,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews
  ) {
    // Unisci nella mappa hotelsWithReviews
    hotelsWithReviews.computeIfAbsent(city, k -> new ArrayList<>()).add(hotel);

    // Sostituisci nella mappa hotels
    hotels.put(city, new ArrayList<>(hotelsWithReviews.get(city)));

    // Aggiorna il file JSON con gli hotel recensiti
    try {
      updateHotelsReviewedFile(hotelsWithReviews);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Metodo per aggiornare hotels con i dati di hotelsWithReviews
  public static void mergeHotelsData(
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews
  ) {
    for (String city : hotelsWithReviews.keySet()) {
      hotels.put(city, new ArrayList<>(hotelsWithReviews.get(city)));
    }
    try {
      updateHotelsReviewedFile(hotelsWithReviews);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static synchronized void insertReview(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels,
    UdpMessage udpMessage
  ) throws IOException, InterruptedException {
    /*
     * Da verificare
     */
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    Hotel hotelInformations = null;
    String hotelName = in.readLine().toLowerCase();
    System.out.println("Received hotel name: " + hotelName);
    String city = in.readLine().toLowerCase().trim(); //non sicuro del lower case
    System.out.println("Received city: " + city);
    if (cities.contains(city) == false) {
      System.out.println("[LOGGED] Città non presente nella lista delle città");
      out.println("NOCITY");
      return;
    } else {
      out.println("CITY");
    }
    /*
     * ricorda di aggiungere al client la lettura di city o non city
     */

    /*
     * vedere dove aggiungo 2 recensioni
     */
    Float globalScore = Float.parseFloat(in.readLine());
    System.out.println("Received global score: " + globalScore);

    Float rate = Float.parseFloat(in.readLine());
    System.out.println("Received single rate: " + rate);

    Float levelClean = Float.parseFloat(in.readLine());
    System.out.println("Received level clean: " + levelClean);

    Float levelPosition = Float.parseFloat(in.readLine());
    System.out.println("Received level position: " + levelPosition);

    Float levelQuality = Float.parseFloat(in.readLine());
    System.out.println("Received level quality: " + levelQuality);

    Float levelService = Float.parseFloat(in.readLine());
    System.out.println("Received level service: " + levelService);

    Float synVote =
      (
        globalScore +
        rate +
        levelClean +
        levelQuality +
        levelPosition +
        levelService
      ) /
      6.0f;
    //c'e un altro modo per scrivere questa media?
    List<Float> ratings = Arrays.asList(
      levelClean,
      levelPosition,
      levelService,
      levelQuality
    );
    String usn = in.readLine();
    System.out.println("Received username : " + usn);
    User user = users.get(usn);
    //non può essere null perché è loggato
    //verifico l'esistenza dell'albergo nella città prima nella lista degli hotel recensiti

    //prendo i dati dell'hotel dal hotels.json
    Hotel hotelReviewed = JointOperations.returnHotelInformations(
      city,
      hotelName
    );

    if (hotelReviewed == null) {
      System.out.println("[LOGGED] Hotel non presente nella città");
      out.println("NOHOTEL");
      return;
    } else {
      out.println("HOTEL");
    }

    //verifico se la lista degli hotel recensiti, ha o meno hotel
    Boolean hotelFind = false;
    ArrayList<Hotel> hotelList = hotelsWithReviews.get(city);
    if (hotelList == null) {
      // nessun hotel recensito nella città
      System.out.println(
        "[LOGGED] Nessun hotel presente nella lista degli hotel già recensiti "
      );
      Hotel newHotel = new Hotel(
        hotelName,
        city,
        hotelReviewed.getDescription(),
        hotelReviewed.getPhone(),
        hotelReviewed.getServices(),
        hotelReviewed.getRate(),
        hotelReviewed.getRatings(),
        hotelReviewed.getRanking(),
        new ArrayList<Review>()
      );

      //aggiungo la recensione

      Review review = new Review(synVote, ratings);
      newHotel.addReview(review);
      RankingAlgorithm.calculateRanking(
        newHotel,
        hotelsWithReviews,
        udpMessage
      );

      hotelsWithReviews
        .computeIfAbsent(city, k -> new ArrayList<>())
        .add(newHotel);
      //scorro gli elementi della lista degli hotel recensiti per vedere gli hotel recensiti
      for (Hotel hotel : hotelsWithReviews.get(city)) {
        System.out.println("[LOGGED] Hotel name inseriti: " + hotel.getName());
      }
    } else {
      //verifico se l'hotel è già presente nella lista degli hotel recensiti per quella città
      for (Hotel hotel : hotelList) {
        if (hotel.getName().equals(hotelName)) {
          System.out.println(
            "[LOGGED] Hotel già presente nella lista degli hotel recensiti"
          );
          //aggiungo la recensione
          Review review = new Review(synVote, ratings);
          hotel.addReview(review);
          hotelsWithReviews
            .computeIfAbsent(city, k -> new ArrayList<>())
            .add(hotel);
          //aggiorno il ranking
          RankingAlgorithm.calculateRanking(
            hotel,
            hotelsWithReviews,
            udpMessage
          );
          hotelFind = true;

          break;
        }
      }
      //se non è presente l'hotel nella lista degli hotel recensiti
      if (!hotelFind) {
        System.out.println(
          "[LOGGED] Hotel non presente nella lista degli hotel recensiti"
        );
        //aggiungo l'hotel in questione
        Hotel newHotel = new Hotel(
          hotelName,
          city,
          hotelReviewed.getDescription(),
          hotelReviewed.getPhone(),
          hotelReviewed.getServices(),
          hotelReviewed.getRate(),
          hotelReviewed.getRatings(),
          hotelReviewed.getRanking(),
          new ArrayList<Review>()
        );
        //aggiungo la recensione

        Review review = new Review(synVote, ratings);
        newHotel.addReview(review);
        hotelsWithReviews
          .computeIfAbsent(city, k -> new ArrayList<>())
          .add(newHotel);

        //aggiorno il ranking
        RankingAlgorithm.calculateRanking(
          newHotel,
          hotelsWithReviews,
          udpMessage
        );

        //scorro gli hotels di hotelsWithReviews per vedere gli hotel recensiti
        for (Hotel hotel : hotelsWithReviews.get(city)) {
          System.out.println("[LOGGED] Hotel name: " + hotel.getName());
        }
      }
    }

    /*
     * FINE TEST NUOVA VERSIONE
     */
    //se non c'è la città nella lista degli hotel recensiti, lo aggiungo
    /*if (hotelList == null) {
      System.out.println(
        "[LOGGED] Città non presente nella lista degli hotel già recensiti "
      );
      //aggiungo l'hotel in questione

      JointOperations.insertHotelName(city, hotelName, hotelsWithReviews);
    } else {
      //verifico se l'hotel è già presente nella lista degli hotel recensiti
      Boolean hotelFound = false;
      for (Hotel hotel : hotelList) {
        if (hotel.getName().equals(hotelName)) {
          System.out.println(
            "[LOGGED] Hotel già presente nella lista degli hotel recensiti"
          );
          hotelFound = true;
          break;
        }
      }
      if (!hotelFound) {
        System.out.println(
          "[LOGGED] Hotel non presente nella lista degli hotel recensiti"
        );
        JointOperations.insertHotelName(city, hotelName, hotelsWithReviews);
      }
      
    }

    if (hotelsWithReviews.get(city) == null) {
      System.out.println("[LOGGED] Hotel non presente nella città");
      out.println("NOHOTEL");
      return;
    } else {
      out.println("HOTEL");
    }*/

    //altrimenti aggiungo la recensione
    /*Boolean hotelFound = false;
    // Cerca l'hotel nella mappa degli hotel
    for (ArrayList<Hotel> hotelLists : hotels.values()) {
      for (Hotel hotel : hotelLists) {
        //System.out.println("Hotel name: " + hotel.getName());
        if (hotel.getName().equals(hotelName)) {
          // Aggiungi la recensione
          Review review = new Review(synVote, ratings);
          hotel.addReview(review);
          System.out.println("[LOGGED] Trovato hotel nella mappa");
          // Aggiorna il ranking dell'hotel, ma dovrebbe essere HotelRankingUpdater.updateHotelRankings(city, hotels, udpMessage);
          RankingAlgorithm.calculateRanking(
            hotel,
            hotelsWithReviews,
            udpMessage
          );

          //aggiorno il ranking

          /* 
          // Unisci l'hotel recensito nella nuova struttura dati e se cambia il primo, manda un messaggio UDP
          mergeHotelsWithReviews(
            hotel.getCity(),
            hotel,
            hotels,
            hotelsWithReviews
          );

          hotelFound = true;
          break;*/
    //  }
    //}

    // Aggiorna il badge dell'utente
    if (user != null) {
      user.incrementRN();
    }
    //problema nella scrittura del nuovo file json
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
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews,
    User userLogged,
    UdpMessage udpMessage
  ) throws IOException, InterruptedException {
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
          insertReview(in, out, users, hotels, udpMessage);
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
