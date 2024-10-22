package server;

import classes.User;
import java.io.BufferedReader;
import java.io.IOException;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
//import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class RegMethods {

  public static String PATH_AND_FILE_NAME = "src/data/Users.json";

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

  public static synchronized void registerUser(
    Socket socket,
    BufferedReader in,
    PrintWriter out,
    ConcurrentHashMap<String, User> users
  ) throws IOException {
    System.out.println("Client connected?" + socket.isConnected());
    Boolean userExist = false;
    String usn = in.readLine();
    out.println("Received username: " + usn);
    System.out.println("Received username:" + usn);
    if (users.size() == 0) {
      //out.println(usn + " inserito correttamente");
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
