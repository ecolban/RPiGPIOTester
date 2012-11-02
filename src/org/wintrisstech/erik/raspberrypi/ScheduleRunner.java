package org.wintrisstech.erik.raspberrypi;

import java.util.Date;
import java.util.List;

import be.doubleyouit.raspberry.gpio.Boardpin;
import be.doubleyouit.raspberry.gpio.Direction;
import be.doubleyouit.raspberry.gpio.GpioGateway;
import be.doubleyouit.raspberry.gpio.impl.GpioGatewayImpl;

public class ScheduleRunner extends Thread {

	private List<GpioAction> actions;

	// Output pins
	private final static Boardpin[] OUT_PIN = //
	{ Boardpin.PIN11_GPIO17, Boardpin.PIN13_GPIO21, Boardpin.PIN15_GPIO22,
			Boardpin.PIN19_GPIO10, Boardpin.PIN21_GPIO9, Boardpin.PIN23_GPIO11 };

	// GPIO gateway
	private static GpioGateway GPIO_GW = getGpioGateway();

	public ScheduleRunner(List<GpioAction> actions) {
		this.actions = actions;
	}

	/**
	 * Executes a list of GpioAction's.
	 * 
	 * @param actions
	 *            a list of GpioAction's
	 */
	public void run() {
		try {
			while (true) {
				for (GpioAction action : actions) {
					long sleepTime = action.getTimeOfAction()
							- TestTime.currentTimeMillis();
					if (0L < sleepTime) {
						System.out.println("Going to sleep for " + sleepTime
								+ " ms.");
						Thread.sleep(sleepTime / TestTime.TIME_FACTOR);
					}
					int[] head = action.getHeadsOff();
					for (int i = 0; i < head.length; i++) {
						GPIO_GW.setValue(OUT_PIN[head[i]], false);
					}
					head = action.getHeadsOn();
					for (int i = 0; i < head.length; i++) {
						GPIO_GW.setValue(OUT_PIN[head[i]], true);
					}
					System.out.print(new Date(TestTime.currentTimeMillis())
							+ ": ");
					System.out.println(action);
					action.addWeek();
				}
			}
		} catch (InterruptedException ex) {
			System.out.println("Interrupted on: "
					+ new Date(TestTime.currentTimeMillis()));

		} finally {
			// Set all pins to "off"
			for (int i = 0; i < OUT_PIN.length; i++) {
				GPIO_GW.setValue(OUT_PIN[i], false);
			}
			System.out.println("Turning all heads off.");
		}
	}

	/**
	 * Sets up the output pins.
	 * 
	 * @return a GpioGateway instance
	 */
	private static GpioGateway getGpioGateway() {
		GpioGateway gw = new GpioGatewayImpl();
		for (int i = 0; i < OUT_PIN.length; i++) {
			gw.setup(OUT_PIN[i], Direction.OUT);
		}
		return gw;
	}

}
