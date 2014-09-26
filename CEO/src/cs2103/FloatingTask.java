package cs2103;

class FloatingTask extends Task {
	private String progress;
	private final static String INCOMPLETE="NEEDS-ACTION";
	private final static String IN_PROCESS="IN-PROCESS";
	private final static String COMPLETED="COMPLETED";
	
	public FloatingTask(String taskUID, String title, String progress) throws CEOException {
		super(taskUID, title);
		this.updateProgress(progress);
	}
	
	public String getProgress(){
		return this.progress;
	}
	
	public void updateProgress(String progress) throws CEOException{
		if (progress.equals(INCOMPLETE) || progress.equals(IN_PROCESS) || progress.equals(COMPLETED)){
			this.progress=progress;
		}else{
			throw new CEOException("Invalid Progress");
		}
	}
}
