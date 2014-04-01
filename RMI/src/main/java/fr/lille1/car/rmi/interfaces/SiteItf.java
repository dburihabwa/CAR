package fr.lille1.car.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Representation of a site that reveices and propagates messages received from
 * other sites. The site has children to whom it transmits all the messages it
 * receives.
 * 
 * @author Dorian Burihabwa
 * 
 */
public interface SiteItf extends Remote {
	/**
	 * Returns the children of the Site.
	 * 
	 * @return The set of children of the site
	 * @throws RemoteException
	 *             If an error occurs when using a remote instance
	 */
	Set<SiteItf> getChildren() throws RemoteException;

	/**
	 * Tests if the site has received messages that it has not yet propagated to
	 * all its children.
	 * 
	 * @return Result of the test.
	 * @throws RemoteException
	 *             If an error occurs when using a remote instance
	 */
	boolean hasUnsentMessages() throws RemoteException;

	/**
	 * Adds a site to the list of {@link SiteItf} that will receive messages
	 * propagated by the site. If the site already is registered as a child of
	 * the current site, the method will return false.
	 * 
	 * @param child
	 *            Site to add to the list of children
	 * @return true if the addition has been made, false otherwise
	 * @throws RemoteException
	 *             If an error occurs when using a remote instance
	 */
	boolean addChild(final SiteItf child) throws RemoteException;

	/**
	 * Receives a {@link Message} and checks whether the message has already
	 * been received or propagated to its children. If it has not, the message
	 * is enqueued and propagated to the children.
	 * 
	 * @param message
	 *            {@link Message} sent
	 * @return true if the message has been added to the queue, false otherwise
	 * @throws RemoteException
	 *             If an error occurs when using a remote instance
	 */
	boolean receive(final Message message) throws RemoteException;

	/**
	 * Sends all {@link Message}s received and not yet transmitted to all its
	 * children.
	 * 
	 * @throws RemoteException
	 *             If an error occurs when using a remote instance
	 */
	void propagate() throws RemoteException;

	/**
	 * Returns the name of the site.
	 * 
	 * @return the name of the site
	 * @throws RemoteException
	 *             If an error occurs when using a remote instance
	 */
	String getName() throws RemoteException;
}
