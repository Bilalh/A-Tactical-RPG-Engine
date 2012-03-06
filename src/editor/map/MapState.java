package editor.map;

public enum MapState {

	DRAW("Draw"),
	DRAW_INFO( "Draw with Current info"),
	ERASE("ERASE"),
	FILL("Fill"),
	EYE("Eye Dropper"),
	SELECTION("Select Area"),
	MOVE("Move"),
	PLACE("Place Unit"),
	LEFT_WALL("Draw the current Texture on the Left wall"),
	RIGHT_WALL("Draw the current Texture on the Right wall");


	public final String description;
	
	MapState(String description){
		this.description = description;
	}
	
}