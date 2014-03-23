package main.java.car.ftp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.ClientSession;
import main.java.car.ftp.FtpRequest;
import main.java.car.ftp.exceptions.UnsupportedCommandException;

/**
 * Servers as a command receiver for a client. All messages sent by client will
 * be first read by a {@link FtpWorker} that will then call on a
 * {@link FtpRequest} to handle the request.
 * 
 * @author dorian
 * 
 */
public class FtpWorker implements Runnable {
	private Socket socket;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public FtpWorker(Socket socket) {
		if (socket == null)
			throw new IllegalArgumentException("socket argument cannot be null");
		this.socket = socket;
	}

	/**
	 * Reads the input from the user in a loop and then lets an
	 * {@link FtpRequest} handle the request.
	 */
	public void run() {
		BufferedInputStream bis = null;
		DataOutputStream dos = null;
		try {
			bis = new BufferedInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeBytes("200 Welcome!\r\n");
			ClientSession clientSession = new ClientSession(socket, true);
			FtpRequest ftpRequest = new FtpRequest(clientSession);
			String command = null;
			while (!socket.isClosed()) {
				System.out.println("Entering LOOP");
				InputStreamReader isr = new InputStreamReader(
						socket.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				do {
					command = br.readLine();
				} while (command == null);
				logger.log(Level.INFO, "Command : " + command);
				try {
					ftpRequest.processRequest(command);
				} catch (UnsupportedCommandException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
					dos.writeBytes(e.getMessage() + "\r\n");
				}
				logger.log(Level.INFO, "Waiting for new command");
				command = null;
			}
			logger.log(Level.INFO, "The client closed the connection!");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			try {
				dos.writeBytes("451 An error occured while processing the client's request!\r\n");
			} catch (IOException e1) {
				logger.log(Level.SEVERE, "Cannot recover from error");
				System.exit(1);
			}
		} finally {

			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					System.exit(1);
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					System.exit(1);
				}
			}
		}

	}

}
