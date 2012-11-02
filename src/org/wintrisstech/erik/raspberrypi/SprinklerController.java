package org.wintrisstech.erik.raspberrypi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SprinklerController {

	// Usage
	private final static String USAGE = "Usage: sudo java -jar RPiGPIOTester.jar";

	private static long SCHEDULE_CHECK_PERIOD = 20000000L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 0) {
			System.out.println(USAGE);
			return;
		}
		try {
			new SprinklerController().run();
		} catch (MalformedURLException e) {
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
	}

	public void run() throws MalformedURLException, InterruptedException,
			IOException {
		ScheduleReader reader = new ScheduleReader();
		URL url = new URL("http://localhost:8888/schedules/Schedules.xml");
		List<GpioAction> actions = reader.read(url);
		long lastRead = System.currentTimeMillis();
		ScheduleRunner runner = new ScheduleRunner(actions);
		runner.start();
		while (true) {
			Thread.sleep(SCHEDULE_CHECK_PERIOD / TestTime.TIME_FACTOR);
			if (isUrlModified(url, lastRead)) {
				stopRunner(runner);
				actions = reader.read(url);
				lastRead = System.currentTimeMillis();
				runner = new ScheduleRunner(actions);
				runner.start();
			}
		}

	}

	private void stopRunner(ScheduleRunner runner) throws InterruptedException {
		if (runner != null && runner.isAlive()) {
			runner.interrupt();
			runner.join();
		}

	}

	private boolean isUrlModified(URL url, long lastRead) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setIfModifiedSince(lastRead);
//		connection.connect();
		return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
	}

}
