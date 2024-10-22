package server;

import java.net.InetAddress;

public class UdpMessage {

  private InetAddress ipAddress;
  private int port;
  private String message;

  public UdpMessage(InetAddress ipAddress, int port, String message) {
    this.ipAddress = ipAddress;
    this.port = port;
    this.message = message;
  }

  public InetAddress getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(InetAddress ipAddress) {
    this.ipAddress = ipAddress;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
