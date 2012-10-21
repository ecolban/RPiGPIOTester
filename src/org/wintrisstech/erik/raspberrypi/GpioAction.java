package org.wintrisstech.erik.raspberrypi;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class GpioAction implements Comparable<GpioAction>{

	private final int[] headsOn;
	private final int[] headsOff;
	private final Calendar time;
	
	public GpioAction(int[] headsOn, int[] headsOff, Calendar time) {
		this.headsOn = headsOn;
		this.headsOff = headsOff;
		this.time = time;
	}

	@Override
	public int compareTo(GpioAction o) {
		return time.compareTo(o.time);
	}

	public int[] getHeadsOn() {
		return headsOn;
	}
	public int[] getHeadsOff() {
		return headsOff;
	}
	
	public long getTimeOfAction() {
		return time.getTimeInMillis();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" off = ");
		sb.append(Arrays.toString(headsOff));
		sb.append(" on = ");
		sb.append(Arrays.toString(headsOn));
		sb.append(" time = ");
		sb.append(new Date(time.getTimeInMillis()));
		return sb.toString();
		
	}
}
