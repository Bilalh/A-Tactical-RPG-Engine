package editor;

/**
 * States for the editor
 * @author Bilal Hussain
 */
public enum State {

	DRAW("Draw"),
	DRAW_INFO( "Draw With Current info"),
	ERASE("ERASE"),
	FILL("FILL"),
	EYE("Eye Dropper"),
	SELECTION("Select Area"),
	MOVE("Move");
	
	public final String description;
	
	State(String description){
		this.description = description;
	}
	
}