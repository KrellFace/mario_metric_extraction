package metricExtraction;

import engine.core.MarioLevel;

public class MarioLevelWrapper extends MarioLevel {
	
	private String levelRep;
	
	public MarioLevelWrapper(String level, boolean visuals) {

		super(level, visuals);
		levelRep = level;
		
	}
	
	public MarioLevelWrapper clone() {
		MarioLevelWrapper level = (MarioLevelWrapper) super.clone();
		level.setStringRep(levelRep);
		return level;

	}
	
	public void setStringRep(String level) {
		levelRep = level;
	}
	
	public String getStringRep() {
		return levelRep;
	}

}
