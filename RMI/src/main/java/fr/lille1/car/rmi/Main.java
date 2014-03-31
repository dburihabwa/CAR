package fr.lille1.car.rmi;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.lille1.car.rmi.impl.SiteImpl;
import fr.lille1.car.rmi.interfaces.SiteItf;
import fr.lille1.car.rmi.utils.Prompt;

public class Main {
	public static final String USAGE = "Usage: java -jar <jar> conf.properties";
	private static Logger logger = Logger.getLogger(Main.class.getName());
	private static Properties properties = new Properties();
	private static Registry registry;

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}
		String filename = args[0];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			properties.load(fis);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(1);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}

		logger.log(Level.INFO, "Successfully loaded " + filename);

		String name = properties.getProperty("site.name");
		final SiteItf site = new SiteImpl(name);

		startRegistry();
		registry.rebind(site.getName(), site);

		String interactive = properties.getProperty("site.interactive");
		if (interactive != null && interactive.equalsIgnoreCase("true")) {
			logger.log(Level.INFO, "Starting interactive mode!");
			System.out.print(">\t");
			Prompt prompt = new Prompt(site);
			prompt.setSender(site);
			while (prompt.nextLine() != null) {
				String[] children = ((String) properties
						.getProperty("site.children")).split(",");
				for (String child : children) {
					try {
						SiteItf s = (SiteItf) registry.lookup(child);
						site.addChild(s);
					} catch (NotBoundException e) {
						logger.log(Level.WARNING, "Could not find child "
								+ child);
					} catch (AccessException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					} catch (RemoteException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				site.propagate();
			}
		} else {
			logger.log(Level.INFO, "Starting passive mode!");
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					try {
						if (!site.hasUnsentMessages()) {
							return;
						}
					} catch (AccessException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					} catch (RemoteException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
					
					if (properties.getProperty("site.children") == null) {
						return;
					}

					String[] children = (properties
							.getProperty("site.children")).split(",");
					for (String child : children) {
						try {
							SiteItf s = (SiteItf) registry.lookup(child);
							site.addChild(s);
						} catch (NotBoundException e) {
							logger.log(Level.WARNING, "Could not find child "
									+ child);
						} catch (AccessException e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
						} catch (RemoteException e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
						}
					}

					try {
						site.propagate();
					} catch (RemoteException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			};
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(task, 0, 5000);
		}
	}

	static void startRegistry() {
		try {
			String host = properties.getProperty("registry.host");
			int port = Integer
					.parseInt(properties.getProperty("registry.port"));
			logger.log(Level.INFO, "Will try to get registry located at "
					+ host + ":" + port);
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(1);
		}
	}
}
