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
	private String username;

	@SuppressWarnings("unused")
	private ClientSession() {

	}

	public ClientSession(final Socket commandSocket, final boolean connected) {
		if (commandSocket == null) {
			throw new IllegalArgumentException(
					"argument commandSocket cannot be null");
		}
		this.commandSocket = commandSocket;
		this.connected = connected;
		this.currentDirectory = Server.getInstance().getRootDirectory();
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

	public void setDataPort(final int port) {
		if (port < 0 || 65536 <= port) {
			throw new IllegalArgumentException("Illegal port number:" + port);
		}
		this.dataPort = port;
	}

	public String getDataAddress() {
		return dataAddress;
	}

	public void setDataAddress(final String dataAddress) {
		if (dataAddress == null)
			throw new IllegalArgumentException(
					"argument dataAddress cannot be null");
		if (dataAddress.isEmpty())
			throw new IllegalArgumentException(
					"argument dataAddress cannot be empty");
		this.dataAddress = dataAddress;
	}

	/**
	 * Sets the current directory of the client.
	 * 
	 * @param directory
	 *            New directory
	 * @thorws {@link IllegalArgumentException} If argument is null or not a
	 *         directory
	 */
	public void setCurrentDirectory(final File directory) {
		if (directory == null) {
			throw new IllegalArgumentException(
					"argument directory cannot be null!");
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
					"argument directory is not a directory : " + directory);
		}
		this.currentDirectory = new File(directory.getAbsolutePath());
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		if (username == null) {
			throw new IllegalArgumentException(
					"username argument cannot be null");
		}
		this.username = username;
	}
}
