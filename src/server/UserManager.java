package server;

import classes.User;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe singleton che gestisce gli utenti.
 */
public final class UserManager {

  private static volatile UserManager instance;
  private ConcurrentHashMap<String, User> users;

  /**
   * Costruttore privato per evitare che venga istanziata una nuova classe.
   */
  private UserManager() {
    users = new ConcurrentHashMap<>();
  }

  /**
   * Restituisce l'istanza di UserManager.
   * @return l'istanza di UserManager
   */
  public static UserManager getInstance() {
    if (instance == null) {
      synchronized (UserManager.class) {
        if (instance == null) {
          instance = new UserManager();
        }
      }
    }
    return instance;
  }

  /**
   * Restituisce la mappa degli utenti.
   * @return la mappa degli utenti
   */
  public ConcurrentHashMap<String, User> getUsers() {
    return users;
  }

  /**
   * Aggiunge un utente alla mappa.
   * @param user l'utente da aggiungere
   */
  public void addUser(User user) {
    users.put(user.getUsn(), user);
  }

  /**
   * Restituisce l'utente con lo username specificato.
   * @param usn lo username dell'utente da restituire
   * @return l'utente associato allo username specificato, o null se non esiste
   */
  public User getUser(String usn) {
    return users.get(usn);
  }

  /**
   * Rimuove l'utente con lo username specificato.
   * @param usn lo username dell'utente da rimuovere
   */
  public void removeUser(String usn) {
    users.remove(usn);
  }

  /**
   * Imposta la mappa degli utenti.
   */
  public void setUsers(ConcurrentHashMap<String, User> users) {
    this.users = users;
  }

  public void put(String usn, User user) {
    users.put(usn, user);
  }

  // Metodo per ottenere la dimensione della mappa degli utenti
  public int size() {
    return users.size();
  }

  // Metodo per ottenere una collezione di tutti gli utenti
  public Collection<User> values() {
    return users.values();
  }
}
