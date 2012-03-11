package editor.map;

public enum MapState {

	DRAW("Draw"),
	DRAW_INFO( "Draw with Current info"),
	FILL("Fill"),
	EYE("Eye Dropper"),
	SELECTION("Select Area"),
	MOVE("Move"),
	PLACE("Place Enemy"),

	LEFT_WALL("Draw the current Texture on the Left wall"),
	RIGHT_WALL("Draw the current Texture on the Right wall"),
	
	START("Set the Player's start location");


	public final String description;
	
	MapState(String description){
		this.description = description;
	}
	
}