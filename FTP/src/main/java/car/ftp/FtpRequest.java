package main.java.car.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.exceptions.UnsupportedCommandException;
import main.java.car.ftp.utils.CommandParser;

/**
 * Class handling all commands by dispatching to the right handlers and
 * providing small utility methods to communicate with the client.
 * 
 * @author dorian
 * 
 */
public class FtpRequest {
	private Logger logger = Logger.getAnonymousLogger();
	private ClientSession clientSession;
	private DataOutputStream dos;
	private boolean binaryFlag;

	@SuppressWarnings("unused")
	private FtpRequest() {
	}

	public FtpRequest(final ClientSession clientSession) {
		if (clientSession == null) {
			throw new IllegalArgumentException(
					"clientSession argument cannot be null!");
		}
		this.clientSession = clientSession;
	}

	/**
	 * Returns the output stream that allows to write responses to the client.
	 * 
	 * @return the output stream on the command socket
	 * @throws IOException
	 *             If an error occurs when trying to recover the output stream
	 *             on the command socket
	 */
	public DataOutputStream getOutputStream() throws IOException {
		if (dos == null)
			this.dos = new DataOutputStream(this.clientSession
					.getCommandSocket().getOutputStream());
		return this.dos;
	}

	/**
	 * Returns the data socket transparently regardless of the mode (active or
	 * passive).
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Socket getDataSocket() throws UnknownHostException, IOException {
		return clientSession.getDataSocket();
	}

	/**
	 * Returns the type last requested by the client.
	 * 
	 * @return last type requested by the client
	 */
	public boolean getBinaryFlag() {
		return this.binaryFlag;
	}

	/**
	 * Parses a command and dispatches the work to the matching method. In case
	 * of error, the method throws an {@link UnsupportedCommandException}. which
	 * should be caught by the object that has called the method.
	 * 
	 * @param commandString
	 *            The command as string
	 * @throws UnsupportedCommandException
	 */
	public void processRequest(final String commandString)
			throws UnsupportedCommandException {
		Command command = CommandParser.parse(commandString);
		Method handler = null;

		for (Method m : this.getClass().getDeclaredMethods()) {
			String method = m.getName();
			if (method.equalsIgnoreCase("process" + command.getCommand())) {
				handler = m;
				break;
			}
		}

		if (handler != null) {
			try {
				handler.invoke(this, command.getArgument());
				dos.flush();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				System.exit(1);
			}
		} else {
			String message = "502 Support for the command is not implemented!";
			logger.log(Level.WARNING, message);
			throw new UnsupportedCommandException(message);
		}

	}

	/**
	 * Changes the client's current directory to the parent directory.
	 * 
	 * @param argument
	 *            Useless argument to match process methods prototype
	 * @throws IOException
	 *             If an error occurs while trying to test the directory,
	 *             switching directory or sending a message to the client.
	 */
	protected void processCDUP(final String argument) throws IOException {
		processCWD("..");
	}

	/**
	 * Changes the current directory of the client.
	 * 
	 * @param directoryString
	 *            Relative or absolute path to the directory
	 * @throws IOException
	 *             If an error occurs while trying to test the directory,
	 *             switching directory or sending a message to the client.
	 */
	protected void processCWD(final String directoryString) throws IOException {
		if (directoryString == null) {
			throw new IllegalArgumentException(
					"argument directorySTring cannot be null!");
		}
		Path path = Paths.get(directoryString);
		if (!path.isAbsolute()) {
			path = Paths.get(clientSession.getCurrentDirectory().toString(),
					directoryString);
		}
		path = path.normalize();
		File directory = new File(path.toString());
		System.out.println("newDirectory :" + path.toString());
		if (!directory.exists()) {
			dos.writeBytes("550 No such file or directory: "
					+ directory.getAbsolutePath() + "\n");
			return;
		}
		if (!directory.isDirectory()) {
			dos.writeBytes("550 " + directoryString + " is not a directory\n");
			return;
		}
		Path rootPath = Server.getInstance().getRootDirectory().toPath();
		if (!path.startsWith(rootPath)) {
			dos.writeBytes("550 " + path
					+ " is not a sub directory of the server \n");
			return;
		}
		clientSession.setCurrentDirectory(directory);
		dos.writeBytes("250 Okay\n");
	}

