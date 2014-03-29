package fr.lille1.car.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Message sent between {@link SiteItf}s.
 * 
 * @author Dorian Burihabwa
 * 
 */
public interface Message extends Remote {
	/**
	 * Returns the sender of the message.
	 * 
	 * @return the sender of the message
	 */
	SiteItf getSender() throws RemoteException;

	/**
	 * Returns the content of the message.
	 * 
	 * @return the content of the message
	 */
	String getContent() throws RemoteException;

	/**
	 * Returns the original time of the message.
	 * 
	 * @return the original time of the message
	 */
	long getTime() throws RemoteException;
}
