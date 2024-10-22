package classes;

public class User {

  /*
   *
   * badge = level of experience
   * rn = review number
   * usn = username
   * pwd= password
   *
   */
  int rn;
  String badge, usn, pwd;
  Boolean online;

  public User(String username, String password) {
    this.usn = username;
    this.pwd = password;
    //forse conviene chiamarlo badge
    this.badge = "recensore";
    this.rn = 0;
    this.online = false;
  }

  public String getUsn() {
    return this.usn;
  }

  public String getPwd() {
    return this.pwd;
  }

  public int getRn() {
    return this.rn;
  }

  public String getBadge() {
    return this.badge;
  }

  public Boolean getOnline() {
    return this.online;
  }

  public void incrementRN() {
    this.rn++;
    modifybadge(this.rn);
  }

  /*
   * Recensore, Recensore esperto, Contributore, Contributore
esperto, Contributore Super
   */
  public void modifybadge(int rn) {
    if (rn > 3) {
      this.badge = "Recensore esperto";
    } else if (rn > 5) {
      this.badge = "Contributore";
    } else if (rn > 7) {
      this.badge = "Contributore esperto";
    } else if (rn > 9) {
      this.badge = "Contributore Super";
    }
  }

  public synchronized void setOnline() {
    this.online = true;
  }

  public synchronized void setOffline() {
    this.online = false;
  }

  public synchronized void setUsn(String username) {
    this.usn = username;
  }

  public synchronized void setPwd(String password) {
    this.pwd = password;
  }

  public synchronized void setRn(int rn) {
    this.rn = rn;
  }

  public synchronized void setbadge(String badge) {
    this.badge = badge;
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
        usn = in.nextLine();
      }
      out.println(usn + " inserito correttamente");
      String pwd = in.nextLine();

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
        writer.close();
      }*/
