package cs2103.parameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs2103.exception.HandledException;

public class Time implements Parameter {
	public static final String[] allowedLiteral = {"T", "time"};
	public static final String type = "TIME";
	private final Date[] time;
	
	public Time(Date[] time){
		this.time = time;
	}
	
	public Date[] getValue(){
		return this.time;
	}
	
	@Override
	public String getType() {
		return type;
	}

	public static Time parse(String timeString) throws HandledException{
		if (timeString == null){
			return null;
		} else {
			return new Time(parseTime(timeString));
		}
	}
	
	private static Date[] parseTime(String timeString) throws HandledException{
		Date[] time = new Date[2];
		time[0] = null; time[1] = null;
		if (timeString != null){
			Pattern p = Pattern.compile("\\d{4}/\\d{2}/\\d{2}\\s\\d{2}:\\d{2}");
			Matcher m = p.matcher(timeString);
			int i = 0;
			while(m.find() && i < 2){
				time[i] = stringToDate(m.group());
				i++;
			}
		}
		return time;
	}
	
	private static Date stringToDate(String timeString) throws HandledException{
		if (timeString == null) return null;
		try {
			TimeZone tz=TimeZone.getDefault();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			dateFormat.setTimeZone(tz);
			return dateFormat.parse(timeString);
		} catch (ParseException e) {
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		}
	}
}
