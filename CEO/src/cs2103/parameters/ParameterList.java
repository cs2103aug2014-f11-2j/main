package cs2103.parameters;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.fortuna.ical4j.model.Recur;
import cs2103.HandledException;

public class ParameterList {
	private Map<String, Parameter> parameterMap;
	
	public ParameterList(){
		parameterMap = new HashMap<String, Parameter>();
	}
	
	public Parameter getParameter(String type){
		if (this.parameterMap.containsKey(type)){
			return this.parameterMap.get(type);
		} else {
			return null;
		}
	}
	
	public void addParameter(Parameter parameter){
		if (parameter != null){
			parameterMap.put(parameter.getType(), parameter);
		}
	}
	
	public void addAllParameter(ArrayList<Parameter> parameterList){
		for (Parameter parameter:parameterList){
			this.addParameter(parameter);
		}
	}
	
	public String getTitle() throws HandledException{
		Parameter parameter = this.parameterMap.get(Title.type);
		if (parameter instanceof Title){
			return ((Title) parameter).getTitle();
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		}
	}
	
	public String getDescription() throws HandledException{
		Parameter parameter = this.parameterMap.get(Description.type);
		if (parameter instanceof Description){
			return ((Description) parameter).getDescription();
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		}
	}
	
	public String getLocation() throws HandledException{
		Parameter parameter = this.parameterMap.get(Location.type);
		if (parameter instanceof Location){
			return ((Location) parameter).getLocation();
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		}
	}
	
	public Date[] getTime() throws HandledException{
		Parameter parameter = this.parameterMap.get(Time.type);
		if (parameter instanceof Time){
			return ((Time) parameter).getTime();
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		}
	}
	
	public boolean getComplete() throws HandledException{
		Parameter parameter = this.parameterMap.get(Complete.type);
		if (parameter instanceof Complete){
			return ((Complete) parameter).getComplete();
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		}
	}
	
	public Recur getRecurrence() throws HandledException{
		Parameter parameter = this.parameterMap.get(Recurrence.type);
		if (parameter instanceof Recurrence){
			return ((Recurrence) parameter).getRecurrence();
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		}
	}
	
	public int getTaskID() throws HandledException{
		Parameter parameter = this.parameterMap.get(TaskID.type);
		if (parameter instanceof TaskID){
			return ((TaskID) parameter).getTaskID();
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		}
	}
}
