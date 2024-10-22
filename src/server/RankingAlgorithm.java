package server;

import classes.Hotel;
import classes.Review;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankingAlgorithm {

  /*'
    Ordinamento delle recensioni: Le recensioni vengono ordinate per data in ordine decrescente (più recenti prima).
Calcolo del punteggio: Per ogni recensione, il punteggio viene calcolato moltiplicando il voto sintetico (synVote) per il numero di voti.
Somma dei punteggi: I punteggi delle recensioni vengono sommati per ottenere il ranking totale.
Normalizzazione: Il ranking totale viene diviso per il numero totale di voti per ottenere un valore medio
     */
  public static void calculateRanking(Hotel hotel) {
    List<Review> reviews = hotel.getReviews();

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

    Float ranking = 0.0f;
    int totalVotes = reviews.size();

    // Calcola il punteggio per ogni recensione
    for (Review review : reviews) {
      Float synVote = review.getSynVote();

      ranking += synVote;
    }

    // Normalizza il ranking in base al numero totale di voti
    if (totalVotes > 0) {
      ranking /= totalVotes;
    }

    hotel.setRanking(ranking);
  }
}
/*
 * Assicurati che le classi Hotel e Review abbiano i metodi necessari (getReviews(), getDate(), getSynVote(), getVotes(), setRanking()).
 */
