package cs2103.parameters;

import cs2103.exception.HandledException;
import cs2103.util.CommonUtil;

public class Option implements Parameter {
	public static final String type = "OPTION";
	public static enum Value {
		DEFAULT, NOSYNC, TEST, SYNC;
	}
	private final Value value;
	
	public Option(Value value){
		this.value = value;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public Value getValue(){
		return this.value;
	}
	
	public static Option parse(String[] optionString) throws HandledException{
		return new Option(parseOption(optionString));
	}
	
	private static Value parseOption(String[] optionString) throws HandledException{
		if (optionString == null || optionString.length < 1){
			return Value.DEFAULT;
		} else {
			String option = CommonUtil.removeDash(optionString[0]);
			if (option.equalsIgnoreCase("nosync") || option.equalsIgnoreCase("no-sync") || option.equalsIgnoreCase("disable-sync") || option.equalsIgnoreCase("disable")){
				return Value.NOSYNC;
			} else if (option.equalsIgnoreCase("test")){
				return Value.TEST;
			} else if (option.equalsIgnoreCase("sync") || option.equalsIgnoreCase("enable-sync") || option.equalsIgnoreCase("enable")){
				return Value.SYNC;
			} else {
				return Value.DEFAULT;
			}
		}
	}
}
