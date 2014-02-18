package main.java.car.ftp.exceptions;

import main.java.car.ftp.utils.CommandParser;

/**
 * Exception expressing the impossibility for the {@link CommandParser} to parse
 * a message.
 * 
 * @author dorian
 * 
 */
public class UnsupportedCommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037452213897092520L;

	public UnsupportedCommandException() {
		super();
	}

	public UnsupportedCommandException(final String message) {
		super(message);
	}
}
