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
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe che contiene i metodi per le operazioni congiunte
 *
 */
public class JointOperations {

  public static String PATH_AND_FILE_NAME_HOTELS =
    "src/data/hotelsReviewed.json";
  public static String NO_HOTEL_FOUND = "NO HOTEL FOUND";
  public static String PATH_AND_FILE_NAME_USERS = "src/data/Users.json";

  public static String NO_USER_FOUND = "NO USER FOUND";
  public static String EOF = "EOF";
  public static String END = "END";

  private static final Set<String> cities = Cities.cities;

  /**
   * Metodo per inviare i dati al client
   * @param out
   * @param data
   */
  public static void sendData(PrintWriter out, String data) {
    int blockSize = 1024;
    for (int i = 0; i < data.length(); i += blockSize) {
      int end = Math.min(data.length(), i + blockSize);
      out.write(data.substring(i, end));
      out.flush();
    }
    //Segnalo la fine del messaggio
    out.println(EOF);
  }

  /**
   * Metodo per inserire tutti gli hotels di una città
   * @param city
   * @param hotels
   */

  /*
      /*
  Hotel hotelHash = findHotelInCity(hotel, city);
        if (hotelHash != null) {
            return hotelHash.toString();
        }
        try (JsonReader reader = new JsonReader(new FileReader(HOTELS_PATH))) {
            reader.beginArray();
            while (reader.hasNext()) {
                Hotel h = gson.fromJson(reader, Hotel.class);
                if (h.getCity().equals(city) && h.getName().equals(hotel)) {
                    checkIfPresentAndAdd(hotels, h);
                    return h.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Hotel not found";
    */

