package engine.ai;

import java.util.ArrayList;

import engine.Player;
import engine.map.IMutableMapUnit;
import engine.map.Map;
import engine.map.MapPlayer;
/**
 * @author bilalh
 */
public class AIPlayer extends MapPlayer {

	private Map map;
	
	public AIPlayer(Map map, ArrayList<IMutableMapUnit> units){
		super(units);
		this.map = map;
	}
	
	
	
}
