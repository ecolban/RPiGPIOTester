package org.wintrisstech.erik.raspberrypi;

import java.io.IOException;
import java.net.HttpURLConnection;
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

	private static final Logger logger = Logger.getLogger(SprinklerController.class.getName());
	
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
		URL url = new URL("http://localhost:8888/schedules/Schedules.xml");
		long lastRead = 0L;
		ScheduleRunner runner = null;
		while (true) {
			try {
				if (isModified(url, lastRead)) {
					stopRunner(runner);
					List<GpioAction> actions = reader.read(url);
					lastRead = System.currentTimeMillis();
					runner = new ScheduleRunner(actions);
					runner.start();
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage());
				// Try again later
			} catch (DocumentException e) {
				logger.log(Level.WARNING, e.getMessage());
				// Try again later
			}
			Thread.sleep(SCHEDULE_CHECK_PERIOD / TestTime.TIME_FACTOR);
		}

	}

	/**
	 * Stops a ScheduleRunner instance gracefully.
	 * 
	 * @param runner
	 *            the instance of the ScheduleRunner
	 * 
	 * @throws InterruptedException
	 *             if interrupted while waiting for the runner to die.
	 */
	private void stopRunner(ScheduleRunner runner) throws InterruptedException {
		if (runner != null && runner.isAlive()) {
			runner.interrupt();
			runner.join();
		}

	}

	/**
	 * Checks if a resource at a given URL accessible through HTTP has been
	 * modified since the time it was last retrieved.
	 * 
	 * @param url
	 *            the URL to check
	 * @param lastRead
	 *            the system time (in ms) when the URL was last retrieved
	 * @return true if the URL has been modified
	 * @throws IOException
	 *             if a HTTP connection to the URL cannot be established
	 */
	private boolean isModified(URL url, long lastRead) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setIfModifiedSince(lastRead);
		return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
	}

}
