package classes;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 *  
 Devo creare una classe Review dove inserisco il tempo della Review, il badge e il punteggio
 E ordino in base a quello, dove inserisco tutto? Dentro 
 Uso una concurrentHashMap per la lista degli hotel, dove metto city, arraylist
 Ho la multihashmap per gli hotel e li ordino in base alle recensioni, con il metodo sort
 ogni qualvolta però che cambia l'elemento in testa alla lista della città, mando un messaggio udp 
 a tutti i dispositivi connessi
 Devo pensare a come considerare le recensioni, faccio una classe Review in cui ci metto semplicemente, voto totale, tempo e badge
 */
public class Review {

  private Float synVote;
  private Date date;
  private List<Float> ratings;

  public Review(Float synVote, List<Float> ratings) {
    this.synVote = synVote; // rappresenta la quality
    //this.quantity = quantity;
    this.date = new Date();
    this.ratings = ratings;

  }

  public Float getSynVote() {
    return synVote;
  }

  /*public static int getQuantity() {
    return quantity;
  }*/

  public Date getDate() {
    return date;
  }

  public void setSynVote(Float synVote) {
    this.synVote = synVote;
  }
}
/*
 * [
  {
    "name": "John Doe",
    "age": 30,
    "hobbies": ["reading", "travelling"],
    "address": {
      "street": "123 Main St",
      "city": "Wonderland",
      "zip": "12345"
    }
  },
  {
    "name": "Jane Smith",
    "age": 25,
    "hobbies": ["painting", "cycling"],
    "address": {
      "street": "456 Elm St",
      "city": "Nowhere",
      "zip": "54321"
    }
  }
]

 */
