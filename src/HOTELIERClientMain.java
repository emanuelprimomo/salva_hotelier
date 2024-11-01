/*
 * Importo le librerie che ho creato
 */
import client.*;
/*
 * Importo le liberire che ho installato nell'ambiente
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Classe principale del client
 * 
 */
public class HOTELIERClientMain {

  static final String HOST_NAME = "localhost";
  static final int PORT = 8888;
  static final String EOF = "EOF";
  static final String END = "END";

  /*
  @SuppressWarnings("deprecation")
  public static class UDPThread implements Runnable {

    public void run() {
      
       * Ricezione messaggio UDp
       

      MulticastSocket socketUDP = null;
      try {
        socketUDP = new MulticastSocket(4446);
      } catch (IOException e) {
        e.printStackTrace();
      }
      //crea il socket
      InetAddress group = null;
      try {
        group = InetAddress.getByName("230.0.0.0");
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
      // Indirizzo del gruppo multicast
      try {
        socketUDP.joinGroup(group);
      } catch (IOException e) {
        e.printStackTrace();
      }
      // Si unisce al gruppo
      byte[] buffer = new byte[1024]; // Buffer per il pacchetto
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // Crea il datagramma

      try {
        System.out.println("Ricezione in corso...");
        socketUDP.receive(packet); // Riceve il pacchetto
        System.out.println("Pacchetto ricevuto");
      } catch (IOException e) {
        System.out.println("Errore durante la ricezione del pacchetto");
      }
      String received = new String(packet.getData(), 0, packet.getLength()); // Converte i byte in stringa
      synchronized (System.out) {
        System.out.println("Received: " + received); // Stampa il messaggio ricevuto
      }
    }
  }*/

  
  public static void main(String[] args)
    throws UnknownHostException, IOException, InterruptedException {
    Scanner in = new Scanner(System.in);
    //Scanner scan = null;
    String command = null;
    String serverResponse = null;
    String userConnected = null;
    String usn = null;
    String pwd = null;
    String city = null;
    String hotelName = null;
    String hotelInformations = "";

    Socket socket = new Socket(HOST_NAME, PORT);

    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    BufferedReader inServer = new BufferedReader(
      new InputStreamReader(socket.getInputStream())
    ); // qui ricevo le risposte dal server
    /*
     * variabile utilizzata per vedere se un utente è loggato
     */
    Boolean log = false;
    Scanner scan = new Scanner(socket.getInputStream());
    Boolean stop = false;

    //Thread thread = new Thread(new UDPThread());
    MulticastSocket socketUDP = new MulticastSocket();
    UdpClientThread udpListener = new UdpClientThread(socketUDP);
    udpListener.start();
    SessionInitializer.sessionMethods(socket, out, inServer, socketUDP);
  }
}
