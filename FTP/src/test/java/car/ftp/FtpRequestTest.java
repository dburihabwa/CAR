package test.java.car.ftp;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import main.java.car.ftp.Server;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FtpRequestTest {
	private static Server server;
	private static File ftpRoot;
	private static File clientRoot;
	private static int serverPort = 1024;
	private static Thread serverThread;

	@BeforeClass
	public static void setup() throws IOException {
		ftpRoot = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + "ftp");
		ftpRoot.deleteOnExit();
		ftpRoot.mkdirs();
		clientRoot = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + "client");
		clientRoot.deleteOnExit();
		clientRoot.mkdirs();

		server = server.getInstance();
		server.init(serverPort, ftpRoot.getAbsolutePath());
		server.loadUsers("./users.properties");
		System.out.println();
		serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					server.start();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}

	@BeforeClass
	public static void setupServer() {
		serverThread.start();
	}

	@AfterClass
	public static void destroy() {
		serverThread.stop();
	}

	@Test
	public void testProcessRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessCWD() throws UnknownHostException, IOException {
		String username = "admin";
		String password = "password";
		Socket socket = new Socket("localhost", serverPort);
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER " + username + "\n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASS " + password + "\n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PWD \n");
		dos.flush();
		response = br.readLine();
		Assert.assertTrue(response.startsWith("257")
				&& response.toLowerCase().contains(
						ftpRoot.getAbsolutePath().toLowerCase()));
		String innerPath = "inner";
		File inner = new File(ftpRoot.getAbsoluteFile() + File.separator
				+ innerPath);
		inner.mkdirs();
		dos.writeBytes("CWD " + innerPath + "\n");
		dos.flush();
		response = br.readLine();
		dos.writeBytes("PWD \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		Assert.assertTrue(response.startsWith("257")
				&& response.toLowerCase().contains(
						inner.getAbsolutePath().toLowerCase()));
		socket.close();
	}

	@Test
	public void testProcessUSER() throws UnknownHostException, IOException {
		String anonymous = "anonymous";
		Socket socket = new Socket("localhost", serverPort);
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER " + anonymous + " \n");
		dos.flush();
		response = br.readLine();
		System.out.println("***" + response + "****");
		Assert.assertTrue(response.startsWith("331"));
		socket.close();
	}

	@Test
	public void testProcessPASSAnonymous() throws UnknownHostException,
			IOException {
		String anonymous = "anonymous";
		Socket socket = new Socket("localhost", serverPort);
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER " + anonymous + " \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASS anonymous@domain.lille1 \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		Assert.assertTrue(response.startsWith("230"));
		socket.close();
	}

	@Test
	public void testProcessPASSUnauthorized() throws UnknownHostException,
			IOException {
		String anonymous = "unauthorized";
		Socket socket = new Socket("localhost", serverPort);
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER " + anonymous + " \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASS anonymous@domain.lille1 \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		Assert.assertTrue(response.startsWith("530"));
		socket.close();
	}

	@Test
	public void testProcessPASSAuthorized() throws UnknownHostException,
			IOException {
		String anonymous = "admin";
		Socket socket = new Socket("localhost", serverPort);
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER " + anonymous + " \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASS password \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		socket.close();
		Assert.assertTrue(response.startsWith("230"));
	}

	@Test
	public void testProcessPASV() throws UnknownHostException, IOException {
		String anonymous = "admin";
		Socket socket = new Socket("localhost", serverPort);
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER " + anonymous + " \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASS password \n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASV \n");
		dos.flush();
		response = br.readLine();
		socket.close();
		System.out.println("response : " + response);
		String portString = response.split("\\s")[1];
		String[] tokens = portString.split(",");
		String address = "127.0.0.1";
		int port = Integer.parseInt(tokens[4]);
		port += (Integer.parseInt(tokens[5]) * 256);
		System.out.println("address: " + address);
		System.out.println("port: " + port);
		SocketAddress dataAddress = new InetSocketAddress(address, port);
		Socket dataSocket = new Socket(address, port);
		int timeout = 5000;
		dataSocket.setSoTimeout(timeout);
		Calendar timer = new GregorianCalendar();
		long elapsed = new GregorianCalendar().getTimeInMillis()
				- timer.getTimeInMillis();
		System.out.println("elapsed: " + elapsed);

		socket.close();
		dataSocket.close();
		Assert.assertTrue(elapsed <= timeout);
	}

	@Test
	public void testProcessQUIT() throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", serverPort);
		String username = "admin";
		String password = "password";
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER " + username + "\n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASS " + password + "\n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		System.out.println("connection is connected : " + socket.isConnected());
		dos.writeBytes("QUIT \n");
		dos.flush();
		response = br.readLine();
		System.out.println("should be quit: " + response);
		System.out.println("connection is connected : " + socket.isConnected());
		Assert.assertTrue(response.startsWith("221"));
		socket.close();
	}

	@Test
	public void testProcessPORT() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessPWD() throws UnknownHostException, IOException {
		String anonymous = "admin";
		Socket socket = new Socket("localhost", serverPort);
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String response = br.readLine();
		dos.writeBytes("USER admin\n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PASS password\n");
		dos.flush();
		response = br.readLine();
		System.out.println("response : " + response);
		dos.writeBytes("PWD \n");
		dos.flush();
		response = br.readLine();
		socket.close();
		Assert.assertTrue(response.startsWith("257")
				&& response.toLowerCase().contains(
						ftpRoot.getAbsolutePath().toLowerCase()));
	}

	@Test
	public void testProcessRETR() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessLIST() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessSTOR() {
		fail("Not yet implemented");
	}
}
