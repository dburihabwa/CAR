package main.java.car.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.exceptions.UnsupportedCommandException;
import main.java.car.ftp.utils.CommandParser;

public class FtpRequest {
	private Logger logger = Logger.getAnonymousLogger();
	private Socket socket;
	private Socket clientSocket;
	private String address;
	private int port = 0;
	private DataOutputStream dos;

	private enum TYPE {
		A, A_N, I, L_8
	};

	private TYPE flag;

	@SuppressWarnings("unused")
	private FtpRequest() {
	}

	public FtpRequest(final Socket socket) {
		if (socket == null)
			throw new NullPointerException("socket is null");
		this.socket = socket;
		this.flag = TYPE.A;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public DataOutputStream getOutputStream() throws IOException {
		if (dos == null)
			this.dos = new DataOutputStream(this.socket.getOutputStream());
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
				System.out.println("Matching command found!");
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

	protected void processUSER(final String username) throws IOException {
		dos = getOutputStream();
		dos.writeBytes("331 Username accepted, now expecting password!\n");
	}

	protected void processPASS(final String password) throws IOException {
		dos = getOutputStream();
		dos.writeBytes("230 Password accepted, please proceed!\n");
	}

	protected void processPORT(final String argument) throws IOException {
		dos = getOutputStream();
		String[] tokens = argument.split(",");
		address = tokens[0] + "." + tokens[1] + "." + tokens[2] + "."
				+ tokens[3];
		port = (Integer.parseInt(tokens[4]) * 256)
				+ Integer.parseInt(tokens[5]);
		System.out.println("Tried to open " + address + ":" + port);
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
		File directory = Main.directory;
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
		File fileToRetrieve = new File(Main.directory + File.separator
				+ file.trim());
		if (!fileToRetrieve.exists())
			dos.writeBytes("400 The file cannot be found on the server!\n");

		dos.writeBytes("150 Going to send " + file + "\n");
		clientSocket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(address, port);
		clientSocket.connect(socketAddress);
		DataInputStream dis = new DataInputStream(new FileInputStream(
				fileToRetrieve));
		DataOutputStream cos = new DataOutputStream(
				clientSocket.getOutputStream());
		byte[] buffer = new byte[1024];
		while (dis.available() > 0) {
			int leftToSend = dis.available();
			if (leftToSend < buffer.length) {
				buffer = new byte[leftToSend];
			}
			dis.read(buffer);
			cos.write(buffer);
		}
		cos.close();
		dis.close();
		clientSocket.close();

		dos.writeBytes("226 The file was succesfully sent!\n");
	}

	protected void processLIST(final String argument) throws IOException {
		dos = getOutputStream();
		File directory = Main.directory;
		String response = "";
		for (File file : directory.listFiles()) {
			String line = getListLine(file);
			response += line;
		}
		dos.writeBytes("150 About to read directory content!\n");
		writeOnDataSocket(response);
		dos.writeBytes("200 \n");
		System.out.println("End of processPORT\n");
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
				+ " " + size + " " + modifiedTime + " " + file.getName() + "\n";
		return line;
	}

	protected void processSTOR(final String file) {
		throw new UnsupportedOperationException("Not implemented yet!");
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
		dos.writeBytes("200\n");
	}

	protected void processQUIT(final String argument) throws IOException {
		if (dos != null)
			dos.close();
		if (socket != null && !socket.isClosed())
			socket.close();
	}

	private String writeOnDataSocket(final String data) throws IOException {
		clientSocket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(address, port);
		clientSocket.connect(socketAddress);
		DataOutputStream cos = new DataOutputStream(
				clientSocket.getOutputStream());
		cos.writeBytes(data);
		cos.close();
		clientSocket.close();
		return data;
	}
}
