package classes;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Hotel {

  private int id;
  private String name;
  private String city;
  private String description;
  private String phone;
  private String[] services;
  private Float rate;
  private Map<String, Float> ratings;
  private Float ranking; //utilizzata per ordinare
  private List<Review> reviews;

  /* hotel con tutte le operazioni */
  public Hotel(
    int id,
    String name,
    String city,
    String description,
    String phone,
    String[] services,
    Float rate,
    Map<String, Float> ratings,
    Float ranking,
    List<Review> reviews
  ) {
    this.id = id;
    this.name = name;
    this.city = city;
    this.description = description;
    this.phone = phone;
    this.services = services;
    this.rate = rate;
    this.ratings = ratings;
    this.reviews = reviews;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getCity() {
    return this.city;
  }

  public String getDescription() {
    return this.description;
  }

  public String getPhone() {
    return this.phone;
  }

  public String[] getServices() {
    return this.services;
  }

  public Float getRate() {
    return this.rate;
  }

  // penso che lo cambierò in float
  public Map<String, Float> getRatings() {
    return this.ratings;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public Float getRanking() {
    return ranking;
  }

  public int getRecentVotes() {
    // Conta le recensioni degli ultimi 30 giorni
    LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
    return (int) reviews
      .stream()
      .filter(review -> {
        LocalDate reviewDate = review
          .getDate()
          .toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
        return reviewDate.isAfter(thirtyDaysAgo);
      })
      .count();
  }

  public int getTotalVotes() {
    // Implementa la logica per ottenere il numero totale di voti
    return reviews.size();
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setdescription(String description) {
    this.description = description;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setServices(String[] services) {
    this.services = services;
  }

  public void setRate(Float rate) {
    this.rate = rate;
  }

  public void setRatings(Map<String, Float> ratings) {
    this.ratings = ratings;
  }

  public void setRanking(Float ranking) {
    this.ranking = ranking;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }

  public void addReview(Review review) {
    this.reviews.add(review);
  }

  @Override
  public String toString() {
    String response = "";

    for (String service : services) {
      response += service + ", ";
    }
    response += "\n" + "Rate: " + rate + ", " + "\n" + "Ratings: ";
    //scorro i ratings e li aggiungo alla stringa
    for (Map.Entry<String, Float> entry : ratings.entrySet()) {
      response += entry.getKey() + ": " + entry.getValue() + ", ";
    }
    response = response.substring(0, response.length() - 2);
    return (
      "Hotel: " +
      "name= " +
      name +
      ',' +
      System.lineSeparator() +
      "city= " +
      city +
      ',' +
      System.lineSeparator() +
      "description= '" +
      description +
      '\'' + // Chiusura corretta della stringa
      ',' +
      System.lineSeparator() +
      "phone= '" +
      phone +
      '\'' + // Chiusura corretta della stringa
      ',' +
      System.lineSeparator() +
      response +
      System.lineSeparator()
    );
  }

  public void setReviews(ArrayList arrayList) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
      "Unimplemented method 'setReviews'"
    );
  }
}
