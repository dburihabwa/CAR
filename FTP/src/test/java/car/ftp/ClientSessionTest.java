package test.java.car.ftp;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import main.java.car.ftp.ClientSession;
import main.java.car.ftp.Server;

import org.junit.Before;
import org.junit.Test;

public class ClientSessionTest {
	@Before
	public void setup() {
		Server.getServer(1024, ".");
	}

	// TODO Test setPort
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDataPort() {
		int port = -1;
		Socket socket = null;
		ClientSession clientSession = new ClientSession(socket, false);
		clientSession.setDataPort(port);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setOutOfRangeDataPort() {
		int port = 65536;
		Socket socket = null;
		ClientSession clientSession = new ClientSession(socket, false);
		clientSession.setDataPort(port);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setNullCurrentDirectory() {
		File directory = null;
		Socket socket = null;
		ClientSession clientSession = new ClientSession(socket, false);
		clientSession.setCurrentDirectory(directory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setFileAsCurrentDirectory() {
		String fileName = "toBeDeleted";
		File directory = null;
		try {
			directory = File.createTempFile(fileName, "file");
			directory.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(directory);
			String content = "Some text";
			fos.write(content.getBytes());
			fos.close();
			if (directory.isDirectory()) {
				fail("The newly created file should not be a directory");
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
		Socket socket = null;
		ClientSession clientSession = new ClientSession(socket, false);
		clientSession.setCurrentDirectory(directory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetNullDataAddress() {
		String dataAddress = null;
		Socket socket = null;
		ClientSession clientSession = new ClientSession(socket, false);
		clientSession.setDataAddress(dataAddress);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetEmptyDataAddress() {
		String dataAddress = "";
		Socket socket = null;
		ClientSession clientSession = new ClientSession(socket, false);
		clientSession.setDataAddress(dataAddress);
	}
}
