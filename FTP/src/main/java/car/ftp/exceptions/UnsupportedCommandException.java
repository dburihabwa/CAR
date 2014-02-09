package main.java.car.ftp.exceptions;

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
