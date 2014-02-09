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
	private Socket dataSocket;
	private boolean connected;
	private File currentDirectory;

	@SuppressWarnings("unused")
	private ClientSession() {

	}

	public ClientSession(Socket commandSocket, boolean connected) {
		super();
		this.commandSocket = commandSocket;
		this.connected = connected;
		this.currentDirectory = Main.directory;
	}

	public Socket getDataSocket() {
		return dataSocket;
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

}
