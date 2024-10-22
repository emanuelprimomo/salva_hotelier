package client;

import java.util.Scanner;

/*
 *
 * Classe utilizzata per inserire le informazioni da parte dell'utente
 *
 */
public class InsertInformations {

  public static String insertUsername(Scanner in) {
    String usn, responseFromServer;
    in = new Scanner(System.in);
    System.out.println("Inserire username:");
    usn = in.nextLine();
    return usn;
  }

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

  public static String insertCity(Scanner in) {
    String city;
    in = new Scanner(System.in);
    System.out.println("Inserire city:");
    city = in.nextLine();
    return city;
  }

  public static String insertHotelName(Scanner in) {
    String hotelName;
    in = new Scanner(System.in);
    System.out.println("Inserire hotel name:");
    hotelName = in.nextLine();
    return hotelName;
  }

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
