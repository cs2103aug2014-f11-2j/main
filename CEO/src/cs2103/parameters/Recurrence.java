//@author A0116713M
package cs2103.parameters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs2103.exception.HandledException;
import cs2103.util.CommonUtil;
import net.fortuna.ical4j.model.Recur;

public class Recurrence implements Parameter {
	public static final String[] allowedLiteral = {"R", "reccuring", "recur"};
	public static final String type = "RECURRENCE";
	private final Recur recur;
	
	/**
	 * @param recur
	 */
	public Recurrence(Recur recur) {
		this.recur = recur;
	}
	
	/**
	 * @return Recur object of Recurrence
	 */
	public Recur getValue() {
		return this.recur;
	}

	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param recurrenceString
	 * @return Recurrence object of String recurrenceString
	 * @throws HandledException
	 */
	public static Recurrence parse(String recurrenceString) throws HandledException {
		if (recurrenceString == null) {
			return null;
		} else {
			return new Recurrence(stringToRecur(recurrenceString));
		}
	}
	
	/**
	 * @param recurrenceString
	 * @return Recur object from String recurrenceString, or null if String recurrenceString does not specify a correct recurring period
	 * @throws HandledException
	 */
	private static Recur stringToRecur(String recurrenceString) throws HandledException {
		int interval = parseInterval(recurrenceString);
		if (interval == 0) {
			return null;
		} else {
			String frequency = parseFrequency(recurrenceString);
			if (frequency == null) {
				return null;
			} else {
				Recur recur=new Recur();
				recur.setFrequency(frequency);
				recur.setInterval(interval);
				return recur;
			}
		}
	}
	
	/**
	 * @param recurrenceString
	 * @return int value of recurrence period, or 1 if recurrence period cannot be found
	 * @throws HandledException
	 */
	private static int parseInterval(String recurrenceString) throws HandledException {
		Pattern p = Pattern.compile("([0-9]+)");
		Matcher m = p.matcher(recurrenceString);
		if (m.find()) {
			recurrenceString = recurrenceString.substring(m.start()).trim();
			return CommonUtil.parseIntegerParameter(m.group(0));
		} else {
			return 1;
		}
	}
	
	/**
	 * @param recurrenceString
	 * @return String time counter(hourly, daily etc) for recurrence, or null if time counter cannot be found
	 * @throws HandledException
	 */
	private static String parseFrequency(String recurrenceString) throws HandledException {
		Pattern p = Pattern.compile("([A-Za-z]+)");
		Matcher m = p.matcher(recurrenceString);
		while (m.find()) {
			String found=m.group(0);
			if (found.equalsIgnoreCase("h") || found.equalsIgnoreCase("hour") || found.equalsIgnoreCase("hours") || found.equalsIgnoreCase("hourly")) {
				return Recur.HOURLY;
			} else if (found.equalsIgnoreCase("d") || found.equalsIgnoreCase("day") || found.equalsIgnoreCase("days") || found.equalsIgnoreCase("daily")) {
				return Recur.DAILY;
			} else if (found.equalsIgnoreCase("w") || found.equalsIgnoreCase("week") || found.equalsIgnoreCase("weeks") || found.equalsIgnoreCase("weekly")) {
				return Recur.WEEKLY;
			} else if (found.equalsIgnoreCase("m") || found.equalsIgnoreCase("month") || found.equalsIgnoreCase("months") || found.equalsIgnoreCase("monthly")) {
				return Recur.MONTHLY;
			} else if (found.equalsIgnoreCase("y") || found.equalsIgnoreCase("year") || found.equalsIgnoreCase("years") || found.equalsIgnoreCase("yearly")) {
				return Recur.YEARLY;
			}
		}
		return null;
	}
}
