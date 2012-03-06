package editor.map;

public enum MapState {

	DRAW("Draw"),
	DRAW_INFO( "Draw with Current info"),
	ERASE("ERASE"),
	FILL("Fill"),
	EYE("Eye Dropper"),
	SELECTION("Select Area"),
	MOVE("Move"),
	PLACE("Place Unit");
	
	public final String description;
	
	MapState(String description){
		this.description = description;
	}
	
}