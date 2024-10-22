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

public class LogMethods {

  /*
   * qui manderò la concurrentHashMap di tutti gli utenti
   */
  //private static final String PATH_AND_FILE_NAME = "data/Users.json";

  public static synchronized void registerUser(
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
  }

  public static synchronized User loginUser(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users
  ) throws IOException {
    /*
     * considera anche se l'utente è già loggato
     */
    String usn = in.readLine();

    System.out.println("Received username: " + usn);
    //devo ottenere lo user in un qualche modo
    User user = new User(null, null);
    for (User u : users.values()) {
      if (u.getUsn().equals(usn)) {
        //System.out.println("entro nell'if");
        user = u;
        //System.out.println("Informazioni username: " + user.getUsn());
        break;
      }
    }
    if (user.getUsn() == null) {
      out.println("username non esistente");
      while (true) {
        usn = in.readLine();
        user = users.get(usn);
        if (user != null) {
          out.println(usn + " inserito correttamente");
          System.out.println(usn + " inserito correttamente");
          break;
        }
        out.println("username non esistente");
        System.out.println("username non esistente");
      }
    } else if (user.getOnline()) {
      out.println("user loggato");
      while (true) {
        usn = in.readLine();
        user = users.get(usn);
        // System.out.println("user gia loggato");
        if (user != null && !user.getOnline()) {
          out.println(usn + " inserito correttamente");
          System.out.println(usn + " inserito correttamente");
          break;
        }
        out.println("user loggato");
      }
    } else {
      System.out.println("username corretto");
      out.println("username corretto");

      String pwd = in.readLine();
      System.out.println("Received password: " + pwd);
      if (user.getPwd().equals(pwd) && !user.getOnline()) {
        //    User user = users.get(usn);
        user.setOnline();
        out.println("login eseguito");
        System.out.println("login eseguito");
      } else {
        out.println("password errata");
        System.out.println("password errata");
      }
    }

    return user;
  }

  public static synchronized void logout(
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users
  ) throws FileNotFoundException, IOException {
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

    String usn = in.readLine();
    User user = users.get(usn);
    user.setOffline();
    users.remove(usn);
    users.put(usn, user);
  }
}
