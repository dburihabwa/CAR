package main.java.car.ftp;

/**
 * Reprensentation of command as sent by the client to the server. The command
 * is split between two components a VERB and an argument
 * 
 * @author dorian
 * 
 */
public class Command {
	/** List of commands that server presents as implemented to the clients */
	public static enum AuhtorizedCommand {
		CDUP, CWD, FEAT, LIST, PASS, PASV, PORT, PWD, QUIT, RETR, STOR, SYST, TYPE, USER
	};

	private AuhtorizedCommand command;
	private String argument;

	public Command(AuhtorizedCommand command, String argument) {
		this.command = command;
		this.argument = argument;
	}

	public AuhtorizedCommand getCommand() {
		return command;
	}

	public String getArgument() {
		return argument;
	}

	@Override
	public String toString() {
		String str = command.name();
		if (argument != null)
			str += "\t" + argument;
		return str;
	}
}