	/**
	 * Records the user name in the client session.
	 * 
	 * @param username
	 *            The user name of the client
	 * @throws IOException
	 *             If an error occurs while writing on the command socket.
	 */
	protected void processUSER(final String username) throws IOException {
		dos = getOutputStream();
		clientSession.setUsername(username);
		dos.writeBytes("331 Username accepted, now expecting password!\n");
	}

	/**
	 * Verifies the username and the password of the client to let her connect
	 * to the server. In case of error the server returns a 530 error message
	 * and closes the connection.
	 * 
	 * @param password
	 *            Password of the client
	 * @throws IOException
	 *             If an error occurs while writing on the command socket.
	 */
	protected void processPASS(final String password) throws IOException {
		dos = getOutputStream();
		String username = clientSession.getUsername();
		String expectedPassword = Server.getInstance().getUsers()
				.getProperty(username);
		if ((expectedPassword == null || !expectedPassword.equals(password))
				&& !username.equalsIgnoreCase("anonymous")) {
			System.err.println(username + ":" + password);
			System.err.println("expectedPassword: " + expectedPassword);
			dos.writeBytes("530 The username and password didn't match!\n");
			processQUIT(null);
			return;
		}
		dos.writeBytes("230 Password accepted, please proceed!\n");
	}

	/**
	 * Sets the server in passive mode.
	 * 
	 * @param argument
	 *            unused.
	 * @throws IOException
	 *             If an error occurs while trying write on the command socket.
	 */
	protected void processPASV(final String argument) throws IOException {
		ServerSocket ss = clientSession.setPassiveMode();
		String address = "";
		for (byte b : ss.getInetAddress().getAddress()) {
			address += "" + b + ",";
		}
		int port = clientSession.getDataPort();
		int first = port / 256;
		int second = port % 256;

		String message = address + first + "," + second;

		logger.log(Level.INFO, "The server is now in passive mode!");
		dos.writeBytes("227 " + message + "\n");
	}

	/**
	 * Save the address and port to be used for a future command.
	 * 
	 * @param argument
	 *            concatenated address and port number
	 * @throws IOException
	 *             If an error occurs while trying write on the command socket
	 */
	protected void processPORT(final String argument) throws IOException {
		dos = getOutputStream();
		clientSession.setActiveMode();
		String arg = argument;
		String[] tokens = arg.split(",");
		String address = tokens[0] + "." + tokens[1] + "." + tokens[2] + "."
				+ tokens[3];
		clientSession.setDataAddress(address);
		int port = (Integer.parseInt(tokens[4]) * 256)
				+ Integer.parseInt(tokens[5]);
		clientSession.setDataPort(port);
		logger.log(Level.INFO, "The server is now in active mode!");
		dos.writeBytes("200 Address and port number has been saved! \n");
	}

	/**
	 * Returns the client current directory
	 * 
	 * @param argument
	 *            Not used
	 * @throws IOException
	 *             If an error occurs while trying to write on the socket.
	 */
	protected void processPWD(final String argument) throws IOException {
		File directory = clientSession.getCurrentDirectory();
		String path = directory.getPath();
		dos.writeBytes("257 " + path + "\n");
	}

