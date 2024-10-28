package server;

import classes.Hotel;
import classes.Review;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RankingAlgorithm {

  /*'
    Ordinamento delle recensioni: Le recensioni vengono ordinate per data in ordine decrescente (più recenti prima).
Calcolo del punteggio: Per ogni recensione, il punteggio viene calcolato moltiplicando il voto sintetico (synVote) per il numero di voti.
Somma dei punteggi: I punteggi delle recensioni vengono sommati per ottenere il ranking totale.
Normalizzazione: Il ranking totale viene diviso per il numero totale di voti per ottenere un valore medio
     
  public static void calculateRanking(
    Hotel hotel,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews,
    UdpMessage udpMessage
  ) throws InterruptedException {
    List<Review> reviews = hotel.getReviews();
    Float ranking = 0.0f;
    Float totalVotes = (float) reviews.size();
    // Calcola il punteggio per ogni recensione

    for (Review review : reviews) {
      ranking += review.getSynVote();
    }

    // Normalizza il ranking in base al numero totale di voti
    if (totalVotes > 0) {
      ranking = ranking / totalVotes;
    }
    System.out.println(
      "[CALCULATE RANKING] Hotel " + hotel.getName() + " - Ranking: " + ranking
    );
    hotel.setRanking(ranking);
    // Ordina le recensioni per data, più recenti prima
    Collections.sort(
      reviews,
      new Comparator<Review>() {
        @Override
        public int compare(Review r1, Review r2) {
          return r2.getDate().compareTo(r1.getDate());
        }
      }
    );
    //mi devo passare l'udp message per poterlo passare al metodo che aggiorna il ranking

    //qui chiamo il metodo per aggiornare il ranking
    HotelRankingUpdater.updateHotelRankings(
      hotel,
      hotelsWithReviews,
      udpMessage
    );
  }
}*/

  // Assicurati che le classi Hotel e Review abbiano i metodi necessari (getReviews(), getDate(), getSynVote(), getVotes(), setRanking()).

  public static void calculateRanking(
    Hotel hotel,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews,
    UdpMessage udpMessage
  ) throws InterruptedException {
    float ranking = calculateRankingScore(hotel);
    hotel.setRanking(ranking);
    System.out.println(
      "[CALCULATE RANKING] Hotel " + hotel.getName() + " - Ranking: " + ranking
    );
    //Aggiorno il ranking
    HotelRankingUpdater.updateHotelRankings(
      hotel,
      hotelsWithReviews,
      udpMessage
    );
    /*for (List<Hotel> hotels : hotelsWithReviews.values()) {
      hotels.sort(
        new Comparator<Hotel>() {
          @Override
          public int compare(Hotel h1, Hotel h2) {
            float ranking1 = calculateRankingScore(h1);
            float ranking2 = calculateRankingScore(h2);

            return Float.compare(ranking2, ranking1);
          }
        }
      );
    }*/
    return;
  }

  private static float calculateRankingScore(Hotel hotel) {
    float quality = calculateWeightedQuality(hotel);
    int quantity = hotel.getTotalVotes();
    int recency = calculateRecentVotes(hotel);

    // Calcola il ranking come somma pesata dei tre parametri
    return quality + quantity + recency;
  }

  private static float calculateWeightedQuality(Hotel hotel) {
    long nowInSeconds = Instant.now().getEpochSecond();
    float totalWeightedQuality = 0.0f;
    float totalWeight = 0.0f;
    for (Review review : hotel.getReviews()) {
      long reviewInSeconds = review.getDate().toInstant().getEpochSecond();
      long secondsSinceReview = nowInSeconds - reviewInSeconds;
      float weight = 1.0f / (secondsSinceReview + 1); // Più recente è la recensione, maggiore è il peso
      totalWeightedQuality += review.getSynVote() * weight;
      totalWeight += weight;
    }
    return totalWeight > 0 ? totalWeightedQuality / totalWeight : 0.0f;
  }

  private static int calculateRecentVotes(Hotel hotel) {
    long currentTimeInMillis =
      LocalDate.now().toEpochDay() * 24L * 60 * 60 * 1000;
    long thirtyDaysAgoInMillis =
      currentTimeInMillis - TimeUnit.DAYS.toMillis(30);
    int recentVotes = 0;
    for (Review review : hotel.getReviews()) {
      long reviewInSeconds = review.getDate().getTime(); // Data della recensione in millisecondi
      if (reviewInSeconds > thirtyDaysAgoInMillis) {
        recentVotes++;
      }
      /*opzione senza i 30 giorni meno realistica
      if (reviewInSeconds > currentTimeInMillis) {
        recentVotes++;
      }*/
    }
    return recentVotes;
  }
}
