package org.wintrisstech.erik.raspberrypi;

import be.doubleyouit.raspberry.gpio.Boardpin;
import be.doubleyouit.raspberry.gpio.Direction;
import be.doubleyouit.raspberry.gpio.GpioGateway;
import be.doubleyouit.raspberry.gpio.impl.GpioGatewayImpl;

public class GPIOTester {

	private final static Boardpin[] OUT_PIN = {

	Boardpin.PIN11_GPIO17, Boardpin.PIN13_GPIO21, Boardpin.PIN15_GPIO22,

	Boardpin.PIN19_GPIO10, Boardpin.PIN21_GPIO9, Boardpin.PIN23_GPIO11

	};

	private final static String USAGE = "Usage: java -jar RPiGPIOTester.jar NumSecs, "
			+ "where NumSecs is the number of seconds to run the program.";

	public static void main(String[] args) {
		if (args.length == 1) {
			try {
				new GPIOTester().runHeadless(Integer.parseInt(args[0]));
			} catch (NumberFormatException ex) {
				System.out.println(USAGE);
			}
		} else {
			System.out.println(USAGE);
		}
	}

	private void runHeadless(int seconds) {
		long now = System.currentTimeMillis();
		long stop = now + 1000 * seconds;
		GpioGateway gpio = getInitializedGPIO();
		int numOutputs = OUT_PIN.length;
		// The sequence that the LEDs are turned on and off. A value of -1 means
		// "skip".
		Boardpin[] tune = { OUT_PIN[0], OUT_PIN[1], OUT_PIN[2],
				OUT_PIN[3], OUT_PIN[4], OUT_PIN[5], null, null, null,
				OUT_PIN[5], OUT_PIN[4], OUT_PIN[3], OUT_PIN[2],
				OUT_PIN[1], OUT_PIN[0], null, null, null };
		int len = tune.length;
		// Turn on the first three LEDs and off the remaining ones
		for (int i = 0; i < 3; i++) {
			gpio.setValue(OUT_PIN[i], true);
		}
		for (int i = 3; i < numOutputs; i++) {
			gpio.setValue(OUT_PIN[i], false);
		}
		int onIndex = 3;
		int offIndex = 0;
		// Now play the sequence repeatedly.
		while (System.currentTimeMillis() < stop) {
			Boardpin pin;
			pin = tune[onIndex];
			if (pin != null) {
				gpio.setValue(pin, true);
			}
			pin = tune[offIndex];
			if (pin != null) {
				gpio.setValue(pin, false);
			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
			}
			onIndex = (onIndex + 1) % len;
			offIndex = (offIndex + 1) % len;

		}
		// Turn all LEDs off.
		for (int i = 0; i < OUT_PIN.length; i++) {
			gpio.setValue(OUT_PIN[i], false);
		}
	}

	private GpioGateway getInitializedGPIO() {
		GpioGateway gpio = new GpioGatewayImpl();
		for (int i = 0; i < OUT_PIN.length; i++) {
			gpio.setup(OUT_PIN[i], Direction.OUT);
		}
		return gpio;
	}
}
