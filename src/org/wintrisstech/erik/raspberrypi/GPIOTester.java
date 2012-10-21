package org.wintrisstech.erik.raspberrypi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import be.doubleyouit.raspberry.gpio.Boardpin;
import be.doubleyouit.raspberry.gpio.Direction;
import be.doubleyouit.raspberry.gpio.GpioGateway;
import be.doubleyouit.raspberry.gpio.impl.GpioGatewayImpl;

public class GPIOTester {

	// Usage
	private final static String USAGE = "Usage: sudo java -jar RPiGPIOTester.jar";
	// Output pins
	private final static Boardpin[] OUT_PIN = //
	{ Boardpin.PIN11_GPIO17, Boardpin.PIN13_GPIO21, Boardpin.PIN15_GPIO22,
			Boardpin.PIN19_GPIO10, Boardpin.PIN21_GPIO9, Boardpin.PIN23_GPIO11 };
	// GPIO gateway
	private GpioGateway gpio;

	public static void main(String[] args) {
		if (args.length == 0) {
			try {
				new GPIOTester().runHeadless();
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println(USAGE);
		}
	}

	private void runHeadless() throws MalformedURLException {
		 gpio = getInitializedGPIO();
		ScheduleReader reader = new ScheduleReader();
		File schedules = new File("/home/pi/RPiGPIOTester/Schedules.xml");
		URL url = schedules.toURI().toURL();
		List<GpioAction> actions = reader.readFile(url);
		try {
			run(actions);
		} catch (InterruptedException e) {
			System.out.println("Run interrupted at "
					+ new Date(TestTime.currentTimeMillis()));
		} finally {
			// Set all pins to "off"
			for (int i = 0; i < OUT_PIN.length; i++) {
				 gpio.setValue(OUT_PIN[i], false);
			}
		}
	}

	private void run(List<GpioAction> actions) throws InterruptedException {
		for (GpioAction a : actions) {
			long sleepTime = (a.getTimeOfAction() - TestTime
					.currentTimeMillis()) / TestTime.TIME_FACTOR;
			if (sleepTime <= 0L) {
				runAction(a);
			} else {
				System.out.println("Going to sleep for " + sleepTime + " ms.");
				Thread.sleep(sleepTime);
				runAction(a);
			}
		}
	}

	private void runAction(GpioAction action) {
		int[] indices = action.getHeadsOff();
		for (int i = 0; i < indices.length; i++) {
			 gpio.setValue(OUT_PIN[indices[i]], false);
		}
		indices = action.getHeadsOn();
		for (int i = 0; i < indices.length; i++) {
			 gpio.setValue(OUT_PIN[indices[i]], true);
		}
		System.out.print(new Date(TestTime.currentTimeMillis()) + ": ");
		System.out.println(action);
	}

	private GpioGateway getInitializedGPIO() {
		GpioGateway gpio = new GpioGatewayImpl();
		for (int i = 0; i < OUT_PIN.length; i++) {
			gpio.setup(OUT_PIN[i], Direction.OUT);
		}
		return gpio;
	}
}
