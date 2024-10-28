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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
 * questo conterrà, la search hotel e la search all hotels, anche la logout, devo modificare tutto per inserire gli hotel
 */
public class JointOperations {

  public static String PATH_AND_FILE_NAME_HOTELS = "src/data/Hotels.json";
  public static String PATH_AND_FILE_NAME_USERS = "src/data/Users.json";
  public static String NO_HOTEL_FOUND = "NO HOTEL FOUND";
  public static String NO_USER_FOUND = "NO USER FOUND";
  public static String EOF = "EOF";
  public static String END = "END";
  public static String USER_LOGGED_OUT = "USER LOGGED OUT";

  public static void sendData(PrintWriter out, String data) {
    int blockSize = 1024;
    for (int i = 0; i < data.length(); i += blockSize) {
      int end = Math.min(data.length(), i + blockSize);
      out.write(data.substring(i, end));
      out.flush(); // Assicurati di inviare i dati in ogni iterazione
    }
    out.println(EOF);
  }

  public static synchronized void insertAllHotelsInCity(
    String city,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels
  ) {
    //cosa returno?
    try (FileReader reader = new FileReader(PATH_AND_FILE_NAME_HOTELS)) {
      System.out.println("Apro il file Hotels.json");
      // Parse il file JSON e converti in JsonArray
      JsonElement jsonElement = JsonParser.parseReader(reader);
      JsonArray jsonArray = jsonElement.getAsJsonArray();
      // Itera su ogni elemento dell'array
      for (JsonElement element : jsonArray) {
        // Ogni elemento è un JsonObject
        JsonObject jsonObject = element.getAsJsonObject();
        // Estrai i valori dal JsonObject
        String name = jsonObject.get("name").getAsString().toLowerCase();
        //System.out.println("nome hotel: " + name);
        String cityInFile = jsonObject.get("city").getAsString().toLowerCase();
        /* mi devo passare anche il nome dell'hotel */
        if (cityInFile.equals(city)) {
          System.out.println("hotel trovato");
          String description = jsonObject.get("description").getAsString();
          String phone = jsonObject.get("phone").getAsString();
          // Estrai e stampa l'array "hobbies"
          System.out.print("Services: ");
          JsonArray services = jsonObject.getAsJsonArray("services");
          String[] servicesString = new String[services.size()];
          int i = 0;
          for (JsonElement service : services) {
            servicesString[i] = service.getAsString();
            i++;
          }
          Float rate = jsonObject.get("rate").getAsFloat();

          //estraggo l'oggetto ratings
          JsonObject ratingsObject = jsonObject.getAsJsonObject("ratings");
          Float cleaning = ratingsObject.get("cleaning").getAsFloat();
          Float position = ratingsObject.get("position").getAsFloat();
          Float serviceInRatings = ratingsObject.get("services").getAsFloat();
          Float quality = ratingsObject.get("quality").getAsFloat();
          /*
            "cleaning": 0,
            "position": 0,
            "services": 0,
            "quality": 0
           */
          /*Float[] ratings = new Float[4];
          ratings[0] = Float.valueOf(cleaning);
          ratings[1] = Float.valueOf(position);
          ratings[2] = Float.valueOf(serviceInRatings);
          ratings[3] = Float.valueOf(quality);*/
          List<Float> ratings = Arrays.asList(
            cleaning,
            position,
            serviceInRatings,
            quality
          );
          System.out.println("Valori tutti inseriti correttamente");

          Hotel hotel = new Hotel(
            name,
            city,
            description,
            phone,
            servicesString,
            rate,
            ratings,
            0.0f,
            new ArrayList<Review>()
          );
          //aggiungo l'elemento se assente
          hotels.computeIfAbsent(city, c -> new ArrayList<>()).add(hotel);
          System.out.println("Dati hotel: " + hotel.toString());
        }
      }
      //inserisco tutti gli hotel correttamente
    } catch (IOException e) { //problema nella lettura dei ratings
      System.out.println("Errore nell'apertura del file");
    }
  }

