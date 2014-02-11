package main.java.car.ftp;

public class Command {
	public static enum AuhtorizedCommand {
		CWD, FEAT, LIST, PASS, PASV, PORT, PWD, QUIT, RETR, STOR, SYST, TYPE, USER
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
