package org.wintrisstech.erik.raspberrypi;

public class TestTime {

	/*
	 * For test purposes this program operates with "test time", which runs
	 * TIME_FACTOR times faster than system time. Test time and system time are
	 * the same at time T_0, which is the time that this class is loaded.
	 */
	public static final long TIME_FACTOR = 1000L;
	public static final long T_0 = System.currentTimeMillis(); //

	public static long currentTimeMillis() {
		return (System.currentTimeMillis() - T_0) * TIME_FACTOR + T_0;
	}

}
