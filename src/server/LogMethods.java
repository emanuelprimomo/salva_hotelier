package server;

import classes.User;
import client.InsertInformations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import server.RegMethods;

/**
 * Classe che contiene i metodi per il login e il logout
 * <p> Questa classe contiene i metodi per il login e il logout </p>
 * <p> Esempio di utilizzo del metodo loginUser </p>
 * <pre> BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
 * User user = LogMethods.loginUser(in, out, users); </pre>
 * <p> Esempio di utilizzo del metodo logout </p>
 * <pre> BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 * PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 * ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
 * LogMethods.logout(in, out, users); </pre>
 *
 */
public class LogMethods {

  /*  public static synchronized void registerUser(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users
  ) throws IOException {
    String usn = in.readLine();
    out.println("Received username: " + usn);
    User user = users.get(usn);
    while (user != null) {
      out.println("username esistente");
      System.out.println("username esistente");
      usn = in.readLine();
      user = users.get(usn);
    }
    out.println(usn + " inserito correttamente");
    String pwd = in.readLine();
    out.println("Received password: " + pwd);
    users.put(usn, new User(usn, pwd));
  }*/

  /**
   * Metodo per il login dell'utente
   * @param in
   * @param out
   * @param users
   * @return
   * @throws IOException
   */
  public static synchronized User loginUser(BufferedReader in, PrintWriter out)
    throws IOException {
    ConcurrentHashMap<String, User> users = UserManager
      .getInstance()
      .getUsers();
    String usn = in.readLine();

    System.out.println("Received username: " + usn);
    //Creo l'oggetto user per poterlo utilizzare nel ciclo for per vedere se l'utente è già registrato
    User user = new User(null, null);
    for (User u : users.values()) {
      if (u.getUsn().equals(usn)) {
        user = u;
        break;
      }
    }
    //Se l'utente non è registrato
    if (user.getUsn() == null) {
      out.println("username non esistente");
      while (true) {
        usn = in.readLine();
        user = users.get(usn);
        //Faccio reinserire l'username di un utente registrato
        if (user != null) {
          out.println(usn + " inserito correttamente");
          System.out.println(usn + " inserito correttamente");
          break;
        }
        out.println("username non esistente");
        System.out.println("username non esistente");
      }
    }
    //Se l'utente è già loggato
    else if (user.getOnline()) {
      out.println("user loggato");
      while (true) {
        usn = in.readLine();
        user = users.get(usn);
        //Faccio reinserire l'username di un utente non loggato
        if (user != null && !user.getOnline()) {
          out.println(usn + " inserito correttamente");
          System.out.println(usn + " inserito correttamente");
          break;
        }
        out.println("user loggato");
      }
    } else {
      //Se l'utente è registrato e non loggato, controllo la password inserita dall'utente e se è corretta lo loggo
      System.out.println("username corretto");
      out.println("username corretto");

      String pwd = in.readLine();
      System.out.println("Received password: " + pwd);
      if (user.getPwd().equals(pwd) && !user.getOnline()) {
        //Se la password è corretta e l'utente non è loggato
        user.setOnline();
        out.println("login eseguito");
        System.out.println("login eseguito");
      } else {
        while (true) {
          out.println("password errata");
          System.out.println("password errata, viene reinserita");
          pwd = in.readLine();
          //Se la password non è corretta
          if (user.getPwd().equals(pwd) && !user.getOnline()) {
            //Faccio reinserire la password
            System.out.println(
              "valore password user: " +
              user.getPwd() +
              "password ricevuta: " +
              pwd
            );

            user.setOnline();
            out.println("login eseguito");
            System.out.println("login eseguito");
            break;
          }
        }
      }
    }
    return user;
  }

  /**
   * Metodo per il logout dell'utente
   * @param in
   * @param out
   * @param users
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static synchronized void logout(BufferedReader in, PrintWriter out)
    throws FileNotFoundException, IOException {
    /*System.out.println("Entrati nel logout");
    String usn = in.readLine();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    List<User> userList = new ArrayList<>();

    try (FileReader reader = new FileReader(PATH_AND_FILE_NAME)) {
      System.out.println("Apro il file Users.json");
      Type userListType = new TypeToken<List<User>>() {}.getType();

      userList = gson.fromJson(reader, userListType);
      if (checkUserExist(userList, usn)) {
        out.println("logout eseguito");
        System.out.println("logout");*/
    ConcurrentHashMap<String, User> users = UserManager
      .getInstance()
      .getUsers();
    String usn = in.readLine();
    User user = users.get(usn);
    if (user != null) {
      user.setOffline();
      users.remove(usn);
      users.put(usn, user);
      out.println("logout eseguito");
      System.out.println("logout");
    } else {
      out.println("Utente non registrato");
      System.out.println("Utente non registrato");
      while (true) {
        usn = in.readLine();
        user = users.get(usn);
        if (user != null) {
          user.setOffline();
          users.remove(usn);
          users.put(usn, user);
          out.println("logout eseguito");
          System.out.println("logout");
          break;
        }
        out.println("Utente non registrato");
        System.out.println("Utente non registrato");
      }
    }
  }
}
