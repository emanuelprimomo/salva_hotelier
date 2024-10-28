package server;

import classes.Hotel;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HotelRankingUpdater {

  public static synchronized void updateHotelRankings(
    Hotel hotel,
    ConcurrentHashMap<String, ArrayList<Hotel>> hotelsWithReviews,
    UdpMessage udpMessage
  ) throws InterruptedException {
    // Ottieni la lista degli hotel per la città
    String city = hotel.getCity();
    List<Hotel> hotelList = hotelsWithReviews.get(city);
    if (hotelList == null) {
      return;
    }
    //UDP message da reperire
    // Ottieni il vecchio primo posto
    Hotel oldFirstPlace = hotelList.isEmpty() ? null : hotelList.get(0);

    // Ordina la lista degli hotel
    /*Collections.sort(
      hotelList,
      new Comparator<Hotel>() {
        @Override
        public int compare(Hotel h1, Hotel h2) {
          // Confronta per ranking
          int rankCompare = Float.compare(h2.getRanking(), h1.getRanking());
          if (rankCompare != 0) {
            return rankCompare;
          }

          // In caso di parità, confronta per numero di voti più recenti
          int recentVotesCompare = Integer.compare(
            h2.getRecentVotes(),
            h1.getRecentVotes()
          );
          if (recentVotesCompare != 0) {
            return recentVotesCompare;
          }

          // In caso di parità, confronta per numero totale di voti
          int totalVotesCompare = Integer.compare(
            h2.getTotalVotes(),
            h1.getTotalVotes()
          );
          if (totalVotesCompare != 0) {
            return totalVotesCompare;
          }

          // In caso di parità, confronta per nome dell'hotel in ordine alfabetico
          return h1.getName().compareTo(h2.getName());
        }
      }
    );*/
    for (List<Hotel> hotels : hotelsWithReviews.values()) {
      hotels.sort(
        new Comparator<Hotel>() {
          @Override
          public int compare(Hotel h1, Hotel h2) {
            int rankCompare = Float.compare(h2.getRanking(), h1.getRanking());
            if (rankCompare != 0) {
              return rankCompare;
            }

            // In caso di parità, confronta per numero di voti più recenti
            int recentVotesCompare = Integer.compare(
              h2.getRecentVotes(),
              h1.getRecentVotes()
            );
            if (recentVotesCompare != 0) {
              return recentVotesCompare;
            }

            // In caso di parità, confronta per numero totale di voti
            int totalVotesCompare = Integer.compare(
              h2.getTotalVotes(),
              h1.getTotalVotes()
            );
            if (totalVotesCompare != 0) {
              return totalVotesCompare;
            }
            // In caso di parità, confronta per nome dell'hotel in ordine alfabetico
            return h1.getName().compareTo(h2.getName());
          }
        }
      );
    }
    //questo credo di cambiarlo

    // Ottieni il nuovo primo posto
    Hotel newFirstPlace = hotelList.isEmpty() ? null : hotelList.get(0);

    // Se il primo è cambiato, invia un messaggio UDP a tutti i dispositivi connessi
    if (oldFirstPlace != newFirstPlace) {
      // Invia messaggio UDP
      /*
       * Mi devo passare l'indirizzo del gruppo multicast e la porta
       
      Thread.sleep(5000);
      System.out.println("TEST UDP");
      String message = "Hello, multicast!"; // Messaggio da inviare
      DatagramPacket packet = new DatagramPacket(
        message.getBytes(),
        message.length(),
        group,
        4446
      );
      group, port e basta 
      try {
        socketUDP.send(packet);
        System.out.println("Messaggio inviato: " + message);
      } catch (IOException e) {
        e.printStackTrace();
      }
      /*
       * FINE TEST UDP
       */
      //String message = "Il primo posto per la città di " + city + " è stato assegnato a " + newFirstPlace.getName();
      udpMessage.setMessage(
        "Il primo posto per la città di " +
        city +
        " è stato assegnato a " +
        newFirstPlace.getName()
      );
      sendUdpMessage(udpMessage);
    }

    // Aggiorna la lista degli hotel ordinata
    hotelsWithReviews.put(city, new ArrayList<>(hotelList));
    return;
  }

  private static void sendUdpMessage(UdpMessage udpMessage)
    throws InterruptedException {
    try {
      DatagramSocket socket = new DatagramSocket();
      byte[] buffer = udpMessage.getMessage().getBytes();
      System.out.println("Invio messaggio UDP: " + udpMessage.getMessage());
      DatagramPacket packet = new DatagramPacket(
        buffer,
        buffer.length,
        udpMessage.getGroup(),
        udpMessage.getPort()
      );
      socket.send(packet);
      //non chiudo la connessione, deve rimanere up
    } catch (IOException e) {
      System.out.println("Errore durante l'invio del messaggio UDP");
      e.printStackTrace();
    }
  }
}
