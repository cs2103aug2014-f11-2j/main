//@author A0116713M
package cs2103.command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.fusesource.jansi.Ansi;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.ParameterList;
import cs2103.util.CommonUtil;

public abstract class Command {
	protected ParameterList parameterList = new ParameterList();
	/**
	 * @return Result Ansi to be printed
	 * @throws HandledException
	 * @throws FatalException
	 */
	public abstract Ansi execute() throws HandledException, FatalException;
	
	/**
	 * @param userInput
	 * @return Parameters and values in form of Queue<String>
	 * @throws HandledException
	 */
	protected static Queue<String> separateCommand(String userInput) throws HandledException {
		CommonUtil.checkNull(userInput, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> result = new LinkedList<String>();
		String[] parameters = userInput.trim().split("\\s+-");
		for (String s:parameters) {
			result.add(CommonUtil.removeDash(s.trim()));
		}
		return result;
	}
	
	/**
	 * @param parameterList
	 * @return Map<String,String> of parameters and values
	 * @throws HandledException
	 */
	protected static Map<String,String> separateParameters(Queue<String> parameterList) throws HandledException {
		CommonUtil.checkNull(parameterList, HandledException.ExceptionType.INVALID_PARA);
		Map<String,String> parameterMap = new HashMap<String, String>();
		while(!parameterList.isEmpty()) {
			String[] splitResult = CommonUtil.splitFirstWord(parameterList.poll());
			if (splitResult[0] != null) {
				parameterMap.put(splitResult[0], splitResult[1]);
			}
		}
		return parameterMap;
	}
	
	
	/**
	 * @param parameterMap
	 * @param allowedLiteral
	 * @return String value for respective parameters
	 */
	protected static String getParameterString(Map<String, String> parameterMap, String[] allowedLiteral) {
		for (String s:allowedLiteral) {
			String result = getParameterFromMap(parameterMap, s);
			if (result != null) return result;
		}
		return null;
	}
	
	private static String getParameterFromMap(Map<String, String> parameterMap, String parameterType) {
		if (parameterMap.containsKey(parameterType)) {
			String value=parameterMap.get(parameterType);
			if (value == null) {
				return "";
			}else{
				return value;
			}
		}else{
			return null;
		}
	}
	
	public ParameterList getParameterList() {
		return this.parameterList;
	}
}
