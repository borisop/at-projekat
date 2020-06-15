package client;

public class Node {
	public static final Node LOCAL = null;
	public String host;
	public int port;
	
	public Node(String host, int port) {
		this.host = host;
		this.port = port;
	}
}
