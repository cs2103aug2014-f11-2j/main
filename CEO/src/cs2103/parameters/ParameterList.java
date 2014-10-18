package cs2103.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cs2103.HandledException;

public class ParameterList {
	private Map<String, Parameter> parameterMap;
	
	public ParameterList(){
		parameterMap = new HashMap<String, Parameter>();
	}
	
	public <T extends Parameter> T getParameter(String typeString, Class<T> type) throws HandledException{
		if (this.parameterMap.containsKey(typeString)){
			Parameter parameter = this.parameterMap.get(typeString);
			assert(parameter != null);
			if (parameter.getClass() == type){
				return type.cast(parameter);
			} else {
				throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
			}
		} else {
			return null;
		}
	}
	
	public void addParameter(Parameter parameter){
		if (parameter != null){
			parameterMap.put(parameter.getType(), parameter);
		}
	}
	
	public void addAllParameters(ArrayList<Parameter> parameterList){
		if (parameterList != null){
			for (Parameter parameter:parameterList){
				if (parameter != null){
					this.addParameter(parameter);
				}
			}
		}
	}
	
	public Title getTitle() throws HandledException{
		return this.getParameter(Title.type, Title.class);
	}
	
	public Description getDescription() throws HandledException{
		return this.getParameter(Description.type, Description.class);
	}
	
	public Location getLocation() throws HandledException{
		return this.getParameter(Location.type, Location.class);
	}
	
	public Time getTime() throws HandledException{
		return this.getParameter(Time.type, Time.class);
	}
	
	public Complete getComplete() throws HandledException{
		return this.getParameter(Complete.type, Complete.class);
	}
	
	public Recurrence getRecurrence() throws HandledException{
		return this.getParameter(Recurrence.type, Recurrence.class);
	}
	
	public TaskID getTaskID() throws HandledException{
		return this.getParameter(TaskID.type, TaskID.class);
	}
	
	public TaskType getTaskType() throws HandledException{
		return this.getParameter(TaskType.type, TaskType.class);
	}
	
	public CommandType getCommandType() throws HandledException{
		return this.getParameter(CommandType.type, CommandType.class);
	}
	
	public Keyword getKeyword() throws HandledException{
		return this.getParameter(Keyword.type, Keyword.class);
	}
}