  public static synchronized void insertHotelName(
    String city,
    String hotelName,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels
  ) {
    Gson gson = new Gson();

    List<Hotel> hotelList = new ArrayList<>();

    //a breve lo continuo
    System.out.println("Hotel da ricercare " + hotelName + " in " + city);
    try (FileReader reader = new FileReader(PATH_AND_FILE_NAME_HOTELS)) {
      System.out.println("Apro il file Hotels.json");
      // Parse il file JSON e converti in JsonArray
      JsonElement jsonElement = JsonParser.parseReader(reader);
      JsonArray jsonArray = jsonElement.getAsJsonArray();

      // Itera su ogni elemento dell'array
      for (JsonElement element : jsonArray) {
        // Ogni elemento è un JsonObject
        JsonObject jsonObject = element.getAsJsonObject();
        // Estrai i valori dal JsonObject
        String name = jsonObject.get("name").getAsString().toLowerCase();
        //System.out.println("nome hotel: " + name);
        String cityInFile = jsonObject.get("city").getAsString().toLowerCase();
        /* mi devo passare anche il nome dell'hotel */
        if (cityInFile.equals(city) && name.equals(hotelName)) {
          System.out.println("hotel trovato");
          String description = jsonObject.get("description").getAsString();
          String phone = jsonObject.get("phone").getAsString();
          // Estrai e stampa l'array "hobbies"
          System.out.print("Services: ");
          JsonArray services = jsonObject.getAsJsonArray("services");
          String[] servicesString = new String[services.size()];
          int i = 0;
          for (JsonElement service : services) {
            servicesString[i] = service.getAsString();
            i++;
          }
          Float rate = jsonObject.get("rate").getAsFloat();

          //estraggo l'oggetto ratings
          JsonObject ratingsObject = jsonObject.getAsJsonObject("ratings");
          Float cleaning = ratingsObject.get("cleaning").getAsFloat();
          Float position = ratingsObject.get("position").getAsFloat();
          Float serviceInRatings = ratingsObject.get("services").getAsFloat();
          Float quality = ratingsObject.get("quality").getAsFloat();

          //inserisco i valori
          List<Float> ratings = Arrays.asList(
            cleaning,
            position,
            serviceInRatings,
            quality
          );
          /*Float[] ratings = new Float[4];
          ratings[0] = Float.valueOf(cleaning);
          ratings[1] = Float.valueOf(position);
          ratings[2] = Float.valueOf(serviceInRatings);
          ratings[3] = Float.valueOf(quality);*/

          System.out.println("Valori tutti inseriti correttamente");

          Hotel hotel = new Hotel(
            hotelName,
            city,
            description,
            phone,
            servicesString,
            rate,
            ratings,
            0.0f,
            new ArrayList<Review>()
          );
          // aggiungo l'elemento se assente, altrimenti nada

          hotels.computeIfAbsent(city, c -> new ArrayList<>()).add(hotel);
          System.out.println("hotel inserito correttamente");
          System.out.println("Dati hotel: " + hotel.toString());
        }
      }
    } catch (IOException e) { //problema nella lettura dei ratings
      System.out.println("Errore nell'apertura del file");
    }
  }

  public static synchronized Hotel returnHotelInformations(
    String city,
    String hotelName
  ) {
    Hotel hotelToReturn = null;
    Gson gson = new Gson();

    List<Hotel> hotelList = new ArrayList<>();

    //a breve lo continuo
    System.out.println("Hotel da ricercare " + hotelName + " in " + city);
    try (FileReader reader = new FileReader(PATH_AND_FILE_NAME_HOTELS)) {
      System.out.println("Apro il file Hotels.json");
      // Parse il file JSON e converti in JsonArray
      JsonElement jsonElement = JsonParser.parseReader(reader);
      JsonArray jsonArray = jsonElement.getAsJsonArray();

      // Itera su ogni elemento dell'array
      for (JsonElement element : jsonArray) {
        // Ogni elemento è un JsonObject
        JsonObject jsonObject = element.getAsJsonObject();
        // Estrai i valori dal JsonObject
        String name = jsonObject.get("name").getAsString().toLowerCase();
        //System.out.println("nome hotel: " + name);
        String cityInFile = jsonObject.get("city").getAsString().toLowerCase();
        /* mi devo passare anche il nome dell'hotel */
        if (cityInFile.equals(city) && name.equals(hotelName)) {
          System.out.println("hotel trovato");
          String description = jsonObject.get("description").getAsString();
          String phone = jsonObject.get("phone").getAsString();
          // Estrai e stampa l'array "hobbies"
          System.out.print("Services: ");
          JsonArray services = jsonObject.getAsJsonArray("services");
          String[] servicesString = new String[services.size()];
          int i = 0;
          for (JsonElement service : services) {
            servicesString[i] = service.getAsString();
            i++;
          }
          Float rate = jsonObject.get("rate").getAsFloat();

          //estraggo l'oggetto ratings
          JsonObject ratingsObject = jsonObject.getAsJsonObject("ratings");
          Float cleaning = ratingsObject.get("cleaning").getAsFloat();
          Float position = ratingsObject.get("position").getAsFloat();
          Float serviceInRatings = ratingsObject.get("services").getAsFloat();
          Float quality = ratingsObject.get("quality").getAsFloat();

          //inserisco i valori
          List<Float> ratings = Arrays.asList(
            cleaning,
            position,
            serviceInRatings,
            quality
          );
          /*ratings[0] = Float.valueOf(cleaning);
          ratings[1] = Float.valueOf(position);
          ratings[2] = Float.valueOf(serviceInRatings);
          ratings[3] = Float.valueOf(quality);*/

          System.out.println("Valori tutti inseriti correttamente");

          hotelToReturn =
            new Hotel(
              hotelName,
              city,
              description,
              phone,
              servicesString,
              rate,
              ratings,
              0.0f,
              new ArrayList<Review>()
            );
        }
      }
    } catch (IOException e) { //problema nella lettura dei ratings
      System.out.println("Errore nell'apertura del file");
    }
    return hotelToReturn;
  }

