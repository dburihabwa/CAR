package main.java.car.ftp.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.Command;
import main.java.car.ftp.Command.AuhtorizedCommand;

public class CommandParser {
	private static Logger logger = Logger.getLogger("CommandParser");

	public static Command parse(String message) {
		String[] tokens = message.split("\\s+");
		AuhtorizedCommand command = null;
		String argument = null;
		for (AuhtorizedCommand c : AuhtorizedCommand.values()) {
			if (tokens[0].equalsIgnoreCase(c.name())) {
				command = c;
				argument = tokens[1];
				break;
			}
		}

		if (command == null) {
			throw new IllegalArgumentException(
					"The command was not recognized by the parser or authorizes by the server: " + tokens[0]);
		}

		Command interpretedCommand = new Command(command, argument);
		logger.log(Level.INFO, "Command interpreted : " + interpretedCommand);
		return interpretedCommand;
	}
}
