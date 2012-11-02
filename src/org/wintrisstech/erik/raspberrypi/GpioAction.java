package org.wintrisstech.erik.raspberrypi;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class GpioAction implements Comparable<GpioAction>{

	private final int[] headsOn;
	private final int[] headsOff;
	private final Calendar time;
	/**
	 * Constructor
	 * @param headsOn an int array of the indices of the heads to turn on
	 * @param headsOff an int array of the indices of the heads to turn off
	 * @param time the time of the action
	 */
	public GpioAction(int[] headsOn, int[] headsOff, Calendar time) {
		this.headsOn = headsOn;
		this.headsOff = headsOff;
		this.time = time;
	}

	@Override
	public int compareTo(GpioAction o) {
		return time.compareTo(o.time);
	}

	/**
	 * Get the array of indices of the heads to turn on.
	 * @return the array of heads to turn on.
	 */
	public int[] getHeadsOn() {
		return headsOn;
	}
	/**
	 * Get the array of indices of the heads to turn off.
	 * @return the array of heads to turn off.
	 */
	public int[] getHeadsOff() {
		return headsOff;
	}
	/**
	 * Get the time of the action
	 * @return the time of the action
	 */
	public long getTimeOfAction() {
		return time.getTimeInMillis();
	}
	
	/**
	 * Adds seven days to the time of this action.
	 */
	public void addWeek() {
		time.add(Calendar.DAY_OF_MONTH, 7);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" off = ");
		sb.append(Arrays.toString(headsOff));
		sb.append(" on = ");
		sb.append(Arrays.toString(headsOn));
		sb.append(" time = ");
		sb.append(new Date(getTimeOfAction()));
		return sb.toString();
		
	}
}
