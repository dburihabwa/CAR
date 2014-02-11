package test.java.car.ftp.utils;

import static org.junit.Assert.fail;
import main.java.car.ftp.Command;
import main.java.car.ftp.Command.AuhtorizedCommand;
import main.java.car.ftp.exceptions.UnsupportedCommandException;
import main.java.car.ftp.utils.CommandParser;

import org.junit.Assert;
import org.junit.Test;

public class CommandParserTest {

	@Test(expected = IllegalArgumentException.class)
	public void parseNull() {
		try {
			CommandParser.parse(null);
		} catch (UnsupportedCommandException e) {
			fail("This should not happen");
		}
	}

	@Test
	public void parseSingleWordCommand() {
		AuhtorizedCommand command = AuhtorizedCommand.LIST;
		String message = command.name();
		try {
			CommandParser.parse(message);
		} catch (UnsupportedCommandException e) {
			fail("This should not happen!");
		}
	}

	@Test(expected = UnsupportedCommandException.class)
	public void parseUnauthorizedCommand() throws UnsupportedCommandException {
		String command = "NON_EXISTANT_COMMAND";
		String argument = "bogus";
		String message = command + " " + argument;

		CommandParser.parse(message);
		fail("An exception should have been thrown before reaching this line!");
	}

	@Test
	public void parseAllAuthorizedCommands() {
		String message;
		for (AuhtorizedCommand c : AuhtorizedCommand.values()) {
			message = c.name();
			try {
				Command command = CommandParser.parse(message);
				if (command == null) {
					fail("command == null for AuthorizedCommand " + c.name());
				}
				if (command.getCommand() == null) {
					fail("command.getCommand() == null for AuthorizedCommand "
							+ c.name());
				}
				if (command.getArgument() == null) {
					fail("command.getArgument() == null for AuthorizedCommand "
							+ c.name());
				}
			} catch (UnsupportedCommandException e) {
				fail("This should not happen");
			}
		}
		Assert.assertTrue(true);
	}
}