	/**
	 * Returns a file from the FTP server.
	 * 
	 * @param file
	 *            Name of the file to retrieve
	 * @throws IOException
	 */
	protected void processRETR(final String file) throws IOException {
		File fileToRetrieve = new File(clientSession.getCurrentDirectory()
				+ File.separator + file.trim());
		if (!fileToRetrieve.exists()) {
			dos.writeBytes("451 The file cannot be found on the server!\n");
			return;
		}

		dos.writeBytes("150 Going to send " + file + "\n");
		Socket dataSocket = getDataSocket();
		if (dataSocket == null) {
			String message = "425 No data connection has been estblished with the client";
			dos.writeBytes(message);
			logger.log(Level.SEVERE, message);
			throw new IOException("");
		}
		FileInputStream fis = new FileInputStream(fileToRetrieve);
		DataOutputStream cos = new DataOutputStream(
				dataSocket.getOutputStream());
		byte[] buffer = new byte[1024];
		Calendar start = new GregorianCalendar();
		int read = 0;
		while ((read = fis.read(buffer)) > 0) {
			cos.write(buffer, 0, read);
		}
		Calendar end = new GregorianCalendar();
		cos.close();
		fis.close();
		dataSocket.close();

		double time = ((double) end.getTimeInMillis() - start.getTimeInMillis()) / 1000.0;
		logger.log(Level.INFO, "Sent " + file + " in " + time + "s");
		clientSession.setActiveMode();
		dos.writeBytes("226 The file was succesfully sent!\n");
	}

	/**
	 * Returns the list of files and directories in the client's current
	 * directory.
	 * 
	 * @param argument
	 *            unused
	 * @throws IOException
	 *             If an error occurs while writing on the data socket.
	 */
	protected void processLIST(final String argument) throws IOException {
		dos = getOutputStream();
		File directory = clientSession.getCurrentDirectory();
		String response = "";
		for (File file : directory.listFiles()) {
			String line = getListLine(file);
			response += line;
		}
		dos.writeBytes("150 About to read directory content!\n");
		writeOnDataSocket(response);

		clientSession.setActiveMode();
		dos.writeBytes("200 \n");
	}

	/**
	 * Builds the line to be sent to the client for a file upon a LIST request.
	 * 
	 * @param file
	 *            The file in the directory
	 * @return The line to be sent back to the client
	 * @throws IOException
	 *             If an error occurs while trying to read the file properties
	 */
	private String getListLine(File file) throws IOException {
		String line = "";
		PosixFileAttributes posixView = Files.readAttributes(file.toPath(),
				PosixFileAttributes.class);
		String owner = posixView.owner().getName();
		String group = posixView.group().getName();
		Set<PosixFilePermission> permissions = posixView.permissions();
		String ownerPermissions = "", groupPermissions = "", othersPermissions = "";
		String permission = "";
		if (file.isDirectory())
			permission += "d";
		else
			permission += "-";

		if (permissions.contains(PosixFilePermission.OWNER_READ))
			ownerPermissions += "r";
		else
			ownerPermissions += "-";
		if (permissions.contains(PosixFilePermission.OWNER_WRITE))
			ownerPermissions += "w";
		else
			ownerPermissions += "-";
		if (permissions.contains(PosixFilePermission.OWNER_EXECUTE))
			ownerPermissions += "x";
		else
			ownerPermissions += "-";

		if (permissions.contains(PosixFilePermission.GROUP_READ))
			groupPermissions += "r";
		else
			groupPermissions += "-";
		if (permissions.contains(PosixFilePermission.GROUP_WRITE))
			groupPermissions += "w";
		else
			groupPermissions += "-";
		if (permissions.contains(PosixFilePermission.GROUP_EXECUTE))
			groupPermissions += "x";
		else
			groupPermissions += "-";

		if (permissions.contains(PosixFilePermission.OTHERS_READ))
			othersPermissions += "r";
		else
			othersPermissions += "-";
		if (permissions.contains(PosixFilePermission.OTHERS_WRITE))
			othersPermissions += "w";
		else
			othersPermissions += "-";
		if (permissions.contains(PosixFilePermission.OTHERS_EXECUTE))
			othersPermissions += "x";
		else
			othersPermissions += "-";

		permission += ownerPermissions + groupPermissions + othersPermissions;

		String size = String.format("%10s", "" + posixView.size());
		long time = posixView.lastModifiedTime().toMillis();
		Date date = new Date(time);
		Date sixMonthsAgo = new Date();
		long sixMonthsPeriod = 15768000000L;
		sixMonthsAgo.setTime(sixMonthsAgo.getTime() - sixMonthsPeriod);
		SimpleDateFormat sdf = null;
		if (date.before(sixMonthsAgo)) {
			sdf = new SimpleDateFormat("MMM dd  YYYY", Locale.ENGLISH);
		} else {
			sdf = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH);
		}
		String modifiedTime = sdf.format(date);
		String numberOfLinks = "1";

