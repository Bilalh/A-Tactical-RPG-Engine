package common.enums;

public enum Orientation {
	TO_SOUTH("South",1),
	TO_NORTH("North",0),
	
	TO_WEST("West",3),
	TO_EAST("East",2), 
	 
	EMPTY("Empty",4);

	private final int inverse;
	public final String description;
	Orientation(String description,int inverse){
		this.description = description;
		this.inverse     = inverse;
	}
	@Override
	
	public String toString() {
		return description;
	}
	
	public Orientation inverse(){
		return values()[inverse];
	}
	
	
}