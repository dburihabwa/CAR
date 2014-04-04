package fr.lille1.car.rmi.interfaces;

import java.io.Serializable;

/**
 * Message sent between {@link SiteItf}s.
 * 
 * @author Dorian Burihabwa
 * 
 */
public interface Message extends Serializable {
	public long id = 1L;
	/**
	 * Returns the sender of the message.
	 * 
	 * @return the sender of the message
	 */
	SiteItf getSender();

	/**
	 * Returns the content of the message.
	 * 
	 * @return the content of the message
	 */
	String getContent();

	/**
	 * Returns the original time of the message.
	 * 
	 * @return the original time of the message
	 */
	long getTime();
}
