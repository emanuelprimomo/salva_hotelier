/*
 * TODO
 * - Fixare implementazione della scrittura su file attraverso l'utilizzo di una concurrentHashMap
 * - Implementare la formattazione con JSONWriter
 * - Implementare inserimento recensioni
 * - Implementare ricerca
 * - Implementare ricerca tutti hotel del luogo
 * - Implementare creatore e modificatore di hotel e recensioni in formato JSON
 *
 */

/*
 * Ragionamento
 * Creo n file JSON per n città diverse? MHHHH, se fosse nel pratico sarebbe confusionario
 * Sennò come faccio?
 */

import classes.*;
import client.InsertInformations;
import com.google.gson.Gson; //ma non sono in lib?
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import netscape.javascript.JSObject;
import server.Cities;
import server.HotelsWithReviewsHandler;
import server.JointOperations;
import server.LogMethods;
import server.Logged;
import server.RegMethods;
import server.ServerSaverThread;
import server.UdpMessage;
import server.UserManager;

/*
 * Mi muovo con i JSONArray e JSONObject, problema nella scrittura del file json
 * Metto due volte gli stessi utenti
 */
public class HOTELIERServerMain {

  /*
   * devo implementare un thread che si occupa semplicemente di scrivere all'interno del file
   */
  private static final int PORT_NUMBER = 8888;
  private static final String PATH_AND_FILE_NAME = "src/data/Users.json";
  private static final int TIME_TO_WRITE = 10 * 1000;
  private static final String PATH_AND_FILE_NAME_HOTELS_REVIEWED =
    "src/data/hotelsReviewed.json";
  private static ConcurrentHashMap<String, ArrayList<Hotel>> hotels = new ConcurrentHashMap();
  private static ConcurrentHashMap<String, User> users = new ConcurrentHashMap();
  private static volatile MulticastSocket socketUDP = null;
  //per chiudere il serverSalverThread
  private static volatile boolean running = true;

  static {
    try {
      File file = new File(PATH_AND_FILE_NAME_HOTELS_REVIEWED);
      if (!file.exists()) {
        createHotelsReviewedFile(file);
      }
    } catch (IOException e) {
      e.printStackTrace();
      //hotelsWithReviews = new ConcurrentHashMap<>(); // Inizializza con una mappa vuota in caso di errore
    }
  }

