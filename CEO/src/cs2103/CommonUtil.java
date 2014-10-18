package cs2103;

import cs2103.parameters.Parameter;

public class CommonUtil {
	public static void checkNullString(String str, HandledException.ExceptionType expectedException) throws HandledException{
		if (str == null) throw new HandledException(expectedException);
	}
	
	public static String[] splitFirstWord(String parameterString) throws HandledException{
		checkNullString(parameterString, HandledException.ExceptionType.INVALID_CMD);
		String[] result = new String[2];
		int splitIndex = parameterString.indexOf(' ');
		if (splitIndex == -1){
			result[0] = parameterString;
			result[1] = null;
		}else{
			result[0] = parameterString.substring(0, splitIndex).trim();
			result[1] = parameterString.substring(splitIndex).trim();
		}
		return result;
	}
	
	public static int parseIntegerParameter(String parameter) throws HandledException {
		checkNullString(parameter, HandledException.ExceptionType.INVALID_PARA);
		parameter = parameter.trim();
		if (parameter.matches("[0-9]+")){
			return Integer.parseInt(parameter);
		} else {
			return -1;
		}
	}
	
	public static String deleteLastChar(StringBuffer sb){
		if (sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} else {
			return null;
		}
	}
	
	public static void checkNullParameter(Parameter parameter, HandledException.ExceptionType expectedException) throws HandledException{
		if (parameter == null) throw new HandledException(expectedException);
	}
}
