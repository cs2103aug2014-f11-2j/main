package cs2103.parameters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Recur;

public class Recurrence implements Parameter {
	public static final String[] allowedLiteral = {"R", "reccuring", "recur"};
	public static final String type = "RECURRENCE";
	private static final long[] divisor = {31556951000L, 2591999000L, 604799000L, 86399000L, 3599000L};
	private final Recur recur;
	
	public Recurrence(Recur recur){
		this.recur = recur;
	}
	
	public Recur getValue(){
		return this.recur;
	}

	@Override
	public String getType() {
		return type;
	}
	
	public static Recurrence parse(String recurrenceString) throws HandledException{
		if (recurrenceString == null){
			return null;
		} else {
			return new Recurrence(stringToRecur(recurrenceString));
		}
	}
	
	public static Recurrence parse(long recurrenceLong) throws HandledException{
		return new Recurrence(longToRecur(recurrenceLong));
	}
	
	private static Recur stringToRecur(String recurrenceString) throws HandledException{
		System.out.println(recurrenceString);
		int interval = parseInterval(recurrenceString);
		if (interval < 0){
			throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
		} else if(interval == 0) {
			return null;
		} else {
			String frequency = parseFrequency(recurrenceString);
			Recur recur=new Recur();
			recur.setFrequency(frequency);
			recur.setInterval(interval);
			return recur;
		}
	}
	
	private static Recur longToRecur(long recurrenceLong) throws HandledException{
		if (recurrenceLong < divisor[4]){
			return null;
		} else {
			long ratio = 0L;
			long div = 0L;
			int i = 0;
			while (i < divisor.length && ratio == 0L){
				div = divisor[i];
				ratio = recurrenceLong/div;
				i++;
			}
			Recur recur=new Recur();
			recur.setFrequency(parseFrequency(div));
			recur.setInterval(parseInterval(ratio));
			return recur;
		}
	}
	
	private static int parseInterval(String recurrenceString){
		Pattern p = Pattern.compile("([0-9]+)");
		Matcher m = p.matcher(recurrenceString);
		if (m.find()){
			String interval = m.group(0);
			return Integer.parseInt(interval);
		} else {
			return -1;
		}
	}
	
	private static int parseInterval(long ratio){
		return (int) ratio;
	}
	
	private static String parseFrequency(String recurrenceString) throws HandledException{
		Pattern p = Pattern.compile("([A-Za-z]+)");
		Matcher m = p.matcher(recurrenceString);
		if (m.find()){
			String found=m.group(0);
			if (found.equalsIgnoreCase("h") || found.equalsIgnoreCase("hour") || found.equalsIgnoreCase("hours") || found.equalsIgnoreCase("hourly")){
				return Recur.HOURLY;
			} else if (found.equalsIgnoreCase("d") || found.equalsIgnoreCase("day") || found.equalsIgnoreCase("days") || found.equalsIgnoreCase("daily")){
				return Recur.DAILY;
			} else if (found.equalsIgnoreCase("w") || found.equalsIgnoreCase("week") || found.equalsIgnoreCase("weeks") || found.equalsIgnoreCase("weekly")){
				return Recur.WEEKLY;
			} else if (found.equalsIgnoreCase("m") || found.equalsIgnoreCase("month") || found.equalsIgnoreCase("months") || found.equalsIgnoreCase("monthly")){
				return Recur.MONTHLY;
			} else if (found.equalsIgnoreCase("y") || found.equalsIgnoreCase("year") || found.equalsIgnoreCase("years") || found.equalsIgnoreCase("yearly")){
				return Recur.YEARLY;
			} else {
				throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
			}
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
		}
	}
	
	private static String parseFrequency(long div) throws HandledException{
		if (div == divisor[0]){
			return Recur.YEARLY;
		} else if (div == divisor[1]){
			return Recur.MONTHLY;
		} else if (div == divisor[2]){
			return Recur.WEEKLY;
		} else if (div == divisor[3]){
			return Recur.DAILY;
		} else if (div == divisor[4]){
			return Recur.HOURLY;
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
		}
	}
}
