package fr.lille1.car.rmi;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
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

		String name = (String) properties.get("site.name");
		final SiteItf site = new SiteImpl(name);

		startRegistry();
		registry.rebind(site.getName(), site);

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				try {
					if (site.getMessage() == null) {
						return;
					}
				} catch (AccessException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				} catch (RemoteException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}

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

				try {
					site.propagate();
				} catch (RemoteException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 5000);
		site.setMessage("Hello World");
	}

	static void startRegistry() {
		try {
			String host = (String) properties.get("registry.host");
			int port = Integer.parseInt((String) properties
					.get("registry.port"));
			logger.log(Level.INFO, "Will try to get registry located at "
					+ host + ":" + port);
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(1);
		}
	}

	static void buildTree(SiteItf site) {
		int children = (Integer) properties.get("site.children");

	}
}