  // Metodo per creare un file JSON vuoto
  private static synchronized void createHotelsReviewedFile(File file)
    throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    try (FileReader reader = new FileReader("src/data/Hotels.json")) {
      // Leggi l'array di hotel dal file di input

      JsonArray hotelsArray = JsonParser.parseReader(reader).getAsJsonArray();

      // Aggiungi le proprietà "rate" e "reviews" a ciascun hotel
      for (JsonElement jsonElement : hotelsArray) {
        JsonObject hotel = jsonElement.getAsJsonObject();
        hotel.addProperty("ranking", 0.0); // Aggiunge la proprietà rate con valore 0.0
        hotel.add("reviews", new JsonArray()); // Aggiunge l'array vuoto reviews
      }

      // Scrivi l'array aggiornato nel nuovo file JSON
      try (
        FileWriter writer = new FileWriter(PATH_AND_FILE_NAME_HOTELS_REVIEWED)
      ) {
        gson.toJson(hotelsArray, writer);
      }
    } catch (IOException e) {
      System.err.println(
        "Errore durante la lettura/scrittura del file JSON: " + e.getMessage()
      );
    } catch (IllegalStateException e) {
      System.err.println(
        "Errore: struttura JSON non valida - " + e.getMessage()
      );
    }
  }

  /**
   * Carica gli hotel con le recensioni dal file JSON.
   * @throws IOException
   */
  public static synchronized void loadHotelsWithReviews() {
    try (
      JsonReader reader = new JsonReader(
        new FileReader(PATH_AND_FILE_NAME_HOTELS_REVIEWED)
      )
    ) {
      Gson gson = new Gson();

      // Inizia a leggere l'array di hotel
      reader.beginArray();
      while (reader.hasNext()) {
        // Deserializza ogni hotel singolarmente
        Hotel hotel = gson.fromJson(reader, Hotel.class);

        // Inserisci l'hotel nella mappa per città
        if (hotel.getReviews() != null && !hotel.getReviews().isEmpty()) {
          // Inserisci l'hotel nella mappa solo se ha recensioni
          hotels
            .computeIfAbsent(hotel.getCity(), k -> new ArrayList<>())
            .add(hotel);
        }
      }
      reader.endArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args)
    throws IOException, InterruptedException, JsonIOException {
    ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
    Socket socket = null;
    System.out.println("Server in ascolto sulla porta " + PORT_NUMBER);
    ConcurrentHashMap<String, User> users = new ConcurrentHashMap();

    /*
     * Creo instanza singleton del server in generale per poterla passare al thread che si occupa di scrivere sul file json e per inserire le recensioni
     * dalla classe logged e da altre classi
     */
    HotelsWithReviewsHandler.getInstance().setHotelsWithReviews(hotels);
    /*
     * Carico tutti gli hotels con recensioni, quindi con il campo reviews.size()!= 0
     */
    loadHotelsWithReviews();

    /*
    System.out.println("Hotels caricati: " + hotels.size());
    // Itera attraverso ogni città nella ConcurrentHashMap
    hotels.forEach((city, hotels) -> {
      System.out.println("Città: " + city);
      // Itera attraverso ogni hotel della città corrente
      for (Hotel hotel : hotels) {
        System.out.println("Nome Hotel: " + hotel.getName());
        System.out.println("Descrizione: " + hotel.getDescription());
        System.out.println("Classifica: " + hotel.getRanking());
        System.out.println("Recensioni:");

        // Itera attraverso le recensioni dell'hotel
        for (Review review : hotel.getReviews()) {
          System.out.println("  Data: " + review.getDate());
          System.out.println("  Voto Sintetico: " + review.getSynVote());
          System.out.println("  Valutazioni:");
          review
            .getRatings()
            .forEach((ratingCategory, ratingValue) -> {
              System.out.println("    " + ratingCategory + ": " + ratingValue);
            });
        }
        System.out.println(
          "---------------------------------------------------"
        );
      }
    });*/

    // Inizializza la mappa degli hotel con le recensioni

    UserManager.getInstance().setUsers(users);
    ServerSaverThread serverSaverThread = new ServerSaverThread();
    serverSaverThread.start();

    /*
     * carico tutti gli utenti presenti in users nel file Users.json
     */
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    File file = new File(PATH_AND_FILE_NAME);

    if (!file.exists()) file.createNewFile(); else {
      /*
       * Se è gia esistente, devo caricare gli utenti all'interno del file Users.json nella concurrentHashMap
       */
      try (FileReader reader = new FileReader(PATH_AND_FILE_NAME)) {
        Type userListType = new TypeToken<List<User>>() {}.getType();
        List<User> registeredUsers = new ArrayList<>();
        JsonObject remainsUsers = new JsonObject();
        JsonArray usersToWrite = new JsonArray();
        /*
         * userList contiene gli utenti presenti nel file Users.json
         */
        registeredUsers = gson.fromJson(reader, userListType);
        if (registeredUsers != null) {
          for (User user : registeredUsers) {
            users.put(user.getUsn(), user);
            //aggiungo l'utente alla concurrentHashMap
          }

          for (User user : users.values()) {
            /* scorro l'array di utenti */
            System.out.println(
              "Stampa Utenti che tolgo a fine progetto: " + user.getUsn()
            );
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    /*
     * Carico gli utenti presenti nella concurrentHashMap users, ma conviene farlo nel main
     
    if (users.isEmpty()) {
      Type userListType = new TypeToken<List<User>>() {}.getType();
      List<User> registeredUsers = new ArrayList<>();
      try (FileReader reader = new FileReader(PATH_AND_FILE_NAME)) {
        registeredUsers = gson.fromJson(reader, userListType);
        if (registeredUsers != null) {
          for (User user : registeredUsers) {
            users.put(user.getUsn(), user);
          }
        }
      } catch (IOException e) {
        System.out.println("Errore nel caricamento degli utenti");
      }
    }*/
    ExecutorService executor = Executors.newCachedThreadPool();
    /*
     * Creo il collegamento UDP
     */
    //MulticastSocket socketUDP = null;
    InetAddress group = null;
    DatagramPacket packet = null;
    try {
      socketUDP = new MulticastSocket(); // Crea un socket multicast
      group = InetAddress.getByName("230.0.0.0"); // Indirizzo del gruppo multicast
      // Invia il pacchetto al gruppo
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    /*
     *
     */
    Runtime
      .getRuntime()
      .addShutdownHook(
        new Thread(() -> {
          running = false;
          serverSaverThread.shutdown(); // Interrompe il ServerSaverThread
          try {
            // Attendi il completamento del ServerSaverThread
            serverSaverThread.join();

            // Chiudi il serverSocket
            if (serverSocket != null && !serverSocket.isClosed()) {
              serverSocket.close();
              System.out.println("Connessione server chiusa.");
            }

            // Chiudi l'executor service
            executor.shutdown();
            try {
              if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                  System.err.println(
                    "Executor service non si è chiuso correttamente."
                  );
                }
              }
            } catch (InterruptedException ie) {
              executor.shutdownNow();
              Thread.currentThread().interrupt();
            }

            // Chiudi il socket UDP se è aperto
            if (socketUDP != null && !socketUDP.isClosed()) {
              socketUDP.close();
              System.out.println("Connessione UDP chiusa.");
            }
          } catch (IOException | InterruptedException e) {
            System.out.println("Errore durante la chiusura del server." + e);
            e.printStackTrace();
          }
        })
      );
    while (running) {
      try {
        socket = serverSocket.accept();
        /* nuovo client arrivato */
        executor.execute(new ServerThread(socket, socketUDP, group));
      } catch (SocketException e) {
        if (!running) {
          System.out.println("Server interrotto.");
        } else {
          throw e;
        }
      }
    }
  }

  private static class ServerThread implements Runnable {

    private final Socket socket;
    //users contiene gli utenti connessi
    private MulticastSocket socketUDP;
    private InetAddress group;

    public ServerThread(
      Socket socket,
      MulticastSocket socketUDP,
      InetAddress group
    ) {
      this.socket = socket;
      this.socketUDP = socketUDP;
      this.group = group;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
      System.out.println("id thread: " + Thread.currentThread().getId());
      System.out.println("Nuovo client");

      try (
        //così leggo il messaggio del client
        BufferedReader in = new BufferedReader(
          new InputStreamReader(socket.getInputStream())
        );
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      ) {
        String requestClient = null;

        while (running) {
          requestClient = in.readLine();
          if (requestClient == null) {
            break;
          }
          //lower case fatto dopo per evitare problemi in caso di errori
          requestClient = requestClient.toLowerCase();
          Boolean userExist = false;
          //requestClient = in.readLine().toLowerCase();
          System.out.println("Received: " + requestClient);
          //out.println("Ricevuto");
          switch (requestClient) {
            case "register":
              RegMethods.registerUser(socket, in, out);
              break;
            case "login":
              User user = LogMethods.loginUser(in, out); //per loggare
              //se l'utente è loggato
              if (user != null) {
                System.out.println(
                  "Utente loggato, valori dell'udp message: " +
                  group +
                  " " +
                  PORT_NUMBER
                );
                Logged.userLoggedOperations(
                  in,
                  out,
                  user,
                  new UdpMessage("", group, PORT_NUMBER)
                );
              }

              break;
            case "search hotel":
              JointOperations.searchHotel(in, out);
              break;
            case "search all hotels":
              JointOperations.searchAllHotels(in, out);
              break;
            default:
              System.out.println("Comando non riconosciuto");
              break;
          }
        }
        /*
         * caso di exit
         */
        System.out.println("Client disconnesso");
        out.close();
        in.close();
        socket.close();
      } catch (Exception e) {
        System.out.println("error: " + e);
        Thread.currentThread().interrupt();
      }
    }
  }
}
