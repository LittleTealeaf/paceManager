package classes;

import java.util.ArrayList;
import java.util.List;

public class Time {
	/**
	 * Time in Seconds
	 */
	public float time;

	/**
	 * @Errors 0: No Error 1: Could not Parse 2: No Text
	 */
	public transient float error;

	public Time() {
		time = 0;
		error = 0;
	}

	public Time(float seconds) {
		time = seconds;
	}

	public Time(double seconds) {
		time = (float) seconds;
	}

	public Time(float hours, float minutes, float seconds) {
		time = (3600 * hours) + (60 * minutes) + seconds;
	}

	public Time(double hours, double minutes, double seconds) {
		time = (float) ((3600 * hours) + (60 * minutes) + seconds);
	}

	public Time(String par) {
		error = 0;
		if(par.contains("PM")) time = 43200;
		else time = 0;
		par = par.replace(" ", "").replace("AM", "").replace("PM", "");

		if(par.length() == 0) {
			time = 0;
			error = 2;
			return;
		}

		List<String> nums = new ArrayList<String>();
		String tmpAdd = "";
		for(char a : par.toCharArray()) {
			if(a == ':') {
				nums.add(tmpAdd);
				tmpAdd = "";
			} else tmpAdd += a;
		}
		nums.add(tmpAdd);

		if(nums.size() == 0) {
			time = 0;
			error = 2;
			return;
		} else {
			try {
				float coef = 1;
				for(int i = 0; i < nums.size(); i++) {
					time += coef * Float.parseFloat(nums.get(nums.size() - 1 - i));
					coef *= 60;
				}
			} catch(NumberFormatException e) {
				error = 1;
				time = 0;
			}

		}
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean twentyfour) {
		float second = time;
		int hour = (int) second / 3600;
		second -= hour * 3600;
		int minute = (int) second / 60;
		second -= minute * 60;
		String suf = "";
		if(!twentyfour && time < 86400) {
			suf = " AM";
			if(hour > 12) {
				hour -= 12;
			}
			if(time >= 43200) suf = " PM";
		}
		String retString = "";
		retString += String.format("%02d", hour);
		retString += ":";
		retString += String.format("%02d", minute);
		retString += ":";
		retString += String.format("%02d", (int) second);
		retString += suf;
		return(retString);
	}

	public boolean tEquals(Time t) {
		return time == t.time;
	}
}
