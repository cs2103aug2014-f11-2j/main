//@author A0112673L
package cs2103.parameters;

import java.util.Date;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class Time implements Parameter {
	public static final String[] allowedLiteral = {"T", "time"};
	public static final String type = "TIME";
	private final Date[] time;
	
	public Time(Date[] time) {
		this.time = time;
	}
	
	/**
	 * @return Date Array from Time
	 */
	public Date[] getValue() {
		return this.time;
	}
	
	@Override
	public String getType() {
		return type;
	}

	/**
	 * @param timeString
	 * @return Time object from String timeString
	 */
	public static Time parse(String timeString) {
		if (timeString == null) {
			return null;
		} else {
			return new Time(parseTime(timeString));
		}
	}
	
	/**
	 * @param timeString
	 * @return Date Array from String timeString, contains only one Date object if timeString contains only one date(DeadlineTask),  
	 * contains two Date objects if timeString contains two dates(PeriodicTask)
	 */
	private static Date[] parseTime(String timeString) {
		Date[] time = new Date[2];
		if (timeString != null) {
			java.util.List<Date> dates = new PrettyTimeParser().parse(timeString);
			if (!dates.isEmpty()) {
				for (int i = 0;i < dates.size() && i < 2;i++){
					time[i] = dates.get(i);
				}
			}
		}
		return time;
	}
}
