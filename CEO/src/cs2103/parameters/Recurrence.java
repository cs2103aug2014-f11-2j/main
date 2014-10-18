package cs2103.parameters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs2103.HandledException;
import net.fortuna.ical4j.model.Recur;

public class Recurrence implements Parameter {
	public static final String[] allowedLiteral = {"R", "reccuring", "recur"};
	public static final String type = "RECURRENCE";
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
	
	private static Recur stringToRecur(String recurrenceString) throws HandledException{
		Pattern p = Pattern.compile("([0-9]+)([hdwmy])");
		Matcher m = p.matcher(recurrenceString);
		if (m.find()){
			int interval=Integer.parseInt(m.group(1));
			String frequency;
			String found=m.group(2);
			if (found.equals("h")){
				frequency=Recur.HOURLY;
			} else if (found.equals("d")){
				frequency=Recur.DAILY;
			} else if (found.equals("w")){
				frequency=Recur.WEEKLY;
			} else if (found.equals("m")){
				frequency=Recur.MONTHLY;
			} else if (found.equals("y")){
				frequency=Recur.YEARLY;
			} else {
				throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
			}
			Recur recur=new Recur();
			recur.setFrequency(frequency);
			recur.setInterval(interval);
			return recur;
		} else if (recurrenceString.equals("0") || recurrenceString.equals("")){
			return null;
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
		}
	}
}
