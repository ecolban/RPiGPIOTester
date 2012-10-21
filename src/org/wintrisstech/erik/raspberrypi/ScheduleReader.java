package org.wintrisstech.erik.raspberrypi;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ScheduleReader {

	private static final TimeZone TZ = TimeZone.getTimeZone("PST");

	private final Calendar now = GregorianCalendar.getInstance();
	
	public static void main(String[] args) {
		
		ScheduleReader reader = new ScheduleReader();
		URL url = reader.getClass().getResource("Schedules.xml");
		List<GpioAction> list = reader.readFile(url);
		for(GpioAction action : list) {
			System.out.println(action);
		}
		System.out.println("NOW = " + new Date(TestTime.currentTimeMillis()));
	}

	public List<GpioAction> readFile(URL url) {
		List<GpioAction> result = new ArrayList<GpioAction>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(url);
			Element root = document.getRootElement();
			now.setTimeInMillis(TestTime.currentTimeMillis());
			for (@SuppressWarnings("unchecked")
			Iterator<Element> i = root.elementIterator("schedule"); i.hasNext();) {
				Element schedule = i.next();
				for (int day : getDaysOfWeek(schedule)) {
					if (day == 0) { // value 0 used for erroneous day
						continue;
					}
					for (@SuppressWarnings("unchecked")
					Iterator<Element> j = schedule.elementIterator("action"); j
							.hasNext();) {
						Element action = j.next();
						try {
							result.add(createGpioAction(day, action));
						} catch (ParseException e) {
							continue;
						} catch (NumberFormatException ex) {
							continue;
						}
					}
				}
			}
		} catch (DocumentException e) {
			System.out.println(e.getMessage());
		}
		Collections.sort(result);
		return result;
	}

	private GpioAction createGpioAction(int day, Element action)
			throws ParseException, NumberFormatException {
		// the ON heads
		String onValue = action.attributeValue("on");
		int[] on = null;
		if (onValue != null) {
			String[] onStrings = onValue.trim().split(", *");
			on = new int[onStrings.length];
			for (int k = 0; k < on.length; k++) {
				on[k] = Integer.parseInt(onStrings[k]);
			}
		} else {
			on = new int[0];
		}
		// the OFF heads
		String offValue = action.attributeValue("off");
		int off[] = null;
		if (offValue != null) {
			String[] offStrings = action.attributeValue("off").trim()
					.split(", *");
			off = new int[offStrings.length];
			for (int k = 0; k < off.length; k++) {
				off[k] = Integer.parseInt(offStrings[k]);
			}
		} else {
			off = new int[0];
		}
		assert on != null || off != null;
		// the time of the action
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TZ);
		Date d = dateFormat.parse(action.attributeValue("time"));
		Calendar timeOfDay = GregorianCalendar.getInstance();
		timeOfDay.setTime(d);
		Calendar time = (Calendar) now.clone();
		time.set(Calendar.HOUR_OF_DAY, timeOfDay.get(Calendar.HOUR_OF_DAY));
		time.set(Calendar.MINUTE, timeOfDay.get(Calendar.MINUTE));
		time.set(Calendar.SECOND, timeOfDay.get(Calendar.SECOND));
		time.set(Calendar.DAY_OF_WEEK, day);
		if (time.before(now)) {
			time.add(Calendar.DAY_OF_WEEK, 7);
		}
		// return a new GpioAction
		return new GpioAction(on, off, time);
	}

	private int[] getDaysOfWeek(Element schedule) {
		Attribute attr = schedule.attribute("day_of_week");
		if (attr == null) {
			return null;
		}
		String[] days = attr.getValue().trim().split(", *");
		int[] result = new int[days.length];
		for (int i = 0; i < days.length; i++) {
			try {
				result[i] = Integer.parseInt(days[i]);
			} catch (NumberFormatException ex) {
				result[i] = 0;
			}
		}
		return result;
	}

	
	

}
