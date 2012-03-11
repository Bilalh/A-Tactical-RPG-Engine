package common.enums;

public enum Orientation {
	TO_NORTH("North"), 
	TO_EAST("East"), 
	TO_SOUTH("South"),
	TO_WEST("West"), 
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