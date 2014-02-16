package main.java.car.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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

public class FtpRequest {
	private Logger logger = Logger.getAnonymousLogger();
	private ClientSession clientSession;
	private DataOutputStream dos;

	private enum TYPE {
		A, A_N, I, L_8
	};

	private TYPE flag;

	@SuppressWarnings("unused")
	private FtpRequest() {
	}

	public FtpRequest(final ClientSession clientSession) {
		if (clientSession == null) {
			throw new IllegalArgumentException(
					"clientSession argument cannot be null!");
		}
		this.clientSession = clientSession;
		this.flag = TYPE.A;
	}

	public DataOutputStream getOutputStream() throws IOException {
		if (dos == null)
			this.dos = new DataOutputStream(this.clientSession
					.getCommandSocket().getOutputStream());
		return this.dos;
	}

	public TYPE getFlag() {
		return this.flag;
	}

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
			try {
				dos = getOutputStream();
				dos.writeBytes("202 Command not supported!\n");
				dos.flush();
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
				System.exit(2);
			}
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
		if (expectedPassword == null && !username.equalsIgnoreCase("anonymous")) {
			dos.writeBytes("530 The username and password didn't match!\n");
			processQUIT(null);
			return;
		}
		dos.writeBytes("230 Password accepted, please proceed!\n");
	}

	protected void processPASV(final String argument) throws IOException {
		// TODO: truly implement
		dos.writeBytes("202 Not implmented yet!\n");
	}

	protected void processPORT(final String argument) throws IOException {
		dos = getOutputStream();
		System.out.println("processPORT: " + argument);
		String arg = argument;
		String[] tokens = arg.split(",");
		String address = tokens[0] + "." + tokens[1] + "." + tokens[2] + "."
				+ tokens[3];
		clientSession.setDataAddress(address);
		int port = (Integer.parseInt(tokens[4]) * 256)
				+ Integer.parseInt(tokens[5]);
		clientSession.setDataPort(port);
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
		Socket dataSocket = new Socket(clientSession.getDataAddress(),
				clientSession.getDataPort());
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

		dos.writeBytes("226 The file was succesfully sent!\n");
	}

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
		dos.writeBytes("200 \n");
	}

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
		// TODO : see number of links
		String numberOfLinks = "1";

		line += permission + " " + numberOfLinks + " " + owner + " " + group
				+ " " + size + " " + modifiedTime + " " + file.getName()
				+ "\r\n";
		return line;
	}

	protected void processSTOR(final String file) throws IOException {
		File newFile = new File(clientSession.getCurrentDirectory()
				+ File.separator + file);
		dos.writeBytes("150 Ready to receive file " + file + " \n");

		Socket dataSocket = new Socket(clientSession.getDataAddress(),
				clientSession.getDataPort());
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
		dos.writeBytes("226 Received and executed STOR " + file + " \n");
	}

	protected void processSYST(final String argument) throws IOException {
		getOutputStream();
		dos.writeBytes("215 UNIX Type: L8\n");
	}

	protected void processTYPE(String typeString) throws IOException {
		typeString = typeString.trim();
		if (typeString.equalsIgnoreCase("A")) {
			flag = TYPE.A;
		} else if (typeString.equalsIgnoreCase("A N")) {
			flag = TYPE.A_N;
		} else if (typeString.equalsIgnoreCase("I")) {
			flag = TYPE.I;
		} else if (typeString.equalsIgnoreCase("L 8")) {
			flag = TYPE.L_8;
		} else {
			logger.log(Level.WARNING, "could not parse type : " + typeString);
		}
		dos.writeBytes("200 \n");
	}

	protected void processQUIT(final String argument) throws IOException {
		if (dos != null)
			dos.close();
		Socket commandSocket = clientSession.getCommandSocket();
		if (commandSocket != null && !commandSocket.isClosed())
			commandSocket.close();
	}

	private String writeOnDataSocket(final String data) throws IOException {
		Socket dataSocket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(
				clientSession.getDataAddress(), clientSession.getDataPort());
		dataSocket.connect(socketAddress);
		DataOutputStream cos = new DataOutputStream(
				dataSocket.getOutputStream());
		cos.writeBytes(data);
		cos.close();
		dataSocket.close();
		return data;
	}
}
