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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe che contiene i metodi per le operazioni dell'utente loggato
 * <p> Questa classe contiene i metodi per le operazioni dell'utente loggato </p>
 * <p> Esempio di utilizzo del metodo insertReview </p>
 * <pre> BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
 * ConcurrentHashMap<String, ArrayList<Hotel>> hotels = new ConcurrentHashMap<>();
 * UdpMessage udpMessage = new UdpMessage();
 * Logged.insertReview(in, out, users, hotels, udpMessage); </pre>
 * <p> Esempio di utilizzo del metodo logout </p>
 * <pre> BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
 * Logged.logout(in, out, users); </pre>
 * <p> Esempio di utilizzo del metodo showMyBadges </p>
 * <pre> BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
 * User user = new User("mario123");
 * Logged.showMyBadges(in, out, users, user); </pre>
 * <p> Esempio di utilizzo del metodo userLoggedOperations </p>
 * <pre> BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
 * ConcurrentHashMap<String, ArrayList<Hotel>> hotels = new ConcurrentHashMap<>();
 * User userLogged = new User("mario123");
 * UdpMessage udpMessage = new UdpMessage();
 * Logged.userLoggedOperations(in, out, users, hotels, userLogged, udpMessage); </pre>
 *
 */

public class Logged {

  private static final String PATH_AND_FILE_NAME_HOTELS_REVIEWED =
    "src/data/hotelsReviewed.json";
  public static String PATH_AND_FILE_NAME_USERS = "src/data/Users.json";
  private static final Set<String> cities = Cities.cities;
  public static String USER_LOGGED_OUT = "USER LOGGED OUT";

  // Metodo per aggiornare il file JSON con gli hotel recensiti, qui se cambia il primo si manda il messaggio UDP
  /**
   * Aggiorna il file JSON con gli hotel recensiti, inviando un messaggio UDP se cambia il primo hotel
   * @param hotels
   * @throws IOException
   */
  /*private static void updateHotelsReviewedFile(
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels
  ) throws IOException {
    JsonObject jsonObject = new JsonObject();
    for (String city : hotels.keySet()) {
      JsonArray jsonArray = new JsonArray();
      for (Hotel hotel : hotels.get(city)) {
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
  }*/

