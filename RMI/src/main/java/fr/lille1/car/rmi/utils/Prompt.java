package fr.lille1.car.rmi.utils;

import java.rmi.RemoteException;
import java.util.Scanner;

import fr.lille1.car.rmi.impl.MessageImpl;
import fr.lille1.car.rmi.interfaces.SiteItf;

/**
 * An object that sends messages to a site using standard input.
 * 
 * @author Dorian Burihabwa
 * 
 */
public class Prompt {
	private Scanner scanner;
	private SiteItf sender;
	private SiteItf destination;

	/**
	 * Constuctor
	 * 
	 * @param dest
	 *            {@link SiteItf} the messages will be sent to.
	 */
	public Prompt(final SiteItf dest) {
		if (dest == null) {
			throw new IllegalArgumentException("dest argument cannot be null!");
		}
		this.destination = dest;
		scanner = new Scanner(System.in);
	}

	/**
	 * Sets a site as the sender of messages.
	 * 
	 * @param sender
	 *            Sender
	 */
	public void setSender(final SiteItf sender) {
		this.sender = sender;
	}

	/**
	 * Reads a message on standard input and sends it to the destination
	 * {@link SiteItf}.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String nextLine() throws RemoteException {
		String content = scanner.nextLine();
		if (content == null) {
			return null;
		}
		destination.receive(new MessageImpl(this.sender, content));
		return content;
	}

}
