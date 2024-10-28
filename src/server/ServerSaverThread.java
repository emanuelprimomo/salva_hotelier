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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerSaverThread extends Thread {

  private final String PATH_AND_FILE_NAME_TO_STORE_DATA =
    "src/data/hotelsReviewed.json";
  private static ConcurrentHashMap<String, ArrayList<Hotel>> hotelWithReviews;

  public ServerSaverThread() {
    ServerSaverThread.hotelWithReviews = hotelWithReviews;
  }

  @Override
  public void run() {
    while (true) {
      try {
        TimeUnit.SECONDS.sleep(10);
        // Salva i dati
        saveData();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  //creo un metodo che mi porta la variabile hotelWithReviews nel ServerSaverThread
  private void saveData() {
    // Implementa il salvataggio dei dati
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    File file = new File(PATH_AND_FILE_NAME_TO_STORE_DATA);

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
        hotelJsonObject.add("reviews", reviewJson);

        hotelsListArray.add(hotelJsonObject);
      }

      hotelsToWrite.add(city, hotelsListArray);
    }
    //problema quando si aggiorna il ranking e si scrive nel file, genera una copia

    try (FileWriter writer = new FileWriter(PATH_AND_FILE_NAME_TO_STORE_DATA)) {
      gson.toJson(hotelsToWrite, writer);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  //errore nel caricamento nella concurrentHashMap dei dati dal file Users.json

}
