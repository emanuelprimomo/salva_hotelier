package server;

import classes.Hotel;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class HotelsWithReviewsHandler {

  private static HotelsWithReviewsHandler instance;
  private ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews;

  private HotelsWithReviewsHandler() {
    hotelsWithReviews = new ConcurrentHashMap<>();
  }

  public static HotelsWithReviewsHandler getInstance() {
    if (instance == null) {
      instance = new HotelsWithReviewsHandler();
    }
    return instance;
  }

  public ConcurrentHashMap<String, ArrayList<Hotel>> getHotelsWithReviews() {
    return hotelsWithReviews;
  }

  public void setHotelsWithReviews(
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews
  ) {
    this.hotelsWithReviews = hotelsWithReviews;
  }
}
