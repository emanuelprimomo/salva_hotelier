package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class UdpClientThread extends Thread {

  private int port = 8888;
  private String multicastGroup = "230.0.0.0";
  private MulticastSocket socketUDP;

  public UdpClientThread(MulticastSocket socketUDP) {
    this.port = port;
    this.multicastGroup = multicastGroup;
    this.socketUDP = socketUDP;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void run() {
    try {
      this.socketUDP = new MulticastSocket(port);
      InetAddress group = InetAddress.getByName(multicastGroup);
      this.socketUDP.joinGroup(group);

      byte[] buffer = new byte[1024]; // Buffer per il pacchetto
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // Crea il datagramma

      System.out.println(
        "UDP Listener avviato sulla porta " +
        port +
        " e gruppo " +
        multicastGroup
      );

      while (true) {
        try {
          System.out.println("Ricezione in corso...");
          socketUDP.receive(packet); // Riceve il pacchetto
          System.out.println("Pacchetto ricevuto");

          String received = new String(packet.getData(), 0, packet.getLength()); // Converte i byte in stringa
          synchronized (System.out) {
            System.out.println("Received: " + received); // Stampa il messaggio ricevuto
          }
        } catch (IOException e) {
          System.out.println("Errore durante la ricezione del pacchetto");
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (socketUDP != null && !socketUDP.isClosed()) {
      try {
        socketUDP.leaveGroup(InetAddress.getByName(multicastGroup));
        socketUDP.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
