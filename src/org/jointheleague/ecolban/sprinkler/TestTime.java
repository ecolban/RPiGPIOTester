package org.jointheleague.ecolban.sprinkler;

public class TestTime {

	/*
	 * For test purposes this program operates with "test time", which runs
	 * TIME_FACTOR times faster than system time. Test time and system time are
	 * the same at time T_0, which is the time that this class is loaded.
	 */

	/**
	 * The factor that determines how much faster test time runs than system
	 * time.
	 */
	public static final long TIME_FACTOR = 5000L;
	
	/**
	 * The time instance when system time and test time are the same.
	 */
	public static final long T_0 = System.currentTimeMillis(); //

	/**
	 * Get the current time in milliseconds according to test time. 
	 * @return
	 */
	public static long currentTimeMillis() {
		return (System.currentTimeMillis() - T_0) * TIME_FACTOR + T_0;
	}

}
