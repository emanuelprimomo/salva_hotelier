package server;

import classes.Hotel;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe singleton che gestisce gli hotel con le recensioni.
 */
public class HotelsWithReviewsHandler {

  private static String PATH_AND_FILE_NAME_HOTELS_REVIEWED =
    "src/data/hotelsReviewed.json";
  private static volatile HotelsWithReviewsHandler instance;
  private ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews;

  /**
   * Costruttore privato per evitare che venga istanziata una nuova classe.
   */
  private HotelsWithReviewsHandler() {
    hotelsWithReviews = new ConcurrentHashMap<>();
    
  }

  /**
   * Restituisce l'istanza di HotelsWithReviewsHandler.
   * @return
   */
  public static HotelsWithReviewsHandler getInstance() {
    if (instance == null) {
      instance = new HotelsWithReviewsHandler();
    }
    return instance;
  }

  /**
   * Restituisce la mappa degli hotel con le recensioni.
   * @return
   */
  public ConcurrentHashMap<String, ArrayList<Hotel>> getHotelsWithReviews() {
    return hotelsWithReviews;
  }

  /**
   * Imposta la mappa degli hotel con le recensioni.
   * @param hotelsWithReviews
   */
  public void setHotelsWithReviews(
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews
  ) {
    this.hotelsWithReviews = hotelsWithReviews;
  }
}
