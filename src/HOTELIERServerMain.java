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
import server.HotelsWithReviewsHandler;
import server.JointOperations;
import server.LogMethods;
import server.Logged;
import server.RegMethods;
import server.ServerSaverThread;
import server.UdpMessage;

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
  private static ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews = new ConcurrentHashMap<>();
  private static volatile MulticastSocket socketUDP = null;

  static {
    try {
      File file = new File(PATH_AND_FILE_NAME_HOTELS_REVIEWED);
      if (!file.exists()) {
        createHotelsReviewedFile(file);
      }
      hotelsWithReviews =
        loadHotelsWithReviewsFromJson(PATH_AND_FILE_NAME_HOTELS_REVIEWED);
    } catch (IOException e) {
      e.printStackTrace();
      hotelsWithReviews = new ConcurrentHashMap<>(); // Inizializza con una mappa vuota in caso di errore
    }
  }

  // Metodo per creare un file JSON vuoto
  private static void createHotelsReviewedFile(File file) throws IOException {
    JsonObject jsonObject = new JsonObject();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(jsonObject.toString());
    }
  }

  // Metodo per leggere gli hotel recensiti da un file JSON
  private static ConcurrentHashMap<String, ArrayList<Hotel>> loadHotelsWithReviewsFromJson(
    String filePath
  ) throws IOException {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews = new ConcurrentHashMap<>();
    try (FileReader reader = new FileReader(filePath)) {
      JsonElement jsonElement = JsonParser.parseReader(reader);
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      for (String city : jsonObject.keySet()) {
        JsonArray jsonArray = jsonObject.getAsJsonArray(city);
        ArrayList<Hotel> hotelList = new ArrayList<>();
        for (JsonElement element : jsonArray) {
          Hotel hotel = new Gson().fromJson(element, Hotel.class);
          hotelList.add(hotel);
        }
        hotelsWithReviews.put(city, hotelList);
      }
    } catch (IOException e) {
      System.out.println("Errore nella lettura del file JSON");
      e.printStackTrace();
    }
    return hotelsWithReviews;
  }

  public static void main(String[] args)
    throws IOException, InterruptedException, JsonIOException {
    ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
    Socket socket = null;
    System.out.println("Server in ascolto sulla porta " + PORT_NUMBER);
    ConcurrentHashMap<String, User> users = new ConcurrentHashMap();
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = new ConcurrentHashMap();
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews = new ConcurrentHashMap<>();
    /*
     * Creo instanza singleton del server in generale per poterla passare al thread che si occupa di scrivere sul file json e per inserire le recensioni
     * dalla classe logged e da altre classi
     */
    HotelsWithReviewsHandler
      .getInstance()
      .setHotelsWithReviews(hotelsWithReviews);

    ServerSaverThread serverSaverThread = new ServerSaverThread();
    serverSaverThread.start();
    //
    Timer timer = new Timer();

    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        //System.out.println("Entrato nel run del timer");
        /*
         * carico tutti gli utenti presenti in users nel file Users.json
         */
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File(PATH_AND_FILE_NAME);
        JsonArray usersToWrite = new JsonArray();
        /*
         * Agiungo gli utenti al file Users.json
         */
        for (User user : users.values()) {
          /* scorro l'array di utenti */
          JsonObject remainsUser = new JsonObject();

          remainsUser.addProperty("usn", user.getUsn());
          remainsUser.addProperty("pwd", user.getPwd());
          remainsUser.addProperty("badge", user.getBadge());
          remainsUser.addProperty("rn", user.getRn());
          remainsUser.addProperty("online", user.getOnline());
          usersToWrite.add(remainsUser);
        }

        try (FileWriter writer = new FileWriter(PATH_AND_FILE_NAME)) {
          gson.toJson(usersToWrite, writer);
          writer.flush();
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    //errore nel caricamento nella concurrentHashMap dei dati dal file Users.json
    File file = new File(PATH_AND_FILE_NAME);
    if (!file.exists()) {
      file.createNewFile();
    } else {
      /*
       * Se è gia esistente, devo caricare gli utenti all'interno del file Users.json nella concurrentHashMap
       */
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      try (FileReader reader = new FileReader(PATH_AND_FILE_NAME)) {
        //System.out.println("Apro il file Users.json [file gia esistente]");
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
            /*users.put(user.getUsn(), user);
          devo inserire i file della concurrentHashMap in Users.json che non sono gia presenti in registeredUsers*/
          }
          for (User user : users.values()) {
            /* scorro l'array di utenti */
            System.out.println("Utente: " + user.getUsn());
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    /*
     * Carico gli utenti presenti nella concurrentHashMap users, ma conviene farlo nel main
     */
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
    }
    timer.scheduleAtFixedRate(task, 0, TIME_TO_WRITE);
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

    while (true) {
      socket = serverSocket.accept();
      /* nuovo client arrivato */
      executor.execute(
        new ServerThread(socket, users, hotels, socketUDP, group)
      );
    }
  }

  private static class ServerThread implements Runnable {

    private final Socket socket;
    //users contiene gli utenti connessi
    private final ConcurrentHashMap<String, User> users;
    private final ConcurrentHashMap<String, ArrayList<Hotel>> hotels;
    private MulticastSocket socketUDP;
    private InetAddress group;

    public ServerThread(
      Socket socket,
      ConcurrentHashMap<String, User> users,
      ConcurrentHashMap<String, ArrayList<Hotel>> hotels,
      MulticastSocket socketUDP,
      InetAddress group
    ) {
      this.socket = socket;
      this.users = users;
      this.hotels = hotels;
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

        while (
          (requestClient = in.readLine().toLowerCase()).equals("exit") == false
        ) {
          Boolean userExist = false;
          //requestClient = in.readLine().toLowerCase();
          System.out.println("Received: " + requestClient);
          //out.println("Ricevuto");
          switch (requestClient) {
            case "register":
              RegMethods.registerUser(socket, in, out, users);
              break;
            case "login":
              User user = LogMethods.loginUser(in, out, users); //per loggare
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
                  users,
                  hotels,
                  hotelsWithReviews,
                  user,
                  new UdpMessage("", group, PORT_NUMBER)
                ); //una volta loggato
              }

              break;
            case "logout":
              JointOperations.logout(in, out, users);
              break;
            case "search hotel":
              JointOperations.searchHotel(in, out, hotels);
              break;
            case "search all hotels":
              JointOperations.searchAllHotels(in, out, hotels);
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
        try {
          socket.close();
        } catch (IOException socketException) {
          socketException.printStackTrace();
        }
      }
    }
  }
}
