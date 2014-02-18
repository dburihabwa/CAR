package main.java.car.ftp;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Keeps all the data related to the client session:
 * <ul>
 * <li>command socket</li>
 * <li>current directory></li>
 * <li>data socket information</li>
 * <li></li>
 * </ul>
 * 
 * @author dorian
 * 
 */
public class ClientSession {
	private Socket commandSocket;
	private File currentDirectory;
	private int dataPort = 0;
	private String dataAddress;
	private String username;
	public ServerSocket serverSocket;
	private Socket passiveSocket;

	private final Logger logger = Logger.getAnonymousLogger();

	@SuppressWarnings("unused")
	private ClientSession() {

	}

	public ClientSession(final Socket commandSocket, final boolean connected) {
		if (commandSocket == null) {
			throw new IllegalArgumentException(
					"argument commandSocket cannot be null");
		}
		this.commandSocket = commandSocket;
		this.currentDirectory = Server.getInstance().getRootDirectory();
	}

	public Socket getCommandSocket() {
		return commandSocket;
	}

	public File getCurrentDirectory() {
		return currentDirectory;
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

	/**
	 * Tries to return the data socket regrardless of whether the server is in
	 * active or passive mode.
	 * 
	 * @return Data socket
	 * @throws IOException
	 *             If an error occurs while getting the new data socket
	 */
	public Socket getDataSocket() throws IOException {
		Socket socket = null;
		if (serverSocket != null) {
			socket = passiveSocket;
		} else {
			socket = new Socket(dataAddress, dataPort);
		}
		return socket;
	}

	/**
	 * Sets the client in passive mode and passively listens for a client
	 * connection that will be used in a future command.
	 * 
	 * @return The server socket that is listening for the new connection.
	 * @throws IOException
	 *             If an error occurs while setting up the socket server
	 */
	public ServerSocket setPassiveMode() throws IOException {
		this.serverSocket = new ServerSocket(0);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket.setSoTimeout(15000);
					passiveSocket = serverSocket.accept();
				} catch (IOException e) {
					logger.log(
							Level.WARNING,
							"The server didn't revceive any connection from the client",
							e);
				}
			}
		});
		t.start();
		this.dataPort = this.serverSocket.getLocalPort();
		return this.serverSocket;
	}

	/**
	 * Sets the server in active mode by cleaning up what was setup by the
	 * setPassive method.
	 */
	public void setActiveMode() {
		this.serverSocket = null;
	}
}
