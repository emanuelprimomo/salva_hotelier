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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerSaverThread extends Thread {

  private final String PATH_AND_FILE_NAME_TO_STORE_DATA =
    "src/data/hotelsReviewed.json";
  private static ConcurrentHashMap<String, ArrayList<Hotel>> hotels;
  private static ConcurrentHashMap<String, User> users;
  private boolean running = true;

  public ServerSaverThread() {
    ServerSaverThread.hotels = hotels;
    ServerSaverThread.users = users;
  }

  public void shutdown() {
    running = false;
    this.interrupt(); // Interrompe il thread se è in attesa
  }

  /**
   * Metodo per caricare i dati nel file hotelsReviewed.json e Users.json
   * @return
   */
  @Override
  public void run() {
    while (running) {
      try {
        TimeUnit.SECONDS.sleep(30); // Salva i dati ogni 100 secondi
        // Salva i dati
        saveData();
        saveUsers();
      } catch (InterruptedException e) {
        if (!running) {
          System.out.println( // Stampa un messaggio se il thread è stato interrotto
            "ServerSaverThread interrotto"
          );
          break;
        }
        e.printStackTrace();
      }
    }
    //salvo i dati finali?
    saveUsers();
    saveData();
  }

  //creo un metodo che mi porta la variabile hotelWithReviews nel ServerSaverThread
  /**
   * Metodo per caricare i dati nel file hotelsReviewed.json
   * @param
   * @return
   */
  private synchronized void saveData() {
    // Implementa il salvataggio dei dati, ho bisogno di fare la merge con i dati già presenti nel file hotelsReviewed.json e la hotelsWithReviews
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    Gson gson = new Gson();

    //Apro il file
    try (
      JsonReader reader = new JsonReader(
        new FileReader(PATH_AND_FILE_NAME_TO_STORE_DATA)
      )
    ) {
      ArrayList<Hotel> hotelsToWrite = new ArrayList<>();
      reader.beginArray();
      while (reader.hasNext()) {
        //hotel del file
        Hotel hotel = gson.fromJson(reader, Hotel.class);

        //Scorro gli hotel con recensioni per trovare l'hotel da aggiornare (che sarebbe hotel, ma con le recensioni aggiornate) se esiste
        for (String city : hotelsWithReviews.keySet()) {
          ArrayList<Hotel> hotels = hotelsWithReviews.get(city);
          //se non ci sono hotel nella città
          if (hotels == null) {
            continue;
          }
          //se ci sono hotel nella città, scorro gli hotel per trovare quello da aggiornare
          for (Hotel hotelWithReviews : hotels) {
            if (hotel.getId() == hotelWithReviews.getId()) {
              System.out.println("Aggiornamento hotel: " + hotel.getName());
              //aggiorno le recensioni di hotel con quelli di hotelWithReviews
              hotel.setReviews(hotelWithReviews.getReviews());
              hotel.setRate(hotelWithReviews.getRate());
              hotel.setRanking(hotelWithReviews.getRanking());

              //una volta aggiornato l'hotel, devo ricarlolare il rate e i ratings
              //aggiorno il rate
              float rate = 0;
              float cleaning = 0;
              float position = 0;
              float services = 0;
              float quality = 0;

              for (Review review : hotel.getReviews()) {
                rate += review.getSynVote();
                //devo entrare in un ciclo per sommare i ratings
                for (Map.Entry<String, Float> entry : review
                  .getRatings()
                  .entrySet()) {
                  if (entry.getKey().equals("cleaning")) {
                    cleaning = entry.getValue();
                  } else if (entry.getKey().equals("position")) {
                    position = entry.getValue();
                  } else if (entry.getKey().equals("services")) {
                    services = entry.getValue();
                  } else if (entry.getKey().equals("quality")) {
                    quality = entry.getValue();
                  }
                }
              }
              //aggiorno il rate
              hotel.setRate(rate / hotel.getReviews().size());
              //aggiorno i ratings
              hotel.setRatings(
                Map.of(
                  "cleaning",
                  cleaning / hotel.getReviews().size(),
                  "position",
                  position / hotel.getReviews().size(),
                  "services",
                  services / hotel.getReviews().size(),
                  "quality",
                  quality / hotel.getReviews().size()
                )
              );
            }
          }
        }
        //scrivo/riscrivo l'hotel aggiornato nel file
        hotelsToWrite.add(hotel);
      }

      try (
        FileWriter writer = new FileWriter(PATH_AND_FILE_NAME_TO_STORE_DATA)
      ) {
        Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
        //writer.beginArray();
        /*for (Hotel hotel : hotelsToWrite) {
          gsonWriter.toJson(hotel, Hotel.class, writer);
        }*/
        System.out.println("sovrascrivo il file");
        gsonWriter.toJson(
          hotelsToWrite,
          new TypeToken<ArrayList<Hotel>>() {}.getType(),
          writer
        );
        //writer.endArray();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      System.out.println("File dove salvare i dati non trovato");
      e.printStackTrace();
    }
    /*File file = new File(PATH_AND_FILE_NAME_TO_STORE_DATA);

    JsonObject hotelsToWrite = new JsonObject();
    if (hotelsWithReviews == null) {
      return;
    }
    System.out.println(
      "Salvataggio dati in corso... hotels inseriti: " +
      hotelsWithReviews.size()
    );

    for (String city : hotelsWithReviews.keySet()) {
      ArrayList<Hotel> hotels = hotelsWithReviews.get(city);
      JsonArray hotelsListArray = new JsonArray();
      Set<String> addedHotels = new HashSet<>(); // Set per tenere traccia degli hotel già aggiunti
      //aggiungo il nome dell'hotel prima della lista di recensioni
      for (Hotel hotel : hotels) {
        if (addedHotels.contains(hotel.getName())) {
          continue; // Salta l'hotel se è già stato aggiunto
        }
        addedHotels.add(hotel.getName());
        JsonObject hotelJsonObject = new JsonObject();

        hotelJsonObject.addProperty("hotelName", hotel.getName());
        //salvo anche il ranking
        hotelJsonObject.addProperty("ranking", hotel.getRanking());

        JsonElement reviewJson = gson.toJsonTree(hotel.getReviews());
        hotelJsonObject.addProperty("rate", hotel.getRate());
        hotelJsonObject.add("reviews", reviewJson);
        //aggiungo il rate medio

        hotelsListArray.add(hotelJsonObject);
      }

      hotelsToWrite.add(city, hotelsListArray);
    }
    //problema quando si aggiorna il ranking e si scrive nel file, genera una copia

    try (FileWriter writer = new FileWriter(PATH_AND_FILE_NAME_TO_STORE_DATA)) {
      gson.toJson(hotelsToWrite, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }*/

  }

  /**
   * Metodo per salvare gli utenti nel file Users.json
   * @param
   * @return
   */
  private synchronized void saveUsers() {
    ConcurrentHashMap<String, User> users = UserManager
      .getInstance()
      .getUsers();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonArray usersToWrite = new JsonArray();
    for (User user : users.values()) {
      JsonObject remainsUser = new JsonObject();

      remainsUser.addProperty("usn", user.getUsn());
      remainsUser.addProperty("pwd", user.getPwd());
      remainsUser.addProperty("badge", user.getBadge());
      remainsUser.addProperty("rn", user.getRn());
      remainsUser.addProperty("online", user.getOnline());
      usersToWrite.add(remainsUser);
    }
    try (FileWriter writer = new FileWriter("src/data/Users.json")) {
      gson.toJson(usersToWrite, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
/*
Aggiunta user nel file Users.json
 JsonArray usersToWrite = new JsonArray();
    /*
     * Agiungo gli utenti al file Users.json
     
    for (User user : users.values()) {
      /* scorro l'array di utenti 
      JsonObject remainsUser = new JsonObject();

      remainsUser.addProperty("usn", user.getUsn());
      remainsUser.addProperty("pwd", user.getPwd());
      remainsUser.addProperty("badge", user.getBadge());
      remainsUser.addProperty("rn", user.getRn());
      remainsUser.addProperty("online", user.getOnline());
      usersToWrite.add(remainsUser);
 */
/*
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
  */
