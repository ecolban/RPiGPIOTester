package org.wintrisstech.erik.raspberrypi;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.doubleyouit.raspberry.gpio.Boardpin;
import be.doubleyouit.raspberry.gpio.Direction;
import be.doubleyouit.raspberry.gpio.GpioGateway;
import be.doubleyouit.raspberry.gpio.impl.GpioGatewayImpl;

/**
 * A ScheduleRunner instance executes repeatedly the list of actions that is
 * passed to it in its constructor.
 * 
 * @author ecolban
 * 
 */
public class ScheduleRunner extends Thread {

	private List<GpioAction> actions;

	// Output pins
	private final static Boardpin[] OUT_PIN = //
	{ Boardpin.PIN11_GPIO17, Boardpin.PIN13_GPIO21, Boardpin.PIN15_GPIO22,
			Boardpin.PIN19_GPIO10, Boardpin.PIN21_GPIO9, Boardpin.PIN23_GPIO11 };

	private static final Logger logger = Logger
			.getLogger(SprinklerController.class.getName());

	// GPIO gateway
	 private static GpioGateway GPIO_GW = getGpioGateway();

	public ScheduleRunner(List<GpioAction> actions) {
		this.actions = actions;
	}

	/**
	 * Executes a list of GpioAction's repeatedly. Exits gracefully if
	 * interrupted. The list of actions must be sorted by time and the first
	 * must have a time later than now. The list must not contain more than one
	 * week's worth of actions. After executing an action, the time of the
	 * action is incremented by one week so it can be executed again a week
	 * later.
	 * 
	 * @param actions
	 *            a list of GpioAction's
	 */
	@Override
	public void run() {
		try {
			while (true) {
				for (GpioAction action : actions) {
					long sleepTime = action.getTimeOfAction()
							- TestTime.currentTimeMillis();
					if (0L < sleepTime) {
//						logger.log(Level.INFO, "Going to sleep for {0}ms",
//								sleepTime);
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
//					logger.log(Level.INFO, "{0}: {1}", new Object[] {
//							new Date(TestTime.currentTimeMillis()), action });
					action.addWeek();
				}
			}
		} catch (InterruptedException ex) {
			logger.log(Level.INFO, "Interrupted on: {0}",
					new Date(TestTime.currentTimeMillis()));

		} finally {
			// Set all pins to "off"
			for (int i = 0; i < OUT_PIN.length; i++) {
				 GPIO_GW.setValue(OUT_PIN[i], false);
			}
			logger.info("Turning all heads off.");
		}
	}

	/**
	 * Called to exit gracefully. This method blocks until this thread is dead.
	 * 
	 * @throws InterruptedException
	 *             if interrupted while waiting for this runner to die.
	 */
	public void exitGracefully() throws InterruptedException {
		if (isAlive()) {
			interrupt();
			join();
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
