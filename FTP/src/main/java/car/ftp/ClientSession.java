package main.java.car.ftp;

import java.io.File;
import java.net.Socket;

/**
 * Keeps all the data related to the client session:
 * <ul>
 * <li>command socket</li>
 * <li>current directory></li>
 * <li>data socket</li>
 * <li></li>
 * </ul>
 * 
 * @author dorian
 * 
 */
public class ClientSession {
	private Socket commandSocket;
	private boolean connected;
	private File currentDirectory;
	private int dataPort = 0;
	private String dataAddress;

	@SuppressWarnings("unused")
	private ClientSession() {

	}

	public ClientSession(Socket commandSocket, boolean connected) {
		super();
		this.commandSocket = commandSocket;
		this.connected = connected;
		this.currentDirectory = Server.SERVER.getRootDirectory();
	}

	public Socket getCommandSocket() {
		return commandSocket;
	}

	public File getCurrentDirectory() {
		return currentDirectory;
	}

	public boolean isConnected() {
		return connected;
	}

	public int getDataPort() {
		return dataPort;
	}
	public void setPort(final int port) {
		if (port < 0 || port > 65536) {
			throw new IllegalArgumentException("Illegal port number:" + port);
		}
		this.dataPort = port;
	}

	public String getDataAddress() {
		return dataAddress;
	}

	public void setDataAddress(String dataAddress) {
		this.dataAddress = dataAddress;
	}

	public void setDataPort(int dataPort) {
		this.dataPort = dataPort;
	}
}
