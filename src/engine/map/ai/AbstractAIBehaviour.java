package engine.map.ai;

import java.util.ArrayList;
import java.util.Comparator;

import engine.map.AIPlayer;
import engine.map.Map;
import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;

/**
 * Defines how the AI choses it's next target 
 * @author Bilal Hussain
 */
public abstract class AbstractAIBehaviour implements Comparator<IMutableMapUnit> {

	protected transient Map map;
	protected transient AIPlayer ai;
	protected transient MapPlayer player;
	
	// If true negate the result of the Comparator
	protected boolean negated;
	public AbstractAIBehaviour(){}
	
	public AbstractAIBehaviour(Map map, AIPlayer ai, MapPlayer player) {
		this.map = map;
		this.ai = ai;
		this.player = player;
	}

	@Override
	public abstract String toString();


	public void setMap(Map map) {
		this.map = map;
	}


	public void setAi(AIPlayer ai) {
		this.ai = ai;
	}


	public void setPlayer(MapPlayer player) {
		this.player = player;
	}


	public boolean isNegated() {
		return negated;
	}


	public void setNegated(boolean negated) {
		this.negated = negated;
	}

}
