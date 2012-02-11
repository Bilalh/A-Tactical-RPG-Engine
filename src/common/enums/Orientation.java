package common.enums;

public enum Orientation {
	UP_TO_NORTH("North"), 
	UP_TO_EAST("East"), 
	UP_TO_SOUTH("South"),
	UP_TO_WEST("West"), 
	EMPTY("Empty");

	public final String description;
	Orientation(String description){
		this.description = description;
	}
	@Override
	public String toString() {
		return description;
	}
	
	
}