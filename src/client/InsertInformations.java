package client;

import java.util.Scanner;

/**
 * Classe che contiene i metodi per inserire le informazioni
 * <p> Questa classe contiene i metodi per inserire le informazioni </p>
 * <p> Esempio di utilizzo del metodo insertUsername </p>
 * <pre> Scanner in = new Scanner(System.in);
 * String usn = InsertInformations.insertUsername(in); </pre>
 * <p> Esempio di utilizzo del metodo insertPassword </p>
 * <pre> Scanner in = new Scanner(System.in);
 * String pwd = InsertInformations.insertPassword(in); </pre>
 * <p> Esempio di utilizzo del metodo insertCity </p>
 * <pre> Scanner in = new Scanner(System.in);
 * String city = InsertInformations.insertCity(in); </pre>
 * <p> Esempio di utilizzo del metodo insertHotelName </p>
 * <pre> Scanner in = new Scanner(System.in);
 * String hotelName = InsertInformations.insertHotelName(in); </pre>
 * <p> Esempio di utilizzo del metodo insertRate </p>
 * <pre> Scanner in = new Scanner(System.in);
 * Float rate = InsertInformations.insertRate(in); </pre>
 * <p> Esempio di utilizzo del metodo insertLevelClean </p>
 * <pre> Scanner in = new Scanner(System.in);
 * Float review = InsertInformations.insertLevelClean(in); </pre>
 * <p> Esempio di utilizzo del metodo insertLevelPosition </p>
 * <pre> Scanner in = new Scanner(System.in);
 * Float review = InsertInformations.insertLevelPosition(in); </pre>
 * <p> Esempio di utilizzo del metodo insertLevelService </p>
 * <pre> Scanner in = new Scanner(System.in);
 * Float review = InsertInformations.insertLevelService(in); </pre>
 * <p> Esempio di utilizzo del metodo insertLevelQuality </p>
 * <pre> Scanner in = new Scanner(System.in);
 * Float review = InsertInformations.insertLevelQuality(in); </pre>
 * <p> Esempio di utilizzo del metodo insertGlobalScore </p>
 * <pre> Scanner in = new Scanner(System.in);
 * Float review = InsertInformations.insertGlobalScore(in); </pre>
 * <p> Esempio di utilizzo del metodo insertReview </p>
 * <pre> Scanner in = new Scanner(System.in);
 * String review = InsertInformations.insertReview(in); </pre>
 *
 */
public class InsertInformations {

  /**
   * Metodo per inserire lo username
   * @param in
   * @return lo username inserito
   */
  public static String insertUsername(Scanner in) {
    String usn, responseFromServer;
    in = new Scanner(System.in);
    System.out.println("Inserire username:");
    usn = in.nextLine();
    return usn;
  }

  /**
   * Metodo per inserire la password
   * @param in
   * @return
   */
  public static String insertPassword(Scanner in) {
    String pwd;
    System.out.println("Inserire password:");
    pwd = in.nextLine();
    while (pwd.length() < 8) {
      System.out.println("Password troppo corta, reinserire password:");
      pwd = in.nextLine();
    }
    return pwd;
  }

  /**
   * Metodo per inserire la città
   * @param in
   * @return
   */
  public static String insertCity(Scanner in) {
    String city;
    in = new Scanner(System.in);
    System.out.println("Inserire city:");
    city = in.nextLine();
    return city;
  }

  /**
   * Metodo per inserire il nome dell'hotel
   * @param in
   * @return
   */
  public static String insertHotelName(Scanner in) {
    String hotelName;
    in = new Scanner(System.in);
    System.out.println("Inserire hotel name:");
    hotelName = in.nextLine();
    return hotelName;
  }

  /**
   * Metodo per inserire il rate della recensione
   * @param in
   * @return
   */
  public static Float insertRate(Scanner in) {
    Float rate;
    in = new Scanner(System.in);
    System.out.println("Inserire rate:");
    rate = in.nextFloat();
    while (rate < 0 || rate > 5) {
      System.out.println("Reinserire rate compreso tra 0 e 5 :");
      rate = in.nextFloat();
    }
    return rate;
  }

  /**
   * Metodo per inserire il livello di pulizia della recensione
   * @param in
   * @return
   */
  public static Float insertLevelClean(Scanner in) {
    Float review;
    in = new Scanner(System.in);
    System.out.println("Inserire livello di pulizia (0-5):");
    review = in.nextFloat();
    while (review < 0 || review > 5) {
      System.out.println("Reinserire livello di pulizia compreso tra 0 e 5 :");
      review = in.nextFloat();
    }
    return review;
  }

  /**
   * Metodo per inserire il livello di posizione della recensione
   * @param in
   * @return
   */
  public static Float insertLevelPosition(Scanner in) {
    Float review;
    in = new Scanner(System.in);
    System.out.println("Inserire livello di posizione (0-5):");
    review = in.nextFloat();
    while (review < 0 || review > 5) {
      System.out.println(
        "Reinserire livello di posizione compreso tra 0 e 5 :"
      );
      review = in.nextFloat();
    }
    return review;
  }

  /**
   * Metodo per inserire il livello di servizi della recensione
   * @param in
   * @return
   */
  public static Float insertLevelService(Scanner in) {
    Float review;
    in = new Scanner(System.in);
    System.out.println("Inserire livello di servizi (0-5):");
    review = in.nextFloat();
    while (review < 0 || review > 5) {
      System.out.println("Reinserire livello di servizi compreso tra 0 e 5 :");
      review = in.nextFloat();
    }
    return review;
  }

  /**
   * Metodo per inserire il livello di qualità della recensione
   * @param in
   * @return
   */
  public static Float insertLevelQuality(Scanner in) {
    Float review;
    in = new Scanner(System.in);
    System.out.println("Inserire livello di qualità (0-5):");
    review = in.nextFloat();
    while (review < 0 || review > 5) {
      System.out.println("Reinserire livello di qualità compreso tra 0 e 5 :");
      review = in.nextFloat();
    }
    return review;
  }

  /**
   * Metodo per inserire il punteggio globale della recensione
   * @param in
   * @return
   */
  public static Float insertGlobalScore(Scanner in) {
    Float review;
    in = new Scanner(System.in);
    System.out.println("Inserire score globale (0-5):");
    review = in.nextFloat();
    while (review < 0 || review > 5) {
      System.out.println("Reinserire score globale compreso tra 0 e 5 :");
      review = in.nextFloat();
    }
    return review;
  }
}
