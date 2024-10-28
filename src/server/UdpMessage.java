package server;

import java.net.InetAddress;

public class UdpMessage {

  private String message;
  private InetAddress group;
  private int port;

  public UdpMessage(String message, InetAddress group, int port) {
    this.message = message;
    this.group = group;
    this.port = port;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public InetAddress getGroup() {
    return group;
  }

  public void setGroup(InetAddress group) {
    this.group = group;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
