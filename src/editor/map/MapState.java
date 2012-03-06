package editor.map;

public enum MapState {

	DRAW("Draw"),
	DRAW_INFO( "Draw With Current info"),
	ERASE("ERASE"),
	FILL("FILL"),
	EYE("Eye Dropper"),
	SELECTION("Select Area"),
	MOVE("Move"),
	PLACE("Place Unit");
	
	public final String description;
	
	MapState(String description){
		this.description = description;
	}
	
}