		line += permission + " " + numberOfLinks + " " + owner + " " + group
				+ " " + size + " " + modifiedTime + " " + file.getName()
				+ "\r\n";
		return line;
	}

	/**
	 * Stores a file on the server
	 * 
	 * @param file
	 *            The file to store on the server
	 * @throws IOException
	 *             If an error occurs while trying to read form the data socket
	 *             or creating the file.
	 */
	protected void processSTOR(final String file) throws IOException {
		File newFile = new File(clientSession.getCurrentDirectory()
				+ File.separator + file);
		dos.writeBytes("150 Ready to receive file " + file + " \n");

		Socket dataSocket = getDataSocket();
		byte[] buffer = new byte[1024];
		Calendar start = new GregorianCalendar();
		DataInputStream cis = new DataInputStream(dataSocket.getInputStream());
		FileOutputStream fos = new FileOutputStream(newFile);
		int read = 0;
		while ((read = cis.read(buffer)) > 0) {
			fos.write(buffer, 0, read);
		}
		Calendar end = new GregorianCalendar();
		fos.close();
		cis.close();
		dataSocket.close();
		double time = ((double) end.getTimeInMillis() - start.getTimeInMillis()) / 1000.0;
		logger.log(Level.INFO, "Transfered " + file + " in " + time + "s");
		clientSession.setActiveMode();
		dos.writeBytes("226 Received and executed STOR " + file + " \n");
	}

	/**
	 * Return bogus information about the server
	 * 
	 * @param argument
	 *            unused
	 * @throws IOException
	 *             If an error occurs while writing on the data socket
	 */
	protected void processSYST(final String argument) throws IOException {
		getOutputStream();
		dos.writeBytes("215 UNIX Type: L8\n");
	}

	/**
	 * Sets the binary flag on or off on client's request.
	 * 
	 * @param typeString
	 *            type argument describing whether the binary flag must be on or
	 *            off
	 * @throws IOException
	 *             If an error occurs while writing on the command socket.
	 */
	protected void processTYPE(String typeString) throws IOException {
		typeString = typeString.trim();
		String message = "set the binary flag ";
		if (typeString.equalsIgnoreCase("A")
				|| typeString.equalsIgnoreCase("A N")) {
			binaryFlag = false;
			message += "on";
		} else if (typeString.equalsIgnoreCase("I")
				|| typeString.equalsIgnoreCase("L 8")) {
			binaryFlag = true;
			message += "off";
		} else {
			message = "could not parse type : " + typeString;
			logger.log(Level.WARNING, message);
		}
		dos.writeBytes("200 " + message + "\n");
	}

	/**
	 * Disconnect the client.
	 * 
	 * @param argument
	 *            unused
	 * @throws IOException
	 *             If an error occurs while trying to close the connection or
	 *             the notifiy the client that it has been disconnected.
	 */
	protected void processQUIT(final String argument) throws IOException {
		dos = getOutputStream();
		dos.writeBytes("221 Bye \n");
		if (dos != null)
			dos.close();
		Socket commandSocket = clientSession.getCommandSocket();
		if (commandSocket != null)
			commandSocket.close();
	}

	/**
	 * Write text on the data socket.
	 * 
	 * @param data
	 *            Text to write
	 * @return The data writtent on the socket
	 * @throws IOException
	 *             If an error occurs while trying to write on the data socket.
	 */
	private String writeOnDataSocket(final String data) throws IOException {
		Socket dataSocket = getDataSocket();
		DataOutputStream cos = new DataOutputStream(
				dataSocket.getOutputStream());
		cos.writeBytes(data);
		cos.close();
		dataSocket.close();
		return data;
	}
}