  /**
   * Inserisce una recensione per un hotel di una certa città
   * @param in
   * @param out
   * @param users
   * @param hotels
   * @param udpMessage
   * @throws IOException
   * @throws InterruptedException
   */
  public static void insertReview(
    BufferedReader in,
    PrintWriter out,
    UdpMessage udpMessage
  ) throws IOException, InterruptedException {
    //prendo la lista degli hotel recensiti dal file hotelsReviewed.json

    ConcurrentHashMap<String, User> users = UserManager
      .getInstance()
      .getUsers();
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    Hotel hotelInformations = null;
    String hotelName = in.readLine().toLowerCase();
    System.out.println("Received hotel name: " + hotelName);
    String city = in.readLine().toLowerCase().trim(); //non sicuro del lower case
    System.out.println("Received city: " + city);
    //verifico se la città è presente nella lista delle città
    if (!cities.contains(city)) {
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

    Float levelClean = Float.parseFloat(in.readLine());
    System.out.println("Received level clean: " + levelClean);

    Float levelPosition = Float.parseFloat(in.readLine());
    System.out.println("Received level position: " + levelPosition);

    Float levelQuality = Float.parseFloat(in.readLine());
    System.out.println("Received level quality: " + levelQuality);

    Float levelService = Float.parseFloat(in.readLine());
    System.out.println("Received level service: " + levelService);

    Float synVote =
      (globalScore + levelClean + levelQuality + levelPosition + levelService) /
      5.0f;
    synVote = (float) Math.round(synVote * 100) / 100; // Arrotonda a due decimali
    //c'e un altro modo per scrivere questa media?
    Map<String, Float> ratings = Map.of(
      "cleaning",
      levelClean,
      "position",
      levelPosition,
      "services",
      levelService,
      "quality",
      levelQuality
    );
    String usn = in.readLine();
    //per incrementare il numero di recensioni
    System.out.println("Received username : " + usn);
    User user = users.get(usn);
    //non può essere null perché è loggato
    //verifico l'esistenza dell'albergo nella città prima nella lista degli hotel recensiti

    /*
     * Vedo se nella hashmap c'è la città e se c'è l'hotel
     */
    /*for (String key : hotels.keySet()) {
      for (Hotel hotel : hotels.get(key)) {
        if (
         hotel.getCity().toLowerCase().equals(city)
        ) {
          //verifico se l'hotel è già presente nella lista degli hotel recensiti

          System.out.println(
            "[LOGGED] Hotel già presente nella lista degli hotel recensiti"
          );
          //Aggiungo la recensione
          Review review = new Review(synVote, ratings);
          hotel.addReview(review);
          RankingAlgorithm.calculateRanking(hotel, udpMessage);
          return;
        }
      }
    }*/
    // Scorri la ConcurrentHashMap
    // Ottieni l'entrySet della ConcurrentHashMap
    for (Map.Entry<String, ArrayList<Hotel>> entry : hotels.entrySet()) {
      String key = entry.getKey(); // Ottieni la chiave (String)
      ArrayList<Hotel> hotelList = entry.getValue(); // Ottieni la lista di Hotel associata alla chiave
      if (hotelList == null) {
        continue;
      }
      // Scorri ogni oggetto Hotel nella lista
      for (Hotel hotel : hotelList) {
        // Accedi al nome dell'hotel
        System.out.println("Nome hotel: " + hotel.getName());
        if (hotel.getName().toLowerCase().equals(hotelName.toLowerCase())) {
          // Verifico se l'hotel è già presente nella lista degli hotel recensiti
          System.out.println(
            "[LOGGED] Hotel già presente nella lista degli hotel recensiti"
          );
          // Aggiungo la recensione
          Review review = new Review(synVote, ratings);
          hotel.addReview(review);
          RankingAlgorithm.calculateRanking(hotel, udpMessage);
          return;
        }
      }
    }

    //altrimenti prendo i dati dell'hotel dal hotels.json se esiste
    Hotel hotelReviewed = JointOperations.returnHotelInformations(
      city,
      hotelName
    );
    //Hotel hotelReviewed = null;
    //se non esiste l'hotel nella città
    if (hotelReviewed == null) {
      System.out.println("[LOGGED] Hotel non presente nella città");
      out.println("NOHOTEL");
      return;
    }
    //altrimenti invio al client che esiste
    out.println("HOTEL");

    //aggiungo la recensione
    Review review = new Review(synVote, ratings);
    hotelReviewed.addReview(review);
    //aggiungo l'hotel alla lista degli hotel recensiti della città
    if (hotels.containsKey(city)) {
      hotels.get(city).add(hotelReviewed);
    } else {
      hotels.put(city, new ArrayList<>(Arrays.asList(hotelReviewed)));
    }
    // Aggiorna il badge dell'utente
    if (user != null) {
      user.incrementRN();
    }
    return;
  }

  /**
   * Esegue il logout dell'utente
   * @param in
   * @param out
   * @param users
   * @throws IOException
   */
  public static synchronized void logout(BufferedReader in, PrintWriter out)
    throws IOException {
    ConcurrentHashMap<String, User> users = UserManager
      .getInstance()
      .getUsers();
    String usn = in.readLine();
    User user = users.get(usn);
    if (user == null) {
      out.println("username non esistente");
      return;
    }
    user.setOffline();
    /*//cambio lo stato nel json
    try {
      // 1. Apri il file JSON utilizzando FileReader
      FileReader reader = new FileReader(PATH_AND_FILE_NAME_USERS);

      // 2. Usa il JsonParser per ottenere il JsonArray dal file
      JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

      // 3. Itera sugli oggetti del JsonArray
      for (JsonElement element : jsonArray) {
        JsonObject obj = element.getAsJsonObject();

        // 4. Modifica il campo "online"
        if (obj.equals(user)) {
          obj.addProperty("online", false);
          break;
        }
      }

      // 5. Scrivi il JsonArray modificato di nuovo nel file
      FileWriter writer = new FileWriter(PATH_AND_FILE_NAME_USERS);
      Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Formatta il JSON per renderlo leggibile
      gson.toJson(jsonArray, writer); // Scrivi nel file
      writer.close();

      System.out.println("File JSON aggiornato correttamente!");
    } catch (IOException e) {
      e.printStackTrace();
    }
    //invio la risposta
    out.println(USER_LOGGED_OUT);
    out.flush();
    // 6. Rimuovi l'utente dal HashMap
    //users.remove(usn);*/

  }

  /**
   * Mostra i distintivi dell'utente
   * @param in
   * @param out
   * @param users
   * @param user
   * @throws IOException
   */
  public static void showMyBadges(
    BufferedReader in,
    PrintWriter out,
    User user
  ) throws IOException {
    ConcurrentHashMap<String, User> users = UserManager
      .getInstance()
      .getUsers();
    out.println(user.getBadge().toString());
    out.flush();
  }

  /**
   * Operazioni dell'utente loggato
   * @param in
   * @param out
   * @param users
   * @param hotels
   * @param userLogged
   * @param udpMessage
   * @throws IOException
   * @throws InterruptedException
   */
  public static synchronized void userLoggedOperations(
    BufferedReader in,
    PrintWriter out,
    User userLogged,
    UdpMessage udpMessage
  ) throws IOException, InterruptedException {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    ConcurrentHashMap<String, User> users = UserManager
      .getInstance()
      .getUsers();
    System.out.println("Sono entrato nella logged.java");
    String command = " ";
    //ciclo per ricevere i comandi
    while (!(command = in.readLine().toLowerCase()).equals("exit")) {
      System.out.println("Ricevuto comando: " + command);
      switch (command) {
        case "search hotel":
          JointOperations.searchHotel(in, out);
          //prendo il dato
          break;
        case "search all hotels":
          JointOperations.searchAllHotels(in, out);
          //prendo i dati
          break;
        case "insert review":
          insertReview(in, out, udpMessage);
          //più complicato
          break;
        case "show my badges":
          showMyBadges(in, out, userLogged);
          //prendo le info sullo user
          break;
        case "logout":
          logout(in, out);
          //chiudo la connessione con la socket
          break;
        case "exit":
          System.out.println("Exiting...");
          break;
        default:
          out.println("Comando non riconosciuto");
          break;
      }
    }
    System.out.println("Exiting...");
  }
}
