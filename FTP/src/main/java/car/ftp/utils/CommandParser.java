package main.java.car.ftp.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.Command;
import main.java.car.ftp.Command.AuhtorizedCommand;
import main.java.car.ftp.exceptions.UnsupportedCommandException;

/**
 * Parses commands sent to the server, checks if they are authorized.
 * 
 * @author dorian
 * 
 */
public class CommandParser {
	private static Logger logger = Logger.getAnonymousLogger();

	/**
	 * Parses a string and returns the associated command.
	 * 
	 * @param message
	 *            String to parse
	 * @return A command object with the AuthorizedCommand and its arguments
	 * @throws UnsupportedCommandException
	 */
	public static Command parse(String message)
			throws UnsupportedCommandException {
		if (message == null)
			throw new IllegalArgumentException(
					"argument message cannot be null!");
		String[] tokens = message.split("\\s+");
		AuhtorizedCommand command = null;
		String argument = null;
		for (AuhtorizedCommand c : AuhtorizedCommand.values()) {
			if (tokens[0].equalsIgnoreCase(c.name())) {
				command = c;
				argument = message.replaceFirst(tokens[0], "").trim();
				break;
			}
		}

		if (command == null) {
			throw new UnsupportedCommandException(
					"202 Unsupported command");
		}

		Command interpretedCommand = new Command(command, argument);
		logger.log(Level.INFO, "Command interpreted : " + interpretedCommand);
		return interpretedCommand;
	}
}
