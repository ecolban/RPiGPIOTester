package org.wintrisstech.erik.raspberrypi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.DocumentException;

/**
 * A SprinklerController instance regularly checks to see if there are updates
 * of the sprinkler schedule and runs the sprinkler according to the most recent
 * update. It requires network connectivity to run.
 * 
 * @author ecolban
 * 
 */
public class SprinklerController {

	// Usage
	private final static String USAGE = "Usage: sudo java -jar RPiGPIOTester.jar";

	private static long SCHEDULE_CHECK_PERIOD = 20000000L; // = 5 hours, 33
															// minutes and 20
															// seconds

	private static final Logger logger = Logger
			.getLogger(SprinklerController.class.getName());

	/**
	 * 
	 * @param args
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws MalformedURLException,
			InterruptedException {
		if (args.length != 0) {
			System.out.println(USAGE);
			return;
		}
		new SprinklerController().run();
	}

	/**
	 * Runs the sprinkler forever according to the most recently read schedule.
	 * Regularly checks if there is a more recent update online, and if that is
	 * the case, tries to retrieve and read it.
	 * 
	 * @throws MalformedURLException
	 *             if the URL for the schedule is malformed
	 * @throws InterruptedException
	 *             if interrupted
	 */
	public void run() throws MalformedURLException, InterruptedException {
		ScheduleReader reader = new ScheduleReader();
		URL url = getURL();
		System.out.println(url);
		int lastRead = -1;
		ScheduleRunner runner = null;
		while (true) {
			try {
				reader.read(url);
				if (reader.getVersion() > lastRead) {
					if (runner != null) {
						runner.exitGracefully();
					}
					List<GpioAction> actions = reader.getActionList();
					lastRead = reader.getVersion();
					runner = new ScheduleRunner(actions);
					runner.start();
				}
			} catch (DocumentException e) {
				logger.log(Level.WARNING, e.getMessage());
				// Try again later
			}
			Thread.sleep(SCHEDULE_CHECK_PERIOD / TestTime.TIME_FACTOR);
		}

	}

	private URL getURL() throws MalformedURLException {
		IdKeeper idKeeper = new IdKeeper();
		try {
			File f = new File("/home/pi/schedules/s" + idKeeper.getId() + ".xml");
			return f.toURI().toURL();
//			return new URL("http://sprinklerwiz.appspot.com/schedules/s"
//					+ idKeeper.getId() + ".xml");
		} catch (IOException e) {
			return null;
		}
	}

}
