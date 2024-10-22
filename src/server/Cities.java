package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Cities {

  /*
   * carica un set con tutte le città
   */
  private static final String PATH__FILE_NAME = "src/data/Hotels.json";
  public static Set<String> cities;

  static {
    try {
      File file = new File(PATH__FILE_NAME);
      if (!file.exists()) {
        file.createNewFile();
      }
      cities = loadCitiesFromJson(PATH__FILE_NAME);
    } catch (IOException e) {
      e.printStackTrace();
      cities = new HashSet<>(); // Inizializza con un set vuoto in caso di errore
    }
  }

  // Metodo per leggere i capoluoghi di regione da un file JSON
  private static Set<String> loadCitiesFromJson(String filePath)
    throws IOException {
    Set<String> cities = new HashSet<>();
    try (FileReader reader = new FileReader(filePath)) {
      //scorro il file json, vedendo solo le città
      JsonElement jsonElement = JsonParser.parseReader(reader);
      JsonArray jsonArray = jsonElement.getAsJsonArray();
      for (JsonElement element : jsonArray) {
        JsonObject jsonObject = element.getAsJsonObject();
        // Estrai i valori dal JsonObject
        String name = jsonObject.get("city").getAsString().toLowerCase();

        if (!cities.contains(name)) {
          System.out.println("Caricata città: " + name);
          cities.add(name);
        }
      }
    } catch (IOException e) {
      System.out.println("Errore nella lettura del file JSON");
      e.printStackTrace();
    }
    return cities;
  }
}