  //manca il caso in cui la città non è presente
  public static void insertAllHotelsInCity(String city) {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    Gson gson = new Gson();
    try (
      JsonReader reader = new JsonReader(
        new FileReader(PATH_AND_FILE_NAME_HOTELS)
      )
    ) {
      reader.beginArray();
      while (reader.hasNext()) {
        Hotel hotel = gson.fromJson(reader, Hotel.class);
        if (hotel.getCity().equals(city) && cities.contains(city)) {
          /*checkIfPresentAndAdd(hotels, h);
          return h.toString();*/
          //da verificare
          hotels.computeIfAbsent(city, c -> new ArrayList<>()).add(hotel);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*public static synchronized void insertAllHotelsInCity(String city) {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
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
        /* mi devo passare anche il nome dell'hotel  //qui era commentato
        if (cityInFile.equals(city)) {
          System.out.println("hotel trovato");
          String description = jsonObject.get("description").getAsString();
          String phone = jsonObject.get("phone").getAsString();
          // Estrai e stampa l'array "hobbies"
          System.out.print("Services: ");
          JsonArray services = jsonObject.getAsJsonArray("services");
          //potrei renderla anche lista di stringhe
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
            0.0,
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
  }*/

  public static void insertHotelName(String city, String hotelName)
    throws IOException {
    Gson gson = new Gson();
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();

    if (city == null || hotelName == null) {
      return;
    }
    if (!cities.contains(city)) {
      return;
    }
    System.out.println(
      "[InsertHotelName]Hotel da ricercare " + hotelName + " in " + city
    );
    hotelName = hotelName.toLowerCase().trim();
    //verifico se l'hotel è già presente in hotels
    if (hotels.get(city) != null) {
      for (Hotel hotel : hotels.get(city)) {
        if (hotel.getName().toLowerCase().equals(hotelName)) {
          System.out.println("Hotel trovato");
          return;
        }
      }
    } else {
      //apro il file
      try (
        JsonReader reader = new JsonReader(
          new FileReader(PATH_AND_FILE_NAME_HOTELS)
        )
      ) {
        reader.beginArray();
        while (reader.hasNext()) {
          Hotel hotel = gson.fromJson(reader, Hotel.class);
          if (
            hotel.getCity().toLowerCase().equals(city) &&
            hotel.getName().toLowerCase().equals(hotelName)
          ) {
            System.out.println("Hotel trovato");
            hotels.computeIfAbsent(city, c -> new ArrayList<>()).add(hotel);
          }
        }
      } catch (FileNotFoundException e) {
        System.out.println("Errore nell'apertura del file");
        e.printStackTrace();
      }
    }
  }

  /*
   *
   *
   */
  /*public static void insertHotelName(
    String city,
    String hotelName
  ) {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
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
        /* mi devo passare anche il nome dell'hotel 
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

          //inserisco il ranking
          Float ranking = jsonObject.get("ranking").getAsFloat();

          //inserisco le recensioni
          JsonArray reviewsJson = jsonObject.getAsJsonArray("reviews");
          List<Review> reviews = new ArrayList<>();
          Review reviewToAdd = null;
          for (JsonElement reviewElement : reviewsJson) {
            JsonObject reviewObject = reviewElement.getAsJsonObject();
            Float synVote = reviewObject.get("synVote").getAsFloat();
            Date date = gson.fromJson(reviewObject.get("date"), Date.class);
            Map<String, Float> ratingsMap = new HashMap<>();
            
            //

            /* 
             * 
            
            Float[] reviewRatings = gson.fromJson(
              reviewObject.get("ratings"),
              Float[].class
            );
            reviewToAdd = new Review(synVote, Arrays.asList(reviewRatings));
            reviewToAdd.setDate(date);
            reviews.add(reviewToAdd);
          }

          System.out.println("Valori tutti inseriti correttamente");

          Hotel hotel = new Hotel(
            hotelName,
            city,
            description,
            phone,
            servicesString,
            rate,
            ,
            ranking,
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
  }*/

  /*
   * Restituisce l'hotel se presente, altrimenti null
   */

  /*  public static Hotel returnHotelInformations(String city, String hotelName) {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();

    if (hotels.get(city) != null) {
      for (Hotel hotel : hotels.get(city)) {
        if (hotel.getName().equals(hotelName)) {
          return hotel;
        }
      }
    } else {
      //apro il file

      Gson gson = new Gson();
      try (
        JsonReader reader = new JsonReader(
          new FileReader(PATH_AND_FILE_NAME_HOTELS)
        )
      ) {
        reader.beginArray();
        while (reader.hasNext()) {
          Hotel hotel = gson.fromJson(reader, Hotel.class);
          if (
            hotel.getCity().toLowerCase().equals(city.toLowerCase()) &&
            hotel.getName().toLowerCase().equals(hotelName.toLowerCase())
          ) {
            return hotel;
          }
        }
      } catch (FileNotFoundException e) {
        System.out.println("Errore nell'apertura del file");
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }*/

  public static Hotel returnHotelInformations(String city, String hotelName) {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    // Se la città non esiste nella mappa, apri il file JSON per cercare l'hotel
    System.out.println("[return hotel informations]");
    Gson gson = new Gson();
    try (
      JsonReader reader = new JsonReader(
        new FileReader(PATH_AND_FILE_NAME_HOTELS)
      )
    ) {
      reader.beginArray();
      System.out.println("Apro il file Hotels.json");
      while (reader.hasNext()) {
        Hotel hotel = gson.fromJson(reader, Hotel.class);
        if (
          hotel.getCity().toLowerCase().equals(city) &&
          hotel.getName().toLowerCase().equals(hotelName)
        ) {
          System.out.println("Hotel trovato");
          return hotel;
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("Errore nell'apertura del file");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void searchHotel(BufferedReader in, PrintWriter out)
    throws IOException {
    /*
     * attendo il messaggio della città
     */
    /*ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    String hotelName = in.readLine();
    System.out.println("Received hotel name: " + hotelName);
    //out.println(hotelName);
    //out.flush();
    String city = in.readLine();
    //out.println("Received city: " + city);
    //out.flush();
    System.out.println("Received city: " + city);

    //inserisco l'hotel
    insertHotelName(city, hotelName);
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
    }*/
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

    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    Gson gson = new Gson();
    String hotelName = in.readLine();
    System.out.println("Received hotel name: " + hotelName);
    //out.println(hotelName);
    //out.flush();
    String city = in.readLine();
    //out.println("Received city: " + city);
    //out.flush();
    System.out.println("Received city: " + city);
    if (!cities.contains(city)) {
      System.out.println("[ERROR] City not found");
      out.println(NO_HOTEL_FOUND);
      return;
    }
    //inserisco l'hotel
    if (hotelName == null) {
      out.println(NO_HOTEL_FOUND);
      return;
    }
    //aggiungo l'hotel alla lista degli hotel della città in questione se non è già presente
    insertHotelName(city, hotelName);

    //altrimenti, se non ho inserito l'hotel significa che non esiste
    if (hotels.get(city) == null || hotels.isEmpty()) {
      System.out.println("Nessun hotel trovato");
      out.println(NO_HOTEL_FOUND);
      return;
    } else {
      System.out.println("Hotel trovato");
      for (Hotel hotel : hotels.get(city)) {
        System.out.println("Scorro gli hotel della citta: " + hotel.getCity());
        if (hotel.getName().toLowerCase().trim().equals(hotelName)) {
          String responseString = hotel.toString();
          System.out.println("Sending response: " + responseString);
          sendData(out, responseString);
          break;
        }
      }
      System.out.println("Sending response: " + END);
      out.println(END);
      out.flush();
    }
  }

  /*
 public String searchAllHotels(String city) {
        try (JsonReader reader = new JsonReader(new FileReader(HOTELS_PATH))) {
            reader.beginArray();
            while (reader.hasNext()) {
                Hotel h = gson.fromJson(reader, Hotel.class);
                if (h.getCity().equals(city)) {
                    checkIfPresentAndAdd(hotels, h);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        ArrayList<Hotel> hotelList = hotels.get(city);
        if (hotelList != null) {
            Collections.sort(hotelList, Comparator.comparing(Hotel::getRankingRate).reversed());
            hotels.put(city, hotelList);
            for(int i = 0; i < hotelList.size(); i++) {
                sb.append(hotelList.get(i).toString());
                if(i != hotelList.size() - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
 */

  public static void searchAllHotels(BufferedReader in, PrintWriter out)
    throws IOException {
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    Gson gson = new Gson();
    String city = in.readLine().toLowerCase().trim();
    System.out.println("Received city: " + city);
    if (!cities.contains(city)) {
      out.println(NO_HOTEL_FOUND);
      return;
    }
    try (
      JsonReader reader = new JsonReader(
        new FileReader(PATH_AND_FILE_NAME_HOTELS)
      )
    ) {
      reader.beginArray();
      while (reader.hasNext()) {
        Hotel hotel = gson.fromJson(reader, Hotel.class);
        System.out.println("Hotel in città: " + hotel.getCity());
        if (hotel.getCity().toLowerCase().equals(city)) {
          System.out.println("Aggiungo l; hotel: " + hotel.getName());
          hotels.computeIfAbsent(city, c -> new ArrayList<>()).add(hotel);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    //altrimenti, per ogni hotel, controllo quelli non inseriti
    if (hotels.get(city) == null || hotels.isEmpty()) {
      out.println(NO_HOTEL_FOUND);
      return;
    } else {
      for (Hotel hotel : hotels.get(city)) {
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
  }
  /*public static synchronized void searchAllHotels(
    BufferedReader in,
    PrintWriter out
  ) throws IOException {
    
     * controllo cosa returnare
     
    ConcurrentHashMap<String, ArrayList<Hotel>> hotels = HotelsWithReviewsHandler
      .getInstance()
      .getHotelsWithReviews();
    String city = in.readLine().toLowerCase();
    System.out.println("Received city: " + city);

    //inserisco l'hotel
    insertAllHotelsInCity(city);

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
    
  }*/
}
