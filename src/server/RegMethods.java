package server;

import classes.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Classe che contiene i metodi per la registrazione di un utente e il controllo se l'utente esiste già o meno
 * <p> Questa classe contiene i metodi per la registrazione di un utente e il controllo se l'utente esiste già o meno </p>
 *
 *
 * <p> Esemplificazione del metodo checkUserExist </p>
 * <pre> List<User> utenti = Arrays.asList(new User("mario123"), new User("luigi456"));
 * Boolean esiste = GestioneUtenti.checkUserExist(utenti, "mario123");
 * if (esiste) {
 * System.out.println("L'utente esiste nella lista.");
 * } else {
 * System.out.println("L'utente non è presente.");
 * } </pre>
 *
 */
public class RegMethods {

  public static String PATH_AND_FILE_NAME = "src/data/Users.json";

  /**
   * Verifica se un utente con uno specifico username esiste in una lista di utenti.
   * Questo metodo è thread-safe, poiché è sincronizzato.
   *
   * @param userList la lista di utenti in cui cercare l'username specificato
   * @param usn lo username da verificare
   * @return `true` se esiste un utente con lo username specificato nella lista, `false` altrimenti
   */
  public static synchronized Boolean checkUserExist(
    List<User> userList,
    String usn
  ) {
    Boolean userExist = false;
    for (User user : userList) {
      if (user.getUsn().equals(usn)) {
        userExist = true;
        break;
      }
    }
    return userExist;
  }

  /**
   * Registra un nuovo utente nel sistema verificando se lo username è già presente.
   * Interagisce con il client tramite `Socket` per ricevere lo username e la password.
   * Se l'utente esiste già, notifica il client e non effettua la registrazione.
   * Questo metodo è thread-safe poiché è sincronizzato e utilizza una `ConcurrentHashMap`
   * per gestire gli utenti.
   *
   * @param socket il socket di connessione al client, utilizzato per stabilire la connessione
   *               e verificare se è attiva
   * @param in il `BufferedReader` per ricevere input dal client, come username e password
   * @param out il `PrintWriter` per inviare risposte al client in merito allo stato della registrazione
   * @param users una mappa concorrente che contiene gli utenti registrati. La mappa usa come chiave
   *              lo username e come valore l'oggetto `User`, che include username e password
   * @throws IOException se si verifica un errore durante la comunicazione con il client
   *
   * <p>Esempio d'uso:</p>
   * <pre>
   *     Socket clientSocket = serverSocket.accept();
   *     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   *     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
   *     registerUser(clientSocket, in, out, users);
   * </pre>
   *
   * <p><b>Dettagli sul funzionamento:</b> Il metodo verifica se esistono utenti nella mappa `users`:
   * <ul>
   *   <li>Se la mappa è vuota, legge username e password dal client e li aggiunge direttamente alla mappa.</li>
   *   <li>Se la mappa contiene già utenti, cerca lo username nella mappa:
   *     <ul>
   *       <li>Se esiste, notifica il client che l'username è già in uso.</li>
   *       <li>Se non esiste, registra l'utente dopo aver letto la password dal client.</li>
   *     </ul>
   *   </li>
   * </ul>
   * </p>
   */
  public static synchronized void registerUser(
    Socket socket,
    BufferedReader in,
    PrintWriter out
  ) throws IOException {
    UserManager users = UserManager.getInstance();
    System.out.println("Client connected?" + socket.isConnected());
    Boolean userExist = false;
    String usn = in.readLine();
    out.println("Received username: " + usn);
    System.out.println("Received username:" + usn);
    if (users.size() == 0) {
      String pwd = in.readLine();
      System.out.println("Received password: " + pwd);
      out.println("Received password: " + pwd);
      users.put(usn, new User(usn, pwd));
    } else {
      for (User user : users.values()) {
        if (user.getUsn().equals(usn)) {
          out.println("username esistente");
          System.out.println("username esistente");
          userExist = true;
          break;
        }
      }
      System.out.println("Controllo user exists");
      if (!userExist) {
        out.println(usn + " inserito correttamente");
        String pwd = in.readLine();
        out.println("Received password: " + pwd);
        users.put(usn, new User(usn, pwd));
      }
    }
    /*Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userList = new ArrayList<>();
    try (FileReader reader = new FileReader(PATH_AND_FILE_NAME)) {
      System.out.println("Apro il file Users.json");
      Type userListType = new TypeToken<List<User>>() {}.getType();

      userList = gson.fromJson(reader, userListType);

      while (checkUserExist(userList, usn)) {
        out.println("username esistente");
        System.out.println("username esistente");
        usn = in.readLine();
      }
      out.println(usn + " inserito correttamente");
      String pwd = in.readLine();

      out.println("Received password: " + pwd);*/
    /*
     * ricevo l'usename e la password
     */
    /*System.out.println("Received username: " + usn + " and password: " + pwd);
      if (!userExist) {
        /* userList.size() mi fornisce l'id 
        userList.add(new User(userList.size(), usn, pwd));
        String json = gson.toJson(userList);
        FileWriter writer = new FileWriter(PATH_AND_FILE_NAME);
        System.out.println("Aggiunto user in lista");
        writer.write(json);
        writer.close();*/
  }
}/*catch (IOException e) {
      e.printStackTrace();
    }
  }
}*/