  public static synchronized void searchHotel(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels
  ) throws IOException {
    /*
     * attendo il messaggio della città
     */
    String hotelName = in.readLine();
    System.out.println("Received hotel name: " + hotelName);
    //out.println(hotelName);
    //out.flush();
    String city = in.readLine();
    //out.println("Received city: " + city);
    //out.flush();
    System.out.println("Received city: " + city);

    //inserisco l'hotel
    insertHotelName(city, hotelName, hotels);
    // inserisco gli Hotels della citta' in questione in una lista
    ArrayList<Hotel> cityHotel = hotels.get(city);
    // hotel da inviare come risposta
    Hotel hotelToReturn = null;
    System.out.println("Sono stati inseriti : " + cityHotel.size() + " hotels");
    if (cityHotel.isEmpty() || cityHotel == null) {
      System.out.println("Non ci sono hotels nella città");
      out.println(NO_HOTEL_FOUND);
    } else {
      for (Hotel hotel : cityHotel) {
        if (hotel.getName().equals(hotelName) && hotel.getCity().equals(city)) {
          hotelToReturn = hotel;
          break;
        }
      }
      String responseString = hotelToReturn.toString();
      //out.println(responseString);
      sendData(out, responseString);
    }
    /*
     * verifico se la lista degli hotels è vuota o no
     */
    /*if (hotels.size() == 0 || !hotels.containsKey(hotelName)) {
      System.out.println("Inserisco l'hotel");

      insertHotelName(city, hotelName, hotels);
    } else {
      System.out.println("Dovrei già avere l'hotel");
      if(hotels.get(city)!=null) {
        
      }else {
        //come vedo se non esiste?
        insertHotelName(city, hotelName, hotels);

      }*/

    /*for (Hotel hotel : hotels.values()) {
        if (hotel.getName().equals(hotelName) && hotel.getCity().equals(city)) {
          hotelToReturn = hotel;
          break;
        }
      }
      if (!hotels.containsKey(hotelToReturn.getName())) {
        hotels.put(city, hotelToReturn);
      }
      if (hotelToReturn == null) {
        out.println(NO_HOTEL_FOUND);
      } else {
        String responseString = hotelToReturn.toString();
        System.out.println("Sending response: " + responseString);
        out.println(responseString);
      }*/
    //l'ho messo bene sia a searchAll che a searchHotel?
    out.flush();
  }

  public static synchronized void searchAllHotels(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels
  ) throws IOException {
    /*
     * controllo cosa returnare
     */
    String city = in.readLine().toLowerCase();
    System.out.println("Received city: " + city);

    //inserisco l'hotel
    insertAllHotelsInCity(city, hotels);

    ArrayList<Hotel> cityHotels = hotels.get(city);

    System.out.println(
      "Sono stati inseriti : " + cityHotels.size() + " hotels"
    );
    //altrimenti, per ogni hotel, controllo quelli non inseriti
    if (cityHotels == null || cityHotels.isEmpty()) {
      out.println(NO_HOTEL_FOUND);
      return;
    } else {
      for (Hotel hotel : cityHotels) {
        String responseString = hotel.toString();
        System.out.println("Sending response: " + responseString);
        sendData(out, responseString);
        //problema nel send data
        //out.println(responseString);
      }
      System.out.println("Sending response: " + END);
      out.println(END);
      out.flush();
    }
    /*
    //una volta fatto ciò, devo mandare tutti gli hotel
    for (Hotel hotel : cityHotels.values()) {
      if (hotel.getCity().equals(city)) {
        String responseString = hotel.toString();
        System.out.println("Sending response: " + responseString);
        out.println(responseString);
        out.flush();
      }
    }
    //per concludere la ricezione
    out.println(END);
    out.flush();
    //devo impostare la ricezione da parte del client
    */
  }

  public static synchronized void logout(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users
  ) throws IOException {
    String usn = in.readLine();
    User user = users.get(usn);
    if (user == null) {
      out.println("username non esistente");
      return;
    }
    user.setOffline();
    //cambio lo stato nel json
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
    //users.remove(usn);

  }
}